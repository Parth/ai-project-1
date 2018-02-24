import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;


public class Player {
	private Tuple origin;
	private Tuple destination;

	private int[][] search;
	public Cell[][] playerWorld;

	private Stack<Cell> path;

	private int counter = 0;
	public int bound;
	private boolean reached = false;

	private Sharable share;

	public Player(Tuple origin, Tuple destination, Sharable share) {
		this.origin = origin;
		this.destination = destination;
		this.share = share;

		bound = share.getBounds();

		search = new int[bound][bound];
		playerWorld = new Cell[bound][bound];

		path = null;

		for (int r = 0; r < bound; r++) {
			for (int c = 0; c < bound; c++) {
				if ( this.origin.x == r && this.origin.y == c ) {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.destination, 0);
				} else if ( this.destination.x == r && this.destination.y == c ) {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.destination, origin.compareTo(destination));
				} else {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.destination, -1);
				}
			}
		}
	}

	public Cell[][] step() {
		// give the player visibility of the 4 tiles around it from the world
		Tuple[] d = new Tuple[] {new Tuple(-1, 0), new Tuple(1, 0), new Tuple(0, 1), new Tuple(0, -1)};
		for (Tuple t : d) {
			Tuple neighborTuple = new Tuple(origin.x + t.x, origin.y + t.y);
			if (neighborTuple.x >= 0 && neighborTuple.y >= 0 && neighborTuple.x < bound && neighborTuple.y < bound) {
				Cell neighborCellWorld = share.getState(neighborTuple);
				Cell neighborCell = playerWorld[neighborTuple.x][neighborTuple.y];
				neighborCell.setGCost(neighborCellWorld.gCost);
				System.out.println("test: "+neighborCell.location);
				playerWorld[neighborTuple.x][neighborTuple.y] = neighborCell;
			}
		}

		boolean newPath = false;
			
		if (path != null) {
			
			// if a path was computed, follow it
			Cell newCell = path.pop();
			origin = newCell.location;

			// detect if the player reached the destination
			if (origin.equals(destination)) {
				reached = true;
				return playerWorld;
			}

			// detect if the path cost increased
			Stack<Cell> pathCopy = (Stack<Cell>)path.clone();
			while (!pathCopy.isEmpty()) {
				Cell pathCell = pathCopy.pop();
				Cell playerWorldCell = playerWorld[pathCell.location.x][pathCell.location.y];

				if (playerWorldCell.gCost == Integer.MAX_VALUE) {
					//one of the cells on the previously computed path is now seen to be blocked.
					newPath = true;
					break;
				}
			}
		} else {
			newPath = true;
		}

		if (newPath) {

			// path cost increased (detected a blockage along path), compute a new path
			counter++;

			// create start state
			Cell startCell = share.getState(origin);
			startCell.setGCost(0);
			search[origin.x][origin.y] = counter;

			// create goal state
			Cell goalCell = share.getState(destination);
			goalCell.setGCost(Integer.MAX_VALUE);
			search[destination.x][destination.y] = counter;

			Queue<Cell> open = new PriorityQueue<>();
			Set<Cell> closed = new HashSet<>();

			open.add(startCell);
			
			// A* search
			while (goalCell.gCost > open.peek().fCost) {
				Cell c = open.poll();
				closed.add(c);

				for (Cell successor : this.A(c)) {
					if (search(successor.location) < counter) {
						successor.setGCost(Integer.MAX_VALUE);
						this.search[successor.location.x][successor.location.y] = counter;
					}
					if (successor.gCost > c.gCost + 1) {
						successor.setGCost(c.gCost + 1);
						successor.setSearchTreeParent(c);
						open.remove(successor);
						open.add(successor);
					}
					this.playerWorld[successor.location.x][successor.location.y] = successor;
				}
			}

			path = new Stack<>();
			Cell iter = goalCell;
			while (!iter.equals(null)) {
				path.push(iter);
				iter = iter.searchTreeParent;
			}

			if (open.size() == 0) {
				//cant reach target
				reached = true;
				return null;
			}
		}

		return playerWorld;
	}

	// return a list of cells that can be traveled to based on available actions at Cell s
	private List<Cell> A(Cell s) {
		List<Cell> successors = new ArrayList<>();
		Tuple[] d = new Tuple[] {new Tuple(-1, 0), new Tuple(1, 0), new Tuple(0, 1), new Tuple(0, -1)};
		for (Tuple t : d) {
			Tuple neighborTuple = new Tuple(origin.x + t.x, origin.y + t.y);
			if (neighborTuple.x >= 0 && neighborTuple.y >= 0 && neighborTuple.x < bound && neighborTuple.y < bound) {
				Cell neighborCell = playerWorld[neighborTuple.x][neighborTuple.y];
				if (neighborCell.gCost < Integer.MAX_VALUE) {
					successors.add(neighborCell);
				}
			}
		}

		return successors;
	}

	private int search(Tuple s) {
		System.out.println(s);
		return search[s.x][s.y];
	}

	public boolean reached() {
		return reached;
	}
}
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
	private Cell[][] playerWorld;

	private Stack<Cell> path;

	private int counter = 0;
	public int bound;
	private boolean reached = false;

	private Sharable share;

	public Player(Tuple origin, Tuple destination, Sharable share) {
		this.origin = origin;
		this.destination = origin;
		this.share = share;

		search = new int[bound][bound];
		playerWorld = new Cell[bound][bound];

		path = null;

		bound = share.getBounds();

		for (int x = 0; x < bound; x++) {
			for (int y = 0; y < bound; y++) {
				playerWorld[x][y] = new Cell(origin, destination, Integer.MAX_VALUE);
			}
		}
	}

	public Cell[][] step() {
		// give the player visibility of the 4 tiles around it from the world
		int[] d = new int[] {-1, 1};
		for (int x : d) {
			for (int y : d) {
				Tuple neighborTuple = new Tuple(origin.x + x, origin.y + y);
				if (neighborTuple.x >= 0 && neighborTuple.y >= 0 && neighborTuple.x < bound && neighborTuple.y < bound) {
					Cell neighborCell = share.getState(neighborTuple);
					playerWorld[neighborTuple.x][neighborTuple.y] = neighborCell;
				}
			}
		}
			
		if (!(path.equals(null) || path.isEmpty())) {
			
			// if a path was computed, follow it
			Cell newCell = path.pop();
			origin = newCell.location;

			// detect if the player reached the destination
			if (origin.equals(destination)) {
				reached = true;
				return playerWorld;
			}
		}

		// detect if the path cost increased
		Stack<Cell> pathCopy = (Stack<Cell>)path.clone();
		boolean newPath = false;
		while (!pathCopy.isEmpty()) {
			Cell pathCell = pathCopy.pop();
			Cell playerWorldCell = playerWorld[pathCell.location.x][pathCell.location.y];

			if (playerWorldCell.gCost == Integer.MAX_VALUE) {
				//one of the cells on the previously computed path is now seen to be blocked.
				newPath = true;
				break;
			}
		}

		if (newPath) {

			//otherwise, compute a new path
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
		int[] d = new int[] {-1, 1};
		for (int x : d) {
			for (int y : d) {
				Tuple neighborTuple = new Tuple(origin.x + x, origin.y + y);
				if (neighborTuple.x >= 0 && neighborTuple.y >= 0 && neighborTuple.x < bound && neighborTuple.y < bound) {
					Cell neighborCell = playerWorld[neighborTuple.x][neighborTuple.y];
					if (neighborCell.gCost < Integer.MAX_VALUE) {
						successors.add(neighborCell);
					}
				}
			}
		}

		return successors;
	}

	private int search(Tuple s) {
		return search[s.x][s.y];
	}

	public boolean reached() {
		return reached;
	}
}
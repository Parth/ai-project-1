import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


public class Player {
	private Tuple origin;
	private Tuple destination;
	private boolean forward;

	private int[][] search;
	public Cell[][] playerWorld;

	private Stack<Cell> path;

	private List<Tuple> moveHistory;

	public int counter = 0;
	public int bound;
	private boolean reached = false;

	private Sharable share;

	// stats
	public int nodesGenerated;
	public boolean reachedTarget;

	public Player(Tuple origin, Tuple destination, Sharable share, boolean forward) {
		this.origin = origin;
		this.destination = destination;
		this.share = share;
		this.forward = forward;

		bound = share.getBounds();

		search = new int[bound][bound];
		playerWorld = new Cell[bound][bound];

		moveHistory = new LinkedList<>();

		path = null;

		for (int r = 0; r < bound; r++) {
			for (int c = 0; c < bound; c++) {
				if ( this.origin.x == r && this.origin.y == c ) {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.origin, this.destination, this.forward ? 0 : Integer.MAX_VALUE, this.forward);
				} else if ( this.destination.x == r && this.destination.y == c ) {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.origin, this.destination, this.forward ? Integer.MAX_VALUE : 0, this.forward);
				} else {
					playerWorld[r][c] = new Cell(new Tuple(r, c), this.origin, this.destination, -1, this.forward);
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
				if (neighborCellWorld.gCost == Integer.MAX_VALUE) {
					Cell neighborCell = playerWorld[neighborTuple.x][neighborTuple.y];
					neighborCell.setGCost(neighborCellWorld.gCost);
					playerWorld[neighborTuple.x][neighborTuple.y] = neighborCell;
				}
			}
		}

		boolean newPath = false;
			
		if (path != null) {
			// detect if the path cost increased
			Stack<Cell> pathCopy = (Stack<Cell>)path.clone();
			while (!pathCopy.isEmpty()) {
				Cell pathCell = pathCopy.pop();
				Cell playerWorldCell = playerWorld[pathCell.location.x][pathCell.location.y];

				if (this.forward) {
					if (!playerWorldCell.location.equals(destination) && playerWorldCell.gCost == Integer.MAX_VALUE) {
						System.out.println("increase in path cost");
						//one of the cells on the previously computed path is now seen to be blocked.
						newPath = true;
						break;
					}
				} else {
					if (!playerWorldCell.location.equals(origin) && playerWorldCell.gCost == Integer.MAX_VALUE) {
						System.out.println("increase in path cost");
						//one of the cells on the previously computed path is now seen to be blocked.
						newPath = true;
						break;
					}
				}
				
			}
			
			if (!newPath) {
				// if a path was computed, follow it
				Cell newCell = path.pop();
				origin = newCell.location;

				// {TODO} have to update the origin's g cost somehow?

				System.out.println("moved to: "+origin);
				moveHistory.add(newCell.location);

				// detect if the player reached the destination
				if (origin.equals(destination)) {
					reached = true;
					reachedTarget = true;
					System.out.println("reached target");
					System.out.print("move history: ");
					for (Tuple move : moveHistory) {
						System.out.print(move+" -> ");
					}
					System.out.println();
					return playerWorld;
				}
				return playerWorld;
			}
		} else {
			newPath = true;
		}

		if (newPath) {

			System.out.println("calculating new path");

			// path cost increased (detected a blockage along path), compute a new path
			counter++;

			// create start state
			Cell startCell = share.getState(origin);
			if(this.forward) {
				startCell.setGCost(0);
			} else {
				startCell.setGCost(Integer.MAX_VALUE);
			}
			playerWorld[origin.x][origin.y] = startCell;
			search[origin.x][origin.y] = counter;

			// create goal state
			Cell goalCell = share.getState(destination);
			if (this.forward) {
				goalCell.setGCost(Integer.MAX_VALUE);
			} else {
				goalCell.setGCost(0);
			}
			playerWorld[destination.x][destination.y] = goalCell;
			search[destination.x][destination.y] = counter;

			Queue<Cell> open = new PriorityQueue<>();
			Set<Cell> closed = new HashSet<>();

			if(this.forward) {
				open.add(startCell);
			} else {
				open.add(goalCell);
			}
			
			// A* search
			Tuple searchEndLocation = this.forward ? destination : origin;
			while (((Cell)this.playerWorld[searchEndLocation.x][searchEndLocation.y]).gCost > open.peek().fCost) {
				Cell first = open.poll();
				
				// tie-breaking

				List<Cell> tie = new LinkedList<>();
				tie.add(first);
				while(open.size() >=1 && open.peek().fCost == first.fCost) {
					tie.add(open.poll());
				}

				int minGCost = Integer.MAX_VALUE;
				int maxGCost = Integer.MIN_VALUE;
				Cell minGCostCell = first;
				Cell maxGCostCell = first;

				for (Cell t : tie) {
					if (t.gCost <= minGCost) {
						minGCost = t.gCost;
						minGCostCell = t;
					}
					if (t.gCost >= maxGCost) {
						maxGCost = t.gCost;
						maxGCostCell = t;
					}
				}

				// use max g cost
				Cell cell = maxGCostCell;

				// use min g cost
				//Cell cell = minGCostCell;

				// use arbitrary
				//Cell cell = first;

				// put cells not used back into open queue
				for (Cell t : tie) {
					if (!t.equals(cell)) {
						open.add(t);
					}
				}

				// end tie-breaking

				closed.add(cell);

				for (Cell successor : this.A(cell, closed)) {
					if (search(successor.location) < counter) {
						successor.setGCost(Integer.MAX_VALUE);
						this.search[successor.location.x][successor.location.y] = counter;
					}
					if (successor.gCost > cell.gCost + 1) {
						successor.setGCost(cell.gCost + 1);
						successor.setSearchTreeParent(cell);
						open.remove(successor);
						open.add(successor);
					}
					this.playerWorld[successor.location.x][successor.location.y] = successor;
				}

				if (open.size() == 0) {
					//cant reach target
					System.out.println("cant reach target");
					reachedTarget = false;
					reached = true;
					return playerWorld;
				}
			}

			if (this.forward) {
				path = new Stack<>();
				Cell iter = this.playerWorld[destination.x][destination.y];
				while (iter != null) {
					path.push(iter);
					iter = iter.searchTreeParent;
				}
			} else {
				path = new Stack<>();
				List<Cell> pathList = new ArrayList<>();
				Cell iter = this.playerWorld[origin.x][origin.y];
				while (iter != null) {
					pathList.add(iter);
					iter = iter.searchTreeParent;
				}

				//place path list items backwards
				ListIterator<Cell> listIter = pathList.listIterator(pathList.size());
				while (listIter.hasPrevious()){
					path.push(listIter.previous());
				}
			}

			Stack<Cell> pathCopy = (Stack<Cell>)path.clone();
			System.out.print("new path: ");
			while(!pathCopy.isEmpty()) {
				Cell c = pathCopy.pop();
				System.out.print(c.location+", ");
			}
			System.out.println();
		}

		return playerWorld;
	}

	// return a list of cells that can be traveled to based on available actions at Cell s
	private List<Cell> A(Cell s, Set<Cell> closed) {
		List<Cell> successors = new ArrayList<>();
		Tuple[] d = new Tuple[] {new Tuple(-1, 0), new Tuple(1, 0), new Tuple(0, 1), new Tuple(0, -1)};
		for (Tuple t : d) {
			Tuple neighborTuple = new Tuple(s.location.x + t.x, s.location.y + t.y);
			if (neighborTuple.x >= 0 && neighborTuple.y >= 0 && neighborTuple.x < bound && neighborTuple.y < bound) {
				Cell neighborCell = playerWorld[neighborTuple.x][neighborTuple.y];
				if (neighborCell.gCost == Integer.MAX_VALUE) {
					if (this.forward && neighborCell.location.equals(destination)) {
						successors.add(neighborCell);
					} else if (!this.forward && neighborCell.location.equals(origin)) {
						successors.add(neighborCell);
					}
				} else {
					if (!closed.contains(neighborCell)) {
						successors.add(neighborCell);
					}
				}
			}
		}

		nodesGenerated += successors.size();

		return successors;
	}

	private int search(Tuple s) {
		return search[s.x][s.y];
	}

	public boolean reached() {
		return reached;
	}
}
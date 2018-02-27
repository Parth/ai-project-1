import java.util.*;

public class World implements Sharable {
	
	private Cell[][] world;
	private int bound;
	private static Tuple[] direction = new Tuple[] {new Tuple(-1, 0), new Tuple(1, 0), new Tuple(0, 1), new Tuple(0, -1)};

	public Tuple origin;
	public Tuple destination;

	public World(int bound, boolean forward) {
		world = new Cell[bound][bound];

		origin = Tuple.generateRandomTuple(bound);
		destination = Tuple.generateRandomTuple(bound);
		while (origin.equals(destination)) {
			destination = Tuple.generateRandomTuple(bound);
		}

		this.bound = bound;

		populateWorld(forward);
	}

	public World(int bound, Cell[][] world, Tuple origin, Tuple destination) {
		this.bound = bound;
		this.world = world;
		this.origin = origin;
		this.destination = destination;
	}

	public World worldCopy(boolean forward) {
		Cell[][] newWorld = new Cell[this.bound][this.bound];
		Tuple newOrigin = new Tuple(this.origin.x, this.origin.y);
		Tuple newDestination = new Tuple(this.destination.x, this.destination.y);
		for (int r = 0; r < this.bound; r++) {
			for (int c = 0; c < newWorld[r].length; c++) {
				Cell oldCell = world[r][c];
				Cell newCell = new Cell(new Tuple(r, c), newOrigin, newDestination, oldCell.gCost, forward);

				newWorld[r][c] = newCell;
			}
		}

		return new World(this.bound, newWorld, newOrigin, newDestination);
	}

	public Cell[][] getWorld() {
		return world;
	}

	@Override 
	public Cell getState(Tuple t) {
		return world[t.x][t.y];
	}

	@Override
	public int getBounds() {
		return bound;
	}

	// DFS
	private void populateWorld(boolean forward) {
		Stack<Tuple> stack = new Stack<Tuple>();
		boolean[][] visited = new boolean[bound][bound];
		
		visited[origin.x][origin.y] = true;

		world[origin.x][origin.y] = new Cell(origin, origin, destination, 0, forward);	//set as unblocked
		stack.push(origin);
		
		while (true) {	// will break if all cells are visited
			while (!stack.isEmpty()) {
				Tuple cur = stack.pop();
				Tuple next = getUnvisitedNeighbor(cur, visited);
				if (next != null) {
					stack.push(cur);
					visited[next.x][next.y] = true;
					
					if ( (origin.x == next.x && origin.y == next.y) || (destination.x == next.x && destination.y == next.y) ) {
						// ensure that origin and destination are unblocked
						world[next.x][next.y] = new Cell(new Tuple(next.x, next.y), origin, destination, 0, forward);
					} else {
						world[next.x][next.y] = new Cell(new Tuple(next.x, next.y), origin, destination, (Math.random() < 0.3) ? Integer.MAX_VALUE : 0, forward);
					}
					if (world[next.x][next.y].gCost == 0) {	//if unblocked
						stack.push(next);
					}
				}
				
			}

			Tuple next = findUnvisitedInWorld(visited);
			if (next == null) {
				break;
			}
			visited[next.x][next.y] = true;
			world[next.x][next.y] = new Cell(next, origin, destination, 0, forward);	//set as unblocked
			stack.push(next);
			
		}
		print(world);
	}

	private Tuple getUnvisitedNeighbor(Tuple cur, boolean[][] visited) {
		List<Integer> unvisitedIndex = new ArrayList<Integer>();
		
		for (int i = 0; i < 4; i++) {
			int r = cur.x + direction[i].x;
			int c = cur.y + direction[i].y;

			if (r >= 0 && r < bound && c >= 0 && c < bound && !visited[r][c]) {
				unvisitedIndex.add(i);
			}
		}

		if (unvisitedIndex.isEmpty()) {
			return null;
		}		

		int idx = unvisitedIndex.get((int)(Math.random() * unvisitedIndex.size()));
		return new Tuple(cur.x + direction[idx].x, cur.y + direction[idx].y);
		
	}

	private Tuple findUnvisitedInWorld(boolean[][] visited) {
		for (int r = 0; r < bound; r++) {
			for (int c = 0; c < bound; c++) {
				if (!visited[r][c]) {
					return new Tuple(r, c);
				}
			}
		}

		return null;
	}

	public static void main(String[] args) throws InterruptedException {
		int bound = 101;
		boolean forward = true; // set to true for forward search, false for backward search
		boolean adaptive = true; // set to true for adaptive A* search

		if (!forward) {
			//adaptive search must be forward
			adaptive = false;
		}

		int numExperiments = 50;
		if (args.length >= 1) {
			bound = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			numExperiments = Integer.parseInt(args[1]);
		}
		int numSearches = 0;
		int numTotalNodesGenerated = 0;
		double totalRuntime = 0.0;
		int numSuccessfulSearches = 0;
		int numTotalNodesExpanded = 0;

		int numSearchesA = 0;
		int numTotalNodesGeneratedA = 0;
		double totalRuntimeA = 0.0;
		int numSuccessfulSearchesA = 0;
		int numTotalNodesExpandedA = 0;

		for (int i = 0; i < numExperiments; i++) {
			World w = new World(bound, forward);

			System.out.println(w.origin);
			System.out.println(w.destination);

			print(w.getWorld());
			
			Player p = new Player(w.origin, w.destination, w, forward, false);

			World worldCopy = w.worldCopy(forward);
			Player pA = new Player(worldCopy.origin, worldCopy.destination, worldCopy, forward, true);

			print(p.playerWorld);

			long startTime = System.currentTimeMillis();
			while (!p.reached()) {
				p.step();
			}
			long stopTime = System.currentTimeMillis();
			long runtime = stopTime - startTime;

			long startTimeA = System.currentTimeMillis();
			if (adaptive) {
				while (!pA.reached()) {
					pA.step();
				}
			}
			long stopTimeA = System.currentTimeMillis();
			long runtimeA = stopTimeA - startTimeA;

			totalRuntime += runtime;
			numTotalNodesGenerated += p.nodesGenerated;
			numTotalNodesExpanded += p.nodesExpanded;
			numSearches += p.counter;
			System.out.println("nodes generated: "+p.nodesGenerated);

			totalRuntimeA += runtimeA;
			numTotalNodesGeneratedA += pA.nodesGenerated;
			numTotalNodesExpandedA += pA.nodesExpanded;
			numSearchesA += pA.counter;

			if (p.reachedTarget) {
				numSuccessfulSearches++;
			}
			if (pA.reachedTarget) {
				numSuccessfulSearchesA++;
			}
		}
		
		System.out.println();
		System.out.println("Stats");
		System.out.println("----------------------");

		double avgNodesExpandedSearch = (numTotalNodesExpanded*1.0)/(numSearches*1.0);
		double avgNodesGeneratedSearch = (numTotalNodesGenerated*1.0)/(numSearches*1.0);
		double avgNodesGeneratedExperiments = (numTotalNodesGenerated*1.0)/(numExperiments*1.0);
		double avgRuntimeExperiments = totalRuntime/(numExperiments*1.0);
		double percentageSuccessfulSearches = (numSuccessfulSearches*1.0)/(numExperiments*1.0);

		System.out.println(numExperiments+" experiments with grid size "+bound+" running "+ (forward ? "forward" : "backward")+" "+(adaptive ? "adaptive" : ""));
		System.out.println("Total runtime: "+totalRuntime);
		System.out.println("Total nodes generated: "+numTotalNodesGenerated);
		System.out.println("Total nodes expanded: "+numTotalNodesExpanded);
		System.out.println("Total number of searches: "+numSearches);
		System.out.println("Average nodes generated per experiment: "+avgNodesGeneratedExperiments);
		System.out.println("Average nodes generated per A* search: "+avgNodesGeneratedSearch);
		System.out.println("Average nodes expanded per A* search: "+avgNodesExpandedSearch);
		System.out.println("Average runtime per experiment: "+avgRuntimeExperiments+" ms");
		System.out.println("Percentage of successful searches: "+percentageSuccessfulSearches);

		System.out.println();

		if (adaptive) {
			System.out.println("Adaptive search");
			System.out.println("-----------------------");
			double avgNodesExpandedSearchA = (numTotalNodesExpandedA*1.0)/(numSearchesA*1.0);
			double avgNodesGeneratedSearchA = (numTotalNodesGeneratedA*1.0)/(numSearchesA*1.0);
			double avgNodesGeneratedExperimentsA = (numTotalNodesGeneratedA*1.0)/(numExperiments*1.0);
			double avgRuntimeExperimentsA = totalRuntimeA/(numExperiments*1.0);
			double percentageSuccessfulSearchesA = (numSuccessfulSearchesA*1.0)/(numExperiments*1.0);

			System.out.println(numExperiments+" experiments with grid size "+bound+" running "+ (forward ? "forward" : "backward")+" "+(adaptive ? "adaptive" : ""));
			System.out.println("Total runtime: "+totalRuntimeA);
			System.out.println("Total nodes generated: "+numTotalNodesGeneratedA);
			System.out.println("Total nodes expanded: "+numTotalNodesExpandedA);
			System.out.println("Total number of searches: "+numSearchesA);
			System.out.println("Average nodes generated per experiment: "+avgNodesGeneratedExperimentsA);
			System.out.println("Average nodes generated per A* search: "+avgNodesGeneratedSearchA);
			System.out.println("Average nodes expanded per A* search: "+avgNodesExpandedSearchA);
			System.out.println("Average runtime per experiment: "+avgRuntimeExperimentsA+" ms");
			System.out.println("Percentage of successful searches: "+percentageSuccessfulSearchesA);
		}
	
	}

	public static void print(Cell[][] cells) {
		System.out.print("  ");
		for (int c = 0; c<cells[0].length; c++) { 
			System.out.print(c+" ");
		}
		System.out.println();
		for (int r = 0; r < cells.length; r++) {
			System.out.print(r+" ");
			for (int c = 0; c < cells[r].length; c++) {
				System.out.print(cells[r][c].toString() + ",");
			}
			System.out.println();
		}
		System.out.println();
	}
}

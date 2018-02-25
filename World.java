public class World implements Sharable {
	
	private Cell[][] world;
	private int bound;

	public Tuple origin;
	public Tuple destination;

	public World(int bound) {
		world = new Cell[bound][bound];

		origin = Tuple.generateRandomTuple(bound);
		destination = Tuple.generateRandomTuple(bound);
		while (origin.equals(destination)) {
			destination = Tuple.generateRandomTuple(bound);
		}

		this.bound = bound;

		for (int r = 0; r < bound; r++) {
			for (int c = 0; c < bound; c++) {
				if ( (origin.x == r && origin.y == c) || (destination.x == r && destination.y == c) ) {
					// ensure that origin and destination are unblocked
					world[r][c] = new Cell(new Tuple(r, c), destination, 0);
				} else {
					world[r][c] = new Cell(new Tuple(r, c), destination, (Math.random() < 0.3) ? Integer.MAX_VALUE : 0);
				}
			}
		}
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

	public static void main(String[] args) throws InterruptedException {
		int bound = 0;
		if (args.length < 1) {
			bound = 101;
		} else {
			bound = Integer.parseInt(args[0]);
		}

		World w = new World(bound);

		System.out.println(w.origin);
		System.out.println(w.destination);

		print(w.getWorld());
		
		Player p = new Player(w.origin, w.destination, w);

		print(p.playerWorld);

		while (!p.reached()) {
			print(p.step());
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

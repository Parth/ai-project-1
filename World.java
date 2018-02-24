public class World implements Sharable {
	
	private Cell[][] world;
	private int bound;

	public World(int bound, Tuple destination) {
		
		world = new Cell[bound][bound];

		this.bound = bound;

		for (int r = 0; r < bound; r++) {
			for (int c = 0; c < bound; c++) {
				world[r][c] = new Cell(new Tuple(c, r), destination, (Math.random() < 0.3) ? Integer.MAX_VALUE : -1);
			}
		}
	}

	public Cell[][] getWorld() {
		return world;
	}

	@Override 
	public Cell getState(Tuple t) {
		return world[t.y][t.x];
	}

	@Override
	public int getBounds() {
		return bound;
	}

	public static void main(String[] args) {
		int bound = Integer.parseInt(args[0]);
		bound = 101;

		Tuple origin = Tuple.generateRandomTuple(bound);
		Tuple destination = Tuple.generateRandomTuple(bound);

		World w = new World(bound, destination);

		print(w.getWorld());
		
		Player p = new Player(origin, destination, w);

		while (!p.reached()) {
			//print(p.step());
		}
	}

	public static void print(Cell[][] cells) {
		for (int r = 0; r < cells.length; r++) {
			for (int c = 0; c < cells[0].length; c++) {
				System.out.print(cells[r][c].toString() + ",");
			}
			System.out.println();
		}
	}
}

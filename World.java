public class World implements Sharable {
	
	private Cell[][] world;

	public World(int bound) {
		
	}

	@Override 
	public Cell getState(Tuple t) {
		return null;
	}

	public static void main(String[] args) {
		int bound = Integer.parseInt(args[0]);

		World w = new World(bound);

		Tuple origin = Tuple.generateRandomTuple(bound);
		Tuple destination = Tuple.generateRandomTuple(bound);
		
		Player p = new Player(origin, destination, w);

		while (!p.reached()) {
			print(p.step());
		}
	}

	public static void print(Cell[][] c) {

	}
}

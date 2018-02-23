public class World implements Sharable {
	
	private char[][] world;

	public World() {
		// Generate world here
	}

	@Override 
	public char getState(int x, int y) {
		return world[x][y];
	}

	public static void main(String[] args) {
		World w = new World();
		// Random origin and dest
		
		Player p = new Player(origin, destination, this);

		while (!p.reached()) {
			print(p.ste());
		}
	}

	public static void print(char[][] c) {

	}
}

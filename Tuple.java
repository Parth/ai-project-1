/**
 * https://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java
 */
public class Tuple { 
	public int x; 
	public int y; 

	public Tuple(int x, int y) { 
		this.x = x; 
		this.y = y; 
	} 

	public static Tuple generateRandomTuple(int bound) {
		int x = (int) (Math.random() * bound);
		int y = (int) (Math.random() * bound);
		return new Tuple(x, y);
	}

	public int compareTo(Tuple t) {
		return Math.abs(this.x - t.x) + Math.abs(this.y - t.y);
	}
} 

public class Player {
	private Tuple<Integer, Integer> origin;
	private Tuple<Integer, Integer> destination;

	private Sharable shar;

	public Player(Tuple<Integer, Integer> origin, Tuple<Integer, Integer> destination, Sharable share) {
		this.origin = origin;
		this.destination = origin;
		this.shar = share;
	}


	public char[][] step() {

	}

	public boolean reached() {
		return false;
	}
}

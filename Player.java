public class Player {
	private Tuple origin;
	private Tuple destination;

	private Sharable shar;

	public Player(Tuple origin, Tuple destination, Sharable share) {
		this.origin = origin;
		this.destination = origin;
		this.shar = share;
	}


	public char[][] step() {
		return null;
	}

	public boolean reached() {
		return false;
	}
}

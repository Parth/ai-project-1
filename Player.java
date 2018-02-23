public class Player {
	private Tuple origin;
	private Tuple destination;

	private Sharable share;

	public Player(Tuple origin, Tuple destination, Sharable share) {
		this.origin = origin;
		this.destination = origin;
		this.share = share;
	}


	public Cell[][] step() {
		return null;
	}

	public boolean reached() {
		return false;
	}
}

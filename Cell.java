public Cell {
	Tuple location;
	int fCost;
	int gCost;
	int hCost;

	public Cell(Tuple location, Tuple destination, int gCost) {
		hCost = location.compareTo(destination);
		this.gCost = gCost;

		fCost = hCost + gCost;
	}
}

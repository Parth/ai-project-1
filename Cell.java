public class Cell {
	Tuple location;
	Cell searchTreeParent;
	int fCost;
	int gCost;
	int hCost;

	public Cell(Tuple location, Tuple destination, int gCost) {
		hCost = location.compareTo(destination);
		this.gCost = gCost;

		fCost = (gCost == Integer.MAX_VALUE) ? Integer.MAX_VALUE : hCost + gCost;

		searchTreeParent = null;
	}

	public void setSearchTreeParent(Cell c) {
		searchTreeParent = c;
	}

	public void setGCost(int gCost) {
		this.gCost = gCost;
		this.fCost = hCost + gCost;
	}

	public String toString() {
		if (fCost == -1) {
			return "U";
		} else if (fCost == Integer.MAX_VALUE) {
			return "B";
		} else {
			return "" + fCost;
		}	
	}
}

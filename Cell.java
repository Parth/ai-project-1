public class Cell implements Comparable<Cell> {
	Tuple location;
	Cell searchTreeParent;
	int fCost;
	int gCost;
	int hCost;

	public Cell(Tuple location, Tuple origin, Tuple destination, int gCost, boolean forward) {
		this.location = location;
		if (forward) {
			hCost = location.compareTo(destination);
		} else {
			hCost = location.compareTo(origin);
		}
		
		this.gCost = gCost;

		fCost = (gCost == Integer.MAX_VALUE) ? Integer.MAX_VALUE : hCost + gCost;

		searchTreeParent = null;
	}

	public Cell(Tuple location, Cell searchTreeParent, int fCost, int gCost, int hCost) {
		this.location = location;
		this.searchTreeParent = searchTreeParent;
		this.fCost = fCost;
		this.gCost = gCost;
		this.hCost = hCost;
	}

	public void setSearchTreeParent(Cell c) {
		searchTreeParent = c;
	}

	public void setGCost(int gCost) {
		this.gCost = gCost;
		this.fCost = hCost + gCost;
	}

	//only used for adaptive A* search
	public void setHCost(int hCost) {
		this.hCost = hCost;
		this.fCost = hCost + gCost;
	}

	public String toString() {
		if (gCost == -1) {
			return "U";
		} else if (gCost == Integer.MAX_VALUE) {
			return "B";
		} else {
			return "" + gCost;
		}	
	}

	@Override
	public int compareTo(Cell other) {
		return this.fCost - other.fCost;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Cell) {
			Cell c = (Cell) o;
			if (this.location.equals(c.location)) {
				return true;
			}
		}
		return false;
	}
}

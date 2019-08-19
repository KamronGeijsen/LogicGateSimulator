package main.java;

import main.java.LogicUpdate.Logic;

public class Tile {
	static int chunkx, chunky;
	static int xchunks, ychunks;
	static Tile Close = null;

	void coords(int x, int y) {
		this.x = x;
		this.y = y;
		nx = x;
		ny = y;
	}

	void ncoords(int x, int y) {
		nx = x;
		ny = y;
	}
	
	boolean isInBox(int minx, int miny, int maxx, int maxy){
		return Active && x >= minx && y >= miny && x <= maxx && y <= maxy;
	}

	void dcoords() {
		drawx = x * Main.GridSize + Main.x;
		drawy = y * Main.GridSize + Main.y;
		drawnx = nx * Main.GridSize + Main.x;
		drawny = ny * Main.GridSize + Main.y;
	}
	public byte getID(){
		return 0;
	}
	public byte[] getMetadata2() {
		return null;
	}
	public int getIDCoords() {
		return (y+(1<<11))<<20 | (x+(1<<11))<<8 | getID()<<1 | (isOn?1:0);
	}
	
	// Tile close(int x, int y) {
	//
	// }

	int x, y;
	int nx, ny;

	int drawx, drawy;
	int drawnx, drawny;
	int img;

	boolean Active = false;
	boolean Pasted;
	boolean isSelected;
	boolean tempSel;
	boolean isOn;

	Logic logic = new Logic();
}

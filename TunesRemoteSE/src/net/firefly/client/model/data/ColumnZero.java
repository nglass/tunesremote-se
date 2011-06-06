package net.firefly.client.model.data;

import javax.swing.ImageIcon;

public class ColumnZero implements Comparable<ColumnZero> {
	private int row;
	private ImageIcon imageIcon;
	
	public ColumnZero(int row, ImageIcon ii) {
		this.row = row;
		this.imageIcon = ii;
	}
	
	public ImageIcon getImageIcon() {
		return this.imageIcon;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int compareTo(ColumnZero c) {
		if (this.row < c.getRow()) return -1;
		if (this.row > c.getRow()) return 1;
		return 0;
	}
}

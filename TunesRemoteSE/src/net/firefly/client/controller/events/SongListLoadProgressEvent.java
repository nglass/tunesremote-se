package net.firefly.client.controller.events;

import java.util.EventObject;

public class SongListLoadProgressEvent extends EventObject {

	private static final long serialVersionUID = 7920529847109298138L;

	private int songListSize = -1;
	
	public SongListLoadProgressEvent(int nbOfLoadedSongs) {
		super(new Integer(nbOfLoadedSongs));
	}
	
	public SongListLoadProgressEvent(int nbOfLoadedSongs, int songListSize) {
		super(new Integer(nbOfLoadedSongs));
		this.songListSize = songListSize;
	}

	public int getNumberOfLoadedSongs() {
		return ((Integer) source).intValue();
	}

	public int getSongListSize() {
		return songListSize;
	}
}

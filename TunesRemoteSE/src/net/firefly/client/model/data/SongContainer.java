package net.firefly.client.model.data;

public class SongContainer implements Cloneable {

	protected long containerId = 0;
	protected Song song = null;
	
	public long getContainerId() {
		return containerId;
	}

	public void setContainerId(long containerId) {
		this.containerId = containerId;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public SongContainer() {
		
	}
	
	public boolean equals(Object o) {
		SongContainer that;
		try {
			that = (SongContainer) o;
		} catch (Exception e) {
			return false;
		}
		if (o == null) {
			return false;
		}
		
		if (this.containerId == that.containerId &&
		    (this.song == that.song || 
			 (this.song != null && that.song != null &&
			  this.song.getDatabaseItemId() == that.song.getDatabaseItemId()))) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------ song-container---------------------\n");
		sb.append("song-container-id     : ").append(this.containerId).append("\n");
		sb.append("song                  :\n").append(this.song.toString());
		sb.append("-------------------------------------------------------\n");
		return sb.toString();
	}
	
	public Object clone(){
		SongContainer sc = new SongContainer();
		sc.containerId = containerId;
		sc.song = (Song) song.clone();

		return sc;
	}
	
}

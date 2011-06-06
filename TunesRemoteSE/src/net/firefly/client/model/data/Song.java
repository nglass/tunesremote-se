/*
 * This file is part of FireflyClient.
 *
 * FireflyClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * FireflyClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2007 Vincent Cariven
 */
package net.firefly.client.model.data;

import java.io.Serializable;

public class Song implements Cloneable, Serializable {

	private static final long serialVersionUID = 5882214037382735970L;

	protected long databaseItemId;

	protected boolean partOfACompilation;

	protected String genre;
	
	protected String artistAlbum;

	protected String sortArtist;
	
	protected String artist;
	
	protected String artistId;

	protected String album;
	
	protected String sortAlbum;
	
	protected String albumId;

	protected String year;

	protected String title;

	protected String discNumber;

	protected String trackNumber;

	protected long size;

	protected long time;
	
	protected String type;
	
	public Song() {
		this.partOfACompilation = false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------ song ------------------------\n");
		sb.append("database-item-id      : ").append(this.databaseItemId).append("\n");
		sb.append("part-of-a-compilation : ").append(this.partOfACompilation).append("\n");
		sb.append("genre                 : ").append(this.genre).append("\n");
		sb.append("artist-album          : ").append(this.artistAlbum).append("\n");
		sb.append("artist                : ").append(this.artist).append("\n");
		sb.append("sort-artist           : ").append(this.sortArtist).append("\n");
		sb.append("album                 : ").append(this.album).append("\n");
		sb.append("sort-album            : ").append(this.sortAlbum).append("\n");
		sb.append("year                  : ").append(this.year).append("\n");
		sb.append("title                 : ").append(this.title).append("\n");
		sb.append("disc-number           : ").append(this.discNumber).append("\n");
		sb.append("track-number          : ").append(this.trackNumber).append("\n");
		sb.append("size                  : ").append(this.size).append("\n");
		sb.append("time                  : ").append(this.time).append("\n");
		sb.append("type                  : ").append(this.type).append("\n");
		sb.append("-------------------------------------------------------\n");
		return sb.toString();
	}

	public boolean equals(Object o) {
		Song s;
		try {
			s = (Song) o;
		} catch (Exception e) {
			return false;
		}
		if (o == null) {
			return false;
		}
		if (this.databaseItemId == s.databaseItemId
				&& this.partOfACompilation == s.isPartOfACompilation()
				&& ((this.genre != null && this.genre.equals(s.getGenre())) || (this.genre == null && s.getGenre() == null))
				&& ((this.artist != null && this.artist.equals(s.getArtist())) || (this.artist == null && s.getArtist() == null))
				&& ((this.artistId != null && this.artist.equals(s.getArtistId())) || (this.artistId == null && s.getArtistId() == null))
				&& ((this.sortArtist != null && this.sortArtist.equals(s.getSortArtist())) || (this.sortArtist == null && s.getSortArtist() == null))
				&& ((this.album != null && this.album.equals(s.getAlbum())) || (this.album == null && s.getAlbum() == null))
				&& ((this.albumId != null && this.albumId.equals(s.getAlbumId())) || (this.albumId == null && s.getAlbumId() == null))
				&& ((this.sortAlbum != null && this.sortAlbum.equals(s.getSortAlbum())) || (this.sortAlbum == null && s.getSortAlbum() == null))
				&& ((this.year != null && this.year.equals(s.getYear())) || (this.year == null && s.getYear() == null))
				&& ((this.title != null && this.title.equals(s.getTitle())) || (this.title == null && s.getTitle() == null))
				&& ((this.type != null && this.type.equals(s.getType())) || (this.type == null && s.getType() == null))
				&& ((this.discNumber != null && this.discNumber.equals(s.getDiscNumber())) || (this.discNumber == null && s
						.getDiscNumber() == null))
				&& ((this.trackNumber != null && this.trackNumber.equals(s.getTrackNumber())) || (this.trackNumber == null && s
						.getTrackNumber() == null)) && this.size == s.getSize() && this.time == s.getTime()) {
			return true;
		}

		return false;
	}

	/**
	 * @return Returns the album.
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @param album
	 *            The album to set.
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	public String getSortAlbum() {
		return sortAlbum;
	}

	public void setSortAlbum(String sortAlbum) {
		this.sortAlbum = sortAlbum;
	}
	
	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	
	/**
	 * @return Returns the artist.
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            The artist to set.
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return Returns the artist.
	 */
	public String getArtistId() {
		return artistId;
	}

	/**
	 * @param artist
	 *            The artist to set.
	 */
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}
	
	/**
	 * @return Returns the artist.
	 */
	public String getSortArtist() {
		return sortArtist;
	}

	/**
	 * @param artist
	 *            The artist to set.
	 */
	public void setSortArtist(String sortArtist) {
		this.sortArtist = sortArtist;
	}
	
	/**
	 * @return Returns the databaseItemId.
	 */
	public long getDatabaseItemId() {
		return databaseItemId;
	}

	/**
	 * @param databaseItemId
	 *            The databaseItemId to set.
	 */
	public void setDatabaseItemId(long databaseItemId) {
		this.databaseItemId = databaseItemId;
	}

	/**
	 * @return Returns the discNumber.
	 */
	public String getDiscNumber() {
		return discNumber;
	}

	/**
	 * @param diskNumber
	 *            The diskNumber to set.
	 */
	public void setDiscNumber(String discNumber) {
		this.discNumber = discNumber;
	}

	/**
	 * @return Returns the partOfACompilation.
	 */
	public boolean isPartOfACompilation() {
		return partOfACompilation;
	}

	/**
	 * @param partOfACompilation
	 *            The partOfACompilation to set.
	 */
	public void setPartOfACompilation(boolean partOfACompilation) {
		this.partOfACompilation = partOfACompilation;
	}

	/**
	 * @return Returns the size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            The size to set.
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return Returns the time.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            The time to set.
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return Returns the trackNumber.
	 */
	public String getTrackNumber() {
		return trackNumber;
	}

	/**
	 * @param trackNumber
	 *            The trackNumber to set.
	 */
	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}

	/**
	 * @return Returns the year.
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year
	 *            The year to set.
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return Returns the artistAlbum.
	 */
	public String getArtistAlbum() {
		//if (this.artistAlbum != null && this.artistAlbum.trim().length() > 0) {
		//	return this.artistAlbum;
		//} else {
			return this.artistAlbum;
		//}
	}

	/**
	 * @param artistAlbum
	 *            The artistAlbum to set.
	 */
	public void setArtistAlbum(String artistAlbum) {
		this.artistAlbum = artistAlbum;
	}
	
	
	
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public Object clone(){
		Song s = new Song();
		s.album = album;
		s.sortAlbum = sortAlbum;
		s.artist = artist;
		s.artistAlbum = artistAlbum;
		s.sortArtist = sortArtist;
		s.databaseItemId = databaseItemId;
		s.discNumber = discNumber;
		s.genre = genre;
		s.partOfACompilation = partOfACompilation;
		s.size = size;
		s.time = time;
		s.title = title;
		s.type = type;
		return s;
	}
}
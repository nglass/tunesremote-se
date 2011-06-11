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
package net.firefly.client.controller;

import hirondelle.web4j.util.EscapeChars;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.Genre;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.list.AlbumList;
import net.firefly.client.model.data.list.ArtistList;
import net.firefly.client.model.data.list.GenreList;
import net.firefly.client.model.data.list.SongList;

public class ListManager {

	public static final int FLG_SEARCH_GENRE = 0x01;
	
	public static final int FLG_SEARCH_ARTIST = 0x02;

	public static final int FLG_SEARCH_ALBUM = 0x04;

	public static final int FLG_SEARCH_TITLE = 0x08;

	public static GenreList extractGenreList(SongList songList, Locale locale) {
		GenreList genreList = new GenreList(locale);
		Set<String> genreSet = new HashSet<String>();
		// -- simple string comparison when iterating over potentialy huge songs list
		Iterator<SongContainer> scit = songList.iterator();
		while (scit.hasNext()) {
			SongContainer sc = scit.next();
			Song s = sc.getSong();
			if (s != null && s.getGenre() != null) {
				genreSet.add(s.getGenre());
			}
		}
		
		// -- sorting with costly Genre comparison (collator based) with reduced extracted genre list
		SortedSet<Genre> genreSortedSet = new TreeSet<Genre>();
		Iterator<String> gsit = genreSet.iterator();
		while (gsit.hasNext()){
			genreSortedSet.add(new Genre(gsit.next()));
		}
		genreList.addAll(genreSortedSet);
		return genreList;
	}
	
	public static ArtistList extractArtistList(SongList songList, Locale locale) {
		ArtistList artistList = new ArtistList(locale);
		Set<Artist> artistSet = new HashSet<Artist>();
		Iterator<Artist> it;
		// -- simple string comparison when iterating over potentially huge songs list
		Iterator<SongContainer> scit = songList.iterator();
		while (scit.hasNext()) {
			SongContainer sc = scit.next();
			Song s = sc.getSong();
			if (s != null && s.getArtist() != null) {
				if (!s.isPartOfACompilation()) {
					Artist a = new Artist(s.getArtist(), s.getSortArtist(), s.getArtistId());
					artistSet.add(a);
				} else {
					artistList.addCompilation();
				}
			}
		}
		
		// -- sorting with costly Artist comparison (collator based) with reduced extracted artist list
		SortedSet<Artist> artistSortedSet = new TreeSet<Artist>();
		it = artistSet.iterator();
		while (it.hasNext()){
			artistSortedSet.add(it.next());
		}
		artistList.addAll(artistSortedSet);
		return artistList;
	}

	public static AlbumList extractAlbumList(SongList songList, Locale locale) {
		AlbumList albumList = new AlbumList(locale);
		Set<Album> albumSet = new HashSet<Album>();
		// -- simple string comparison when iterating over potentially huge songs list
		Iterator<SongContainer> scit = songList.iterator();
		while (scit.hasNext()) {
			SongContainer sc = scit.next();
			Song s = sc.getSong();
			if (s != null && s.getAlbum() != null) {
				albumSet.add(new Album(s.getAlbum(), s.getSortAlbum(), s.getAlbumId()));
			}
		}
		// -- sorting with costly Album comparison (collator based) with reduced extracted album list
		SortedSet<Album> albumSortedSet = new TreeSet<Album>();
		Iterator<Album> it = albumSet.iterator();
		while (it.hasNext()){
			albumSortedSet.add(it.next());
		}
		albumList.addAll(albumSortedSet);
		return albumList;
	}

	// -- flags: sum FLG_SEARCH_GENRE FLG_SEARCH_ARTIST, FLG_SEARCH_ALBUM, FLG_SEARCH_TITLE
	public static SongList filterList(SongList songList, String pattern, int flags) {
		SongList result = new SongList();
		String[] patternStrings = EscapeChars.forRegex(pattern).split("\\s");

		Pattern[] patterns = new Pattern[patternStrings.length];
		for (int i = 0; i < patternStrings.length; i++) {
			patterns[i] = Pattern.compile(patternStrings[i], Pattern.CASE_INSENSITIVE);
		}
		
		Iterator<SongContainer> it = songList.iterator();
		while (it.hasNext()) {
			SongContainer sc = it.next();
			Song s = sc.getSong();
			boolean matches = false;
			// -- test if some flag is set
			if (((flags & FLG_SEARCH_GENRE) > 0) && (s.getGenre() != null)) {
				boolean matches_genre = true;
				for (int i = 0; i < patterns.length; i++) {
					Matcher aMatcher = patterns[i].matcher(s.getGenre());
					matches_genre = matches_genre && aMatcher.find();
				}
				matches = matches || matches_genre;
			}
			if (((flags & FLG_SEARCH_ARTIST) > 0) && (s.getArtist() != null)) {
				boolean matches_artist = true;
				for (int i = 0; i < patterns.length; i++) {
					Matcher aMatcher = patterns[i].matcher(s.getArtist());
					matches_artist = matches_artist && aMatcher.find();
				}
				matches = matches || matches_artist;
			}
			if (((flags & FLG_SEARCH_ALBUM) > 0) && (s.getAlbum() != null)) {
				boolean matches_album = true;
				for (int i = 0; i < patterns.length; i++) {
					Matcher aMatcher = patterns[i].matcher(s.getAlbum());
					matches_album = matches_album && aMatcher.find();
				}
				matches = matches || matches_album;
			}
			if (((flags & FLG_SEARCH_TITLE) > 0) && (s.getTitle() != null)) {
				boolean matches_title = true;
				for (int i = 0; i < patterns.length; i++) {
					Matcher aMatcher = patterns[i].matcher(s.getTitle());
					matches_title = matches_title && aMatcher.find();
				}
				matches = matches || matches_title;
			}
			if (matches) {
				result.add(sc);
			}
		}
		return result;
	}

	public static SongList filterListByGenresAlbumsAndArtists(SongList songList, Genre[] genres, Album[] albums, Artist[] artists, boolean compilation) {
		SongList result = new SongList();
		
		Iterator<SongContainer> scit = songList.iterator();
		while (scit.hasNext()) {
			SongContainer sc = scit.next();
			Song s = sc.getSong();
			
			if (s == null) continue;
			
			boolean matchesGenre = false;
			boolean matchesAlbum = false;
			boolean matchesArtistOrCompilation = false;
			if (genres == null || genres.length == 0){
				matchesGenre = true;
			} else {
				for (int i = 0; i < genres.length; i++) {
					if (s.getGenre() != null && s.getGenre().equalsIgnoreCase(genres[i].getGenre())) {
						matchesGenre = true;
						break;
					}
				}
			}
			if (matchesGenre) {
				if (albums == null || albums.length == 0){
					matchesAlbum = true;
				} else {
					for (int i = 0; i < albums.length; i++) {
						if (s.getAlbum() != null && s.getAlbum().equalsIgnoreCase(albums[i].getAlbum())) {
							matchesAlbum = true;
							break;
						}
					}
				}
				if (matchesAlbum) {
					if (artists == null || artists.length == 0){
						if (compilation) {
							if (s.isPartOfACompilation()){
								matchesArtistOrCompilation = true;
							}
						} else {
							// no artist at all selected
							matchesArtistOrCompilation = true;
						}
					} else {
						if (compilation && s.isPartOfACompilation()){
							matchesArtistOrCompilation = true;
						} else {
							for (int i = 0; i < artists.length; i++) {
								if (s.getArtist() != null && s.getArtist().equalsIgnoreCase(artists[i].getArtist())) {
									matchesArtistOrCompilation = true;
									break;
								}
							}
						}
					}
				}
			}
			if (matchesGenre &&  matchesAlbum && matchesArtistOrCompilation){
				result.add(sc);
			}
		}
		return result;
	}
	
	public static SongList filterListExcludingFiletypes(SongList songList, Set<String> excludedFiletypes) {
		if (excludedFiletypes == null || excludedFiletypes.size() == 0){
			return (SongList)songList.clone();
		}
		SongList result = new SongList();
		
		Iterator<SongContainer> scit = songList.iterator();
		while (scit.hasNext()) {
			SongContainer sc = scit.next();
			Song s = sc.getSong();
			
			if (s == null) continue;
			
			if (s.getType() == null || s.getType().trim().length() == 0 || !excludedFiletypes.contains(s.getType())) {
				result.add(sc);
			}
		}
		return result;
	}

	public static int getFirstGenreIndexStartingWith(GenreList genreList, char c){
		return getFirstGenreIndexStartingWith(genreList, Character.toString(c));
	}
	
	public static int getFirstGenreIndexStartingWith(GenreList genreList, String s){
		Iterator<Genre> it = genreList.iterator();
		Genre g = null;
		int i = 0;
		while (it.hasNext()){
			g = it.next();
			if (g.getGenre() != null && g.getGenre().length() > 0 && !g.isSpecial()){
				if (g.getGenre().startsWith(s) || g.getGenre().toLowerCase().startsWith(s.toLowerCase())){
					return i;
				}
			}
			i++;
		}
		return -1;
	}
	
	public static int getFirstArtistIndexStartingWith(ArtistList artistList, char c){
		return getFirstArtistIndexStartingWith(artistList, Character.toString(c));
	}
	
	public static int getFirstArtistIndexStartingWith(ArtistList artistList, String s){
		Iterator<Artist> it = artistList.iterator();
		Artist a = null;
		int i = 0;
		while (it.hasNext()){
			a = it.next();
			if (a.getArtist() != null && a.getArtist().length() > 0 && !a.isSpecial()){
				if (a.getArtist().startsWith(s) || a.getArtist().toLowerCase().startsWith(s.toLowerCase())){
					return i;
				}
			}
			i++;
		}
		return -1;
	}
	
	public static int getFirstAlbumIndexStartingWith(AlbumList albumList, char c){
		return getFirstAlbumIndexStartingWith(albumList, Character.toString(c));
	}
	
	public static int getFirstAlbumIndexStartingWith(AlbumList albumList, String s){
		Iterator<Album> it = albumList.iterator();
		Album a = null;
		int i = 0;
		while (it.hasNext()){
			a = (Album)it.next();
			if (a.getAlbum() != null && a.getAlbum().length() > 0 && !a.isSpecial()){
				if (a.getAlbum().startsWith(s)|| a.getAlbum().toLowerCase().startsWith(s.toLowerCase())){
					return i;
				}
			}
			i++;
		}
		return -1;
	}

}

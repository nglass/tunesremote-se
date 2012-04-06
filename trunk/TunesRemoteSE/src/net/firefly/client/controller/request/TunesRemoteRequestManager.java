/*
 * This file is part of TunesRemote-SE.
 *
 * TunesRemote-SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote-SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package net.firefly.client.controller.request;

import java.net.URL;
import java.util.regex.Pattern;

import net.firefly.client.controller.events.SongListLoadProgressEvent;
import net.firefly.client.controller.listeners.SongListLoadProgressListener;
import net.firefly.client.model.data.Album;
import net.firefly.client.model.data.Artist;
import net.firefly.client.model.data.Database;
import net.firefly.client.model.data.Song;
import net.firefly.client.model.data.SongContainer;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.tools.FireflyClientException;

import org.tunesremote.TagListener;
import org.tunesremote.daap.RequestHelper;
import org.tunesremote.daap.Response;
import org.tunesremote.daap.ResponseParser;
import org.tunesremote.daap.Session;

import android.util.Log;

public class TunesRemoteRequestManager implements IRequestManager {

   public final static String TAG = TunesRemoteRequestManager.class.toString();
   public final static Pattern MLIT_PATTERN = Pattern.compile("mlit");

   private final Session session;
   private long basePlaylistId;
   private int trackCount;

   public void readBasePlaylist(TagListener listener) {
      Log.d(TAG, " in readBasePlaylist");
      try {
         byte[] raw = RequestHelper.request(String.format("%s/databases/%d/containers/%s/items?session-id=%s&meta=dmap.itemname,"
                  + "dmap.itemid,daap.songartist,daap.songartistid,daap.songalbumid,daap.songalbum,"
                  + "daap.songgenre,daap.songtime,daap.songyear,daap.songtracknumber,daap.songdiscnumber,"
                  + "dmap.containeritemid,daap.sortname,daap.sortartist,daap.sortalbum,daap.songcompilation,"
                  + "&query=('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32')", session.getRequestBase(), session.databaseId,
                  this.basePlaylistId, session.sessionId), false);

         // parse list, passing off events in the process
         ResponseParser.performSearch(raw, listener, MLIT_PATTERN, false);

      } catch (Exception e) {
         Log.w(TAG, "readPlaylists Exception:" + e.getMessage());
      }
   }

   private void readBasePlaylistInfo() {
      try {
         Response playlists = RequestHelper.requestParsed(String.format("%s/databases/%d/containers?session-id=%s&meta=dmap.itemname,"
                  + "dmap.itemcount,dmap.itemid,dmap.persistentid,daap.baseplaylist", session.getRequestBase(), session.databaseId, session.sessionId), false);

         for (Response resp : playlists.getNested("aply").getNested("mlcl").findArray("mlit")) {
            // abpl marks the base playlist
            if (resp.containsKey("abpl")) {
               this.basePlaylistId = resp.getNumberLong("miid");
            }

            // however we only want the music track count
            String name = resp.getString("minm");
            if (name.equals("Music")) {
               this.trackCount = (int) resp.getNumberLong("mimc");
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public TunesRemoteRequestManager(Session session) {
      this.session = session;
      readBasePlaylistInfo();
   }

   public int getTrackCount() {
      return this.trackCount;
   }

   @Override
   public boolean isAuthenticationRequired(String host, int port, String proxyHost, String proxyPort) throws FireflyClientException {
      return false;
   }

   @Override
   public boolean checkAuthentication(String host, int port, String proxyHost, String proxyPort, String password) throws FireflyClientException {
      return true;
   }

   @Override
   public Database getDatabase(String host, int port, String proxyHost, String proxyPort, String password) throws FireflyClientException {
      // 3 - load database
      Database database = null;

      return database;
   }

   private class TrackListener implements TagListener {

      protected int loadedSongsCount = 0;
      protected SongList songList;
      protected SongListLoadProgressListener listener;

      public TrackListener(SongList songList, SongListLoadProgressListener listener) {
         this.songList = songList;
         this.listener = listener;
      }

      @Override
      public void foundTag(String tag, Response resp) {
         // add a found search result to our list
         if (resp.containsKey("minm")) {
            Song song = new Song();
            SongContainer sc = new SongContainer();

            try {
               sc.setDatabaseId(session.databaseId);
               sc.setContainerId(resp.getNumberLong("mcti"));

               song.setDatabaseItemId(resp.getNumberLong("miid"));
               song.setTitle(resp.getString("minm"));

               String sortTitle = resp.getString("assn");
               if (sortTitle != null && sortTitle.length() > 0) {
                  song.setSortTitle(sortTitle);
               }

               song.setGenre(resp.getString("asgn"));
               song.setTime(resp.getNumberLong("astm"));
               song.setYear(resp.getNumberString("asyr"));
               song.setDiscNumber(resp.getNumberString("asdn"));
               song.setPartOfACompilation(resp.getNumberLong("asco") > 0);
               song.setTrackNumber(resp.getNumberString("astn"));

               String artistName = resp.getString("asar");
               String artistId = resp.getNumberString("asri");
               String sortArtist = resp.getString("assa");
               if (sortArtist == null || sortArtist.length() == 0) {
                  sortArtist = artistName;
               }
               Artist artist = new Artist(artistName, sortArtist, artistId);
               song.setArtist(artist);

               String albumName = resp.getString("asal");
               String albumId = resp.getNumberString("asai");
               String sortAlbum = resp.getString("assu");
               if (sortAlbum == null || sortAlbum.length() == 0) {
                  sortAlbum = albumName;
               }
               Album album = new Album(albumName, sortAlbum, albumId);
               song.setAlbum(album);

               sc.setSong(song);

               songList.add(sc);
               loadedSongsCount++;

               if (listener != null) {
                  listener.onProgressChange(new SongListLoadProgressEvent(loadedSongsCount));
               }
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }

      @Override
      public void searchDone() {
         Log.d(TAG, "Found " + loadedSongsCount + " tracks");
      }
   }

   @Override
   public SongList getSongList(String host, int port, String proxyHost, String proxyPort, String password) throws FireflyClientException {
      return getSongList(host, port, proxyHost, proxyPort, password, null);
   }

   @Override
   public SongList getSongList(String host, int port, String proxyHost, String proxyPort, String password, SongListLoadProgressListener listener)
            throws FireflyClientException {

      SongList songList = new SongList();
      TrackListener trackListener = new TrackListener(songList, listener);
      this.readBasePlaylist(trackListener);

      return songList;
   }

   @Override
   public void updateSongList(SongList songList, String host, int port, String proxyHost, String proxyPort, String password, long overDatabaseId)
            throws FireflyClientException {
      updateSongList(songList, host, port, proxyHost, proxyPort, password, overDatabaseId, null);
   }

   @Override
   public void updateSongList(SongList songList, String host, int port, String proxyHost, String proxyPort, String password, long overDatabaseId,
            SongListLoadProgressListener listener) throws FireflyClientException {

      System.err.println("updateSongList not implemented");
   }

   @Override
   public URL getSongStreamURL(String host, int port, int itemId) throws FireflyClientException {
      throw new FireflyClientException("Not a streaming server");
   }

   @Override
   public boolean getUseHttpCompressionWhenPossible() {
      return false;
   }

   @Override
   public void setUseHttpCompressionWhenPossible(boolean use) {
   }
}
/*
 * This file is part of TunesRemote SE.
 *
 * TunesRemote SE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * TunesRemote SE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package org.tunesremote_se;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.firefly.client.controller.request.PlaylistRequestManager;
import net.firefly.client.controller.request.TunesRemoteRequestManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.DatabaseDialog;
import net.firefly.client.gui.swing.frame.ClientFrame;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.player.MediaPlayer;

import org.libtunesremote_se.CloseOnLastWindow;
import org.libtunesremote_se.LibraryDetails;
import org.tunesremote.daap.Session;

public class TunesRemoteSession {

   private final ClientFrame client;
   private final Configuration config;
   private final Context context;
   private final MediaPlayer mediaPlayer;
   private final LibraryInfo libraryInfo;
   protected TunesRemoteRequestManager mainRequestManager;
   protected PlaylistRequestManager playlistRequestManager;
   private final Session session;

   private void close() {
      client.setVisible(false);
      client.updateConfigOnClose();
      mediaPlayer.close();
      client.dispose();
      CloseOnLastWindow.unregisterWindow();
   }

   public TunesRemoteSession(LibraryDetails library, Session session) {
      this.config = Configuration.getInstance();
      this.context = new Context(this.config);

      this.context.setSession(session);

      this.session = session;

      this.mediaPlayer = new MediaPlayer(context, session);
      this.context.setPlayer(this.mediaPlayer);

      // convert library details for firefly
      this.libraryInfo = new LibraryInfo();
      libraryInfo.setHost(library.getAddress());
      libraryInfo.setPort(library.getPort());
      libraryInfo.setSongCount(0);
      libraryInfo.setLibraryName(library.getLibraryName());
      libraryInfo.setLibraryId(library.getLibrary());

      this.context.setLibraryInfo(libraryInfo);

      this.mainRequestManager = new TunesRemoteRequestManager(this.session);
      this.context.setMainRequestManager(mainRequestManager);
      this.playlistRequestManager = new PlaylistRequestManager(this.context, this.session);
      this.context.setPlaylistRequestManager(this.playlistRequestManager);

      client = new ClientFrame(context);
      client.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            close();
         }
      });
      CloseOnLastWindow.registerWindow();
      client.setVisible(true);

      // 3.2 - update LibraryInfo object
      context.getLibraryInfo().setSongCount(this.mainRequestManager.getTrackCount());
      context.setServerVersion(library.getLibraryType());

      DatabaseDialog nextDialog = new DatabaseDialog(context, client);
      nextDialog.setVisible(true);
   }
}

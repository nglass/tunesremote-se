package org.tunesremote_se;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.libtunesremote_se.CloseOnLastWindow;
import org.libtunesremote_se.LibraryDetails;
import org.tunesremote.daap.Session;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.controller.request.PlaylistRequestManager;
import net.firefly.client.controller.request.TunesRemoteRequestManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.DatabaseDialog;
import net.firefly.client.gui.swing.frame.ClientFrame;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.player.MediaPlayer;
import net.firefly.client.tools.FireflyClientException;

public class TunesRemoteSession {
	
	private ClientFrame client;
	private Configuration config;
	private Context context;
	private MediaPlayer mediaPlayer;
	private LibraryInfo libraryInfo;
	protected TunesRemoteRequestManager mainRequestManager;
	protected PlaylistRequestManager playlistRequestManager;
	private Session session;
	
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

		this.mainRequestManager = new TunesRemoteRequestManager (this.session);
		this.context.setMainRequestManager(mainRequestManager);
		this.playlistRequestManager = new PlaylistRequestManager(this.context, this.session);
		this.context.setPlaylistRequestManager(this.playlistRequestManager);
		
		client = new ClientFrame(context);
		client.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		CloseOnLastWindow.registerWindow();
		client.setVisible(true);

		// 3.2 - update LibraryInfo object
		context.getLibraryInfo().setSongCount(this.mainRequestManager.getTrackCount());
		context.setServerVersion("ITunes");
	
		DatabaseDialog nextDialog = new DatabaseDialog(context, client);
		nextDialog.setVisible(true);
	}
}

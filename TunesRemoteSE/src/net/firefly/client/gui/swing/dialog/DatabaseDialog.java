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
package net.firefly.client.gui.swing.dialog;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.firefly.client.controller.LibraryManager;
import net.firefly.client.controller.ListManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.controller.events.SongListLoadProgressEvent;
import net.firefly.client.controller.listeners.SongListLoadProgressListener;
import net.firefly.client.controller.request.IRequestManager;
import net.firefly.client.controller.request.PlaylistRequestManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.other.LabeledProgressBar;
import net.firefly.client.gui.swing.other.SplashScreen;
import net.firefly.client.model.data.list.AlbumList;
import net.firefly.client.model.data.list.ArtistList;
import net.firefly.client.model.data.list.GenreList;
import net.firefly.client.model.data.list.SongList;
import net.firefly.client.model.library.Library;
import net.firefly.client.model.library.LibraryInfo;
import net.firefly.client.model.playlist.list.PlaylistList;
import net.firefly.client.tools.FireflyClientException;

public class DatabaseDialog extends JDialog implements SongListLoadProgressListener {

	private static final long serialVersionUID = -3947598961831559192L;

	protected String DIALOG_TITLE;;

	// -- ATTRIBUTE(S)
	protected Context context;

	protected Frame rootContainer;

	protected int cachedSongCount;

	protected IRequestManager requestManager;
	
	protected PlaylistRequestManager playlistRequestManager;

	protected LabeledProgressBar progressBar;

	// -- CONSTRUCTOR(S)

	public DatabaseDialog(Context context, Frame rootContainer, int cachedSongCount) {
		super(rootContainer, true);
		this.cachedSongCount = cachedSongCount;
		this.context = context;
		this.rootContainer = rootContainer;
		this.DIALOG_TITLE = ResourceManager.getLabel("dialog.library.load.title", context.getConfig().getLocale());
		this.requestManager = context.getMainRequestManager();
		this.requestManager.setUseHttpCompressionWhenPossible(context.getConfig().isHttpCompressionEnabled());
		this.playlistRequestManager = context.getPlaylistRequestManager();
		this.playlistRequestManager.setUseHttpCompressionWhenPossible(context.getConfig().isHttpCompressionEnabled());
		initialize();
	}

	public DatabaseDialog(Context context, Frame rootContainer) {
		this(context, rootContainer, 0);
	}

	// -- METHOD(S)
	protected void initialize() {
		// -- title
		setTitle(DIALOG_TITLE);

		// -- progress bar
		progressBar = new LabeledProgressBar(0, context.getLibraryInfo().getSongCount());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		// -- labels
		JLabel libraryNameLabel = new JLabel(ResourceManager.getLabel("dialog.library.load.library.name", context
				.getConfig().getLocale()));
		JLabel libraryNameLabelValue = new JLabel("<html><b>" + context.getLibraryInfo().getLibraryName());

		JLabel itemsCountLabel = new JLabel(ResourceManager.getLabel("dialog.library.load.song.count", context
				.getConfig().getLocale()));
		String countString = "<html><b>" + context.getLibraryInfo().getSongCount() + "</b> ";
		if (cachedSongCount > 0) {
			countString += ResourceManager.getLabel("dialog.library.load.remote", context.getConfig().getLocale());
		}

		JLabel itemsCountLabelValue = new JLabel(countString);

		Component box = Box.createHorizontalGlue();
		JLabel itemsCountLocalValue = new JLabel("<html><b>" + cachedSongCount + "</b> "
				+ ResourceManager.getLabel("dialog.library.load.local", context.getConfig().getLocale()));
		if (cachedSongCount != context.getLibraryInfo().getSongCount()) {
			itemsCountLocalValue.setText(itemsCountLocalValue.getText() + " <b>"
					+ ResourceManager.getLabel("dialog.library.load.not.up.to.date", context.getConfig().getLocale())
					+ "</b>");
		} else {
			itemsCountLocalValue.setText(itemsCountLocalValue.getText() + " "
					+ ResourceManager.getLabel("dialog.library.load.up.to.date", context.getConfig().getLocale()));
		}

		// -- add elements in the layout
		int margin = 15;
		
		// -- infos
		GridBagConstraints libraryNameLabelGBC = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(margin, margin, 2, 5), 1, 1);
		GridBagConstraints libraryNameLabelValueGBC = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(margin, 5, 2, margin), 1, 1);

		GridBagConstraints itemsCountLabelGBC = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(2, margin, 2, 5), 1, 1);
		GridBagConstraints itemsCountLabelValueGBC = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, margin), 1, 1);

		GridBagConstraints itemsCountLocalBoxGBC = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(2, margin, 2, 5), 1, 1);
		GridBagConstraints itemsCountLocalValueGBC = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, margin), 1, 1);

		// -- progress bar
		GridBagConstraints progressBarGBC = new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(15, margin, 2, margin), 1, 1);

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(libraryNameLabel, libraryNameLabelGBC);
		getContentPane().add(libraryNameLabelValue, libraryNameLabelValueGBC);

		getContentPane().add(itemsCountLabel, itemsCountLabelGBC);
		getContentPane().add(itemsCountLabelValue, itemsCountLabelValueGBC);

		if (cachedSongCount > 0) {
			getContentPane().add(box, itemsCountLocalBoxGBC);
			getContentPane().add(itemsCountLocalValue, itemsCountLocalValueGBC);
		}
		getContentPane().add(progressBar, progressBarGBC);

		// -- close/submission behaviour
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addKeyListener(new EscapeKeyListener());

		// -- center dialog when shown / manage field selection
      SplashScreen.close();
      try {
         setUndecorated(false);
      } catch (Throwable ignore){}
      center();

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            loadRemoteSongList();
         }
      });
	}

	// -- center the dialog
	protected void center() {
		pack();
		Rectangle rcRect = rootContainer.getBounds();
		int x = rcRect.x + (rcRect.width / 2) - (getWidth() / 2), y = rcRect.y + (rcRect.height / 2) - (getHeight() / 2);
		setBounds(x, y, getWidth(), getHeight());
		validate();
	}

	protected void enableDialog(boolean enable) {
		if (enable) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			setTitle(DIALOG_TITLE);
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else {
			setTitle(DIALOG_TITLE + " - "
					+ ResourceManager.getLabel("dialog.library.load.loading", context.getConfig().getLocale()));
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}

	// -- INNER CLASS(ES)

	class CancelButtonActionListener implements ActionListener {
		protected JDialog dialog;

		public CancelButtonActionListener(JDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			context.setLibraryInfo(null);
			dialog.dispose();
		}
	}

	class UpdateSongListButtonActionListener implements ActionListener {
		protected DatabaseDialog dialog;

		public UpdateSongListButtonActionListener(DatabaseDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			// -- action thread
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						if (progressBar != null) {
							progressBar.setMaximum(cachedSongCount);
						}

						// 1 - Get the local song list
						SongList songList = null;
						try {
							progressBar.setAdditionalLabel(ResourceManager.getLabel(
									"dialog.library.progress.loading.local", context.getConfig().getLocale()));
							Library library = LibraryManager.loadSavedLibrary(context.getConfig().getConfigRootDirectory(), context.getLibraryInfo().getLibraryId(),
									dialog);
							songList = library.getSongList();
						} catch (FireflyClientException ex) {
							dialog.enableDialog(true);
							progressBar.setIndeterminate(false);
							ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
									"dialog.library.load.error.message", context.getConfig().getLocale(),
									new String[] { ex.getMessage() }), ResourceManager.getLabel(
									"dialog.library.load.error.title", context.getConfig().getLocale()), ex, context
									.getConfig().getLocale());
							return;
						}
						
						// 2 - update the songlist
						try {
							progressBar.setValue(0);
							progressBar.setAdditionalLabel(ResourceManager.getLabel(
									"dialog.library.progress.loading.update", context.getConfig().getLocale()));
							requestManager.updateSongList(songList, context.getLibraryInfo().getHost(), context
									.getLibraryInfo().getPort(), "", "", context.getLibraryInfo().getPassword(), songList.getMaxDatabaseId(), dialog);
							
							// 3 - update the context
							context.setMasterSongList(songList);
							context.setGlobalSongList(songList);
							context.setGlobalGenreList(ListManager.extractGenreList(context.getGlobalSongList(), context
									.getConfig().getLocale()));
							context.setGlobalArtistList(ListManager.extractArtistList(context.getGlobalSongList(), context
									.getConfig().getLocale()));
							context.setGlobalAlbumList(ListManager.extractAlbumList(context.getGlobalSongList(), context
									.getConfig().getLocale()));

							context.setFilteredGenreList((GenreList) context.getGlobalGenreList().clone());
							context.setFilteredArtistList((ArtistList) context.getGlobalArtistList().clone());
							context.setFilteredSongList((SongList) context.getGlobalSongList().clone());
							context.setFilteredAlbumList((AlbumList) context.getGlobalAlbumList().clone());
							
							// 4 - get playlist list
							PlaylistList playlists = playlistRequestManager.getPlaylistList(context.getSession().databaseId, songList);
							context.setPlaylists(playlists);
							
							// 5 - auto save updated library
							Thread t = new Thread(new Runnable(){
								public void run() {
									Library library = new Library();
									library.setLibraryInfo(context.getLibraryInfo());
									library.setSongList(context.getMasterSongList());
									try {
										LibraryManager.saveLibrary(context.getConfig().getConfigRootDirectory(), library, true);
									} catch (FireflyClientException ex) {
										ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("dialog.save.library.error.message", context
												.getConfig().getLocale(), new String[] { ex.getMessage() }), ResourceManager.getLabel(
												"dialog.save.library.error.title", context.getConfig().getLocale()), ex, context.getConfig()
												.getLocale());
										return;
									}
								}
							});
							t.start();
							

						} catch (FireflyClientException ex) {
							dialog.enableDialog(true);
							progressBar.setIndeterminate(false);
							ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
									"dialog.library.load.error.message", context.getConfig().getLocale(),
									new String[] { ex.getMessage() }), ResourceManager.getLabel(
									"dialog.library.load.error.title", context.getConfig().getLocale()), ex, context
									.getConfig().getLocale());
							return;
						}

						// 5 - restore dialog title / hide dialog
						dialog.enableDialog(true);
						dialog.setVisible(false);
					} catch (Throwable t) {
						dialog.enableDialog(true);
						ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
								"dialog.library.load.unexpected.error.message", context.getConfig().getLocale(),
								new String[] { ((t.getMessage() != null) ? " (" + t.getMessage() + ")." : ".") }),
								ResourceManager.getLabel("dialog.library.load.unexpected.error.title", context
										.getConfig().getLocale()), t, context.getConfig().getLocale());
						progressBar.setIndeterminate(false);
						progressBar.setValue(0);
						return;
					} finally {
						context.setAutoload(false);
					}
				}
			});
			// 1 - Change dialog appearance
			progressBar.setIndeterminate(true);
			dialog.enableDialog(false);
			t.start();

		}
	}

	public void loadRemoteSongList() {
	   final DatabaseDialog dialog = this;
	   
      // -- action thread
      Thread t = new Thread(new Runnable() {
         public void run() {
            try {
               // 0 - ask confirmation if it seems to be up-to-date
               if (cachedSongCount == context.getLibraryInfo().getSongCount()) {
                  int confirm = JOptionPane.showConfirmDialog(rootContainer, ResourceManager.getLabel(
                        "dialog.library.full.reload.confirm.message", context.getConfig().getLocale()),
                        ResourceManager.getLabel("dialog.library.full.reload.confirm.title", context
                              .getConfig().getLocale()), JOptionPane.YES_NO_OPTION);
                  if (confirm != JOptionPane.YES_OPTION) {
                     dialog.enableDialog(true);
                     progressBar.setIndeterminate(false);
                     progressBar.setValue(0);
                     return;
                  }
               }

               // 1 - Get the remote song list
               if (progressBar != null) {
                  progressBar.setMaximum(context.getLibraryInfo().getSongCount());
               }
               SongList songList = null;

               try {
                  progressBar.setAdditionalLabel(ResourceManager.getLabel(
                        "dialog.library.progress.loading.remote", context.getConfig().getLocale()));
                  songList = requestManager.getSongList(context.getLibraryInfo().getHost(), context
                        .getLibraryInfo().getPort(), "","", context.getLibraryInfo().getPassword(), dialog);
                  
                  // 2 - update the context
                  context.setMasterSongList(songList);
                  context.setGlobalSongList(songList);
                  context.setGlobalGenreList(ListManager.extractGenreList(context.getGlobalSongList(), context
                        .getConfig().getLocale()));
                  context.setGlobalArtistList(ListManager.extractArtistList(context.getGlobalSongList(), context
                        .getConfig().getLocale()));
                  context.setGlobalAlbumList(ListManager.extractAlbumList(context.getGlobalSongList(), context
                        .getConfig().getLocale()));

                  context.setFilteredGenreList((GenreList) context.getGlobalGenreList().clone());
                  context.setFilteredArtistList((ArtistList) context.getGlobalArtistList().clone());
                  context.setFilteredSongList((SongList) context.getGlobalSongList().clone());
                  context.setFilteredAlbumList((AlbumList) context.getGlobalAlbumList().clone());

                  // 3- get playlist list
                  PlaylistList playlists = playlistRequestManager.getPlaylistList(context.getSession().databaseId, songList);
                  context.setPlaylists(playlists);
                  
                  // 4 - auto save newly loaded library
                  Thread t = new Thread(new Runnable(){
                     public void run() {
                        Library library = new Library();
                        LibraryInfo li = context.getLibraryInfo();
                        if (li == null) return;
                        library.setLibraryInfo(li);
                        library.setSongList(context.getMasterSongList());
                        try {
                           LibraryManager.saveLibrary(context.getConfig().getConfigRootDirectory(), library, true);
                        } catch (FireflyClientException ex) {
                           ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel("dialog.save.library.error.message", context
                                 .getConfig().getLocale(), new String[] { ex.getMessage() }), ResourceManager.getLabel(
                                 "dialog.save.library.error.title", context.getConfig().getLocale()), ex, context.getConfig()
                                 .getLocale());
                           return;
                        }
                     }
                  });
                  t.start();
                  
               } catch (FireflyClientException ex) {
                  dialog.enableDialog(true);
                  ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
                        "dialog.library.load.error.message", context.getConfig().getLocale(),
                        new String[] { ex.getMessage() }), ResourceManager.getLabel(
                        "dialog.library.load.error.title", context.getConfig().getLocale()), ex, context
                        .getConfig().getLocale());
                  progressBar.setIndeterminate(false);
                  progressBar.setValue(0);
                  return;
               }
               // 4 - restore dialog title / hide dialog
               dialog.enableDialog(true);
               dialog.setVisible(false);
               
            } catch (Throwable t) {
               dialog.enableDialog(true);
               ErrorDialog.showDialog(rootContainer, ResourceManager.getLabel(
                     "dialog.library.load.unexpected.error.message", context.getConfig().getLocale(),
                     new String[] { ((t.getMessage() != null) ? " (" + t.getMessage() + ")." : ".") }),
                     ResourceManager.getLabel("dialog.library.load.unexpected.error.title", context
                           .getConfig().getLocale()), t, context.getConfig().getLocale());
               progressBar.setIndeterminate(false);
               progressBar.setValue(0);
               return;
            } finally {
               context.setAutoload(false);
            }
         }
      });
      // 1 - Change dialog appearance
      progressBar.setIndeterminate(true);
      dialog.enableDialog(false);
      t.start();

   }
	
	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			context.setLibraryInfo(null);
			context.setPlaylists(null);
		}
		super.processWindowEvent(e);
	}

	class EscapeKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				context.setLibraryInfo(null);
				dispose();
			}
		}
	}

	public void onProgressChange(SongListLoadProgressEvent evt) {
		if (progressBar != null) {
			if (evt.getSongListSize() != -1) {
				progressBar.setMaximum(evt.getSongListSize());
			}
			if (progressBar.isIndeterminate()) {
				if (progressBar.getValue() > 0) {
					progressBar.setIndeterminate(false);
				}
			}
			progressBar.setValue(evt.getNumberOfLoadedSongs());
		}
	}
}

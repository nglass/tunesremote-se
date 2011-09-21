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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;

public class ConfigLocationDialog extends JDialog implements
		PropertyChangeListener {

	private static final long serialVersionUID = 8937734224829299820L;

	protected String DIALOG_TITLE;

	// -- ATTRIBUTE(S)
	protected Context context;

	protected Frame rootContainer;

	protected JRadioButton appDirRadio;

	protected JRadioButton homeDirRadio;

	protected ButtonGroup group;

	protected JButton okButton;

	protected JButton cancelButton;
	
	protected String configRootDirectory = null;

	// -- CONSTRUCTOR(S)
	public ConfigLocationDialog(Context context, Frame rootContainer) {
		super(rootContainer, true);
		this.DIALOG_TITLE = ResourceManager
				.getLabel("dialog.config.location.title", context.getConfig()
						.getLocale());
		this.context = context;
		this.rootContainer = rootContainer;
		initialize();
	}

	// -- METHOD(S)
	protected void initialize() {
		// -- title
		setTitle(DIALOG_TITLE);
		
		// -- radios

		homeDirRadio = new JRadioButton();
		String homeDirRadioText = ResourceManager.getLabel(
				"dialog.config.location.homedir", context.getConfig()
						.getLocale());
		homeDirRadio.setText("   " + homeDirRadioText + "   ");
		homeDirRadio.setFont(homeDirRadio.getFont().deriveFont(Font.BOLD));
		homeDirRadio.setSelected(true);

		appDirRadio = new JRadioButton();
		String appDirRadioText = ResourceManager.getLabel(
				"dialog.config.location.appdir", context.getConfig()
						.getLocale());
		appDirRadio.setText("   " + appDirRadioText + "   ");
		appDirRadio.setFont(appDirRadio.getFont().deriveFont(Font.BOLD));
		appDirRadio.setSelected(false);

		group = new ButtonGroup();
		group.add(appDirRadio);
		group.add(homeDirRadio);

		// -- labels
		JLabel messageLabel = new JLabel(ResourceManager.getLabel(
				"dialog.config.location.message", context.getConfig()
						.getLocale()));

		JLabel appDirValue = new JLabel(ConfigurationManager
				.getConfigRootDirectoryApp());
		JLabel appDirRadioHint = new JLabel(ResourceManager.getLabel(
				"dialog.config.location.appdir.hint", context.getConfig()
						.getLocale()));
		appDirRadioHint.setFont(appDirRadioHint.getFont().deriveFont(10F)
				.deriveFont(Font.ITALIC));

		JLabel homeDirValue = new JLabel(ConfigurationManager
				.getConfigRootDirectoryUser());
		JLabel homeDirRadioHint = new JLabel(ResourceManager.getLabel(
				"dialog.config.location.homedir.hint", context.getConfig()
						.getLocale()));
		homeDirRadioHint.setFont(homeDirRadioHint.getFont().deriveFont(10F)
				.deriveFont(Font.ITALIC));

		// -- buttons
		okButton = new JButton(ResourceManager.getLabel(
				"dialog.config.location.button.next", context.getConfig()
						.getLocale()));
		okButton.addActionListener(new OKButtonActionListener(this));
		cancelButton = new JButton(ResourceManager.getLabel(
				"dialog.config.location.button.cancel", context.getConfig()
						.getLocale()));
		cancelButton.addActionListener(new CancelButtonActionListener(this));

		int margin = 15;

		// -- add elements in the layout
		GridBagConstraints messageLabelGBC = new GridBagConstraints(0, 0, 2, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(margin, 5, 5, 5), 1, 1);

		GridBagConstraints homeDirRadioGBC = new GridBagConstraints(0, 1, 1, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, margin, 0, 5), 1, 1);
		GridBagConstraints homeDirValueGBC = new GridBagConstraints(1, 1, 1, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, margin), 1, 1);
		GridBagConstraints homeDirRadioHintGBC = new GridBagConstraints(0, 2,
				2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, margin, 5, margin), 1, 0);

		GridBagConstraints appDirRadioGBC = new GridBagConstraints(0, 3, 1, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, margin, 0, 5), 1, 1);
		GridBagConstraints appDirValueGBC = new GridBagConstraints(1, 3, 1, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, margin), 1, 1);
		GridBagConstraints appDirRadioHintGBC = new GridBagConstraints(0, 4, 2,
				1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, margin, 5, margin), 1, 1);

		GridBagConstraints cancelButtonGBC = new GridBagConstraints(0, 5, 1, 1,
				0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(15, margin, margin, 5), 1, 1);
		GridBagConstraints okButtonGBC = new GridBagConstraints(1, 5, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(15, 5, margin, margin), 1, 1);

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(messageLabel, messageLabelGBC);
		getContentPane().add(appDirRadio, appDirRadioGBC);
		getContentPane().add(appDirValue, appDirValueGBC);
		getContentPane().add(appDirRadioHint, appDirRadioHintGBC);
		getContentPane().add(homeDirRadio, homeDirRadioGBC);
		getContentPane().add(homeDirValue, homeDirValueGBC);
		getContentPane().add(homeDirRadioHint, homeDirRadioHintGBC);
		getContentPane().add(okButton, okButtonGBC);
		getContentPane().add(cancelButton, cancelButtonGBC);

		getRootPane().setDefaultButton(okButton);

		// -- close/submission behavior
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		appDirRadio.addKeyListener(new EscapeKeyListener());
		homeDirRadio.addKeyListener(new EscapeKeyListener());
		okButton.addKeyListener(new EscapeKeyListener());
		cancelButton.addKeyListener(new EscapeKeyListener());

		// -- center dialog when shown / manage field selection
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				try {
					setUndecorated(false);
				} catch (Throwable ignore) {
				}
				center();
				context.setLibraryInfo(null);
				getRootPane().setDefaultButton(okButton);
				homeDirRadio.requestFocus();
			}
		});

		UIManager.addPropertyChangeListener(this);
	}

	// -- center the dialog
	protected void center() {
		pack();
		Rectangle window = getBounds();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
		validate();
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(this);
			return;
		}
	}

	// -- INNER CLASS(ES)

	class CancelButtonActionListener implements ActionListener {
		protected JDialog dialog;

		public CancelButtonActionListener(JDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			configRootDirectory = null;
			dialog.dispose();
		}
	}

	class OKButtonActionListener implements ActionListener {
		protected ConfigLocationDialog dialog;

		public OKButtonActionListener(ConfigLocationDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			if (homeDirRadio.isSelected()){
				configRootDirectory = ConfigurationManager.getConfigRootDirectoryUser();
			} else {
				configRootDirectory = ConfigurationManager.getConfigRootDirectoryApp();
			}
			dialog.dispose();
		}
	}

	public void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			context.setLibraryInfo(null);
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

	class FieldFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent focusEvent) {
			JTextField field = (JTextField) focusEvent.getSource();
			field.selectAll();
		}
	}

	public String getConfigRootDirectory() {
		return configRootDirectory;
	}
}

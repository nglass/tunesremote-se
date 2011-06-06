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

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.firefly.client.controller.ConfigurationManager;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.renderer.LocaleComboBoxRenderer;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.tools.FireflyClientException;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = -7833126945802553373L;

	protected String DIALOG_TITLE;

	// -- ATTRIBUTE(S)
	protected Context context;

	protected Frame rootContainer;

	protected JCheckBox showGenreCheckbox;
	
	protected JCheckBox notificationEnabledCheckbox;

	protected JComboBox localeCombo;
	
	protected JButton okButton;

	protected JButton cancelButton;

	// -- CONSTRUCTOR(S)

	public PreferencesDialog(Context context, Frame rootContainer) {
		super(rootContainer, true);
		this.context = context;
		this.rootContainer = rootContainer;
		this.DIALOG_TITLE = ResourceManager.getLabel("dialog.prefs.title", context.getConfig().getLocale());
		initialize();
	}

	// -- METHOD(S)
	protected void initialize() {
		// -- title
		setTitle(DIALOG_TITLE);

		// -- FIELDS

		// -- locale
		localeCombo = new JComboBox(Configuration.AVAILABLE_LOCALES);
		localeCombo.setSelectedItem(context.getConfig().getLocale());
		localeCombo.setRenderer(new LocaleComboBoxRenderer());
		
		// -- show genre
		showGenreCheckbox = new JCheckBox("<html>&nbsp;&nbsp;"
				+ ResourceManager.getLabel("dialog.prefs.show.genre", context.getConfig().getLocale()),
				context.getConfig().isShowGenre());
		
		// -- notification popup
		notificationEnabledCheckbox = new JCheckBox("<html>&nbsp;&nbsp;"
				+ ResourceManager.getLabel("dialog.prefs.notification", context.getConfig().getLocale()), context
				.getConfig().isNotificationEnabled());
		if (!Configuration.canActivateNotification()) {
			notificationEnabledCheckbox.setToolTipText(ResourceManager.getLabel("dialog.prefs.notification.tooltip", context
					.getConfig().getLocale()));
			notificationEnabledCheckbox.setSelected(false);
			notificationEnabledCheckbox.setEnabled(false);
		}
		
		
		// -- LABELS
		JLabel localeComboLabel = new JLabel(ResourceManager
				.getLabel("dialog.prefs.locale", context.getConfig().getLocale()));
		
		// -- BUTTONS
		okButton = new JButton(ResourceManager.getLabel("dialog.prefs.button.ok", context.getConfig().getLocale()));
		okButton.addActionListener(new OKButtonActionListener(this));
		cancelButton = new JButton(ResourceManager.getLabel("dialog.prefs.button.cancel", context.getConfig().getLocale()));
		cancelButton.addActionListener(new CancelButtonActionListener(this));
		
		// -- add elements in the layout
		int margin = 15;

		// -- infos
		GridBagConstraints localeLabelGBC = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, margin, 2, 5), 1, 1);
		GridBagConstraints localeComboGBC = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 5, 2, margin), 1, 1);

		GridBagConstraints showGenreCheckboxGBC = new GridBagConstraints(0, 5, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, margin, 0, 5), 1, 1);
		
		GridBagConstraints notificationEnabledCheckboxGBC = new GridBagConstraints(0, 6, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(-5, margin, -5, 5), 1, 1);			

		// -- buttons
		GridBagConstraints cancelButtonGBC = new GridBagConstraints(0, 24, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(10, margin, 10, 5), 1, 1);
		GridBagConstraints okButtonGBC = new GridBagConstraints(1, 24, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(10, 5, 10, margin), 1, 1);

		getContentPane().setLayout(new GridBagLayout());

		getContentPane().add(localeComboLabel, localeLabelGBC);
		getContentPane().add(localeCombo, localeComboGBC);

		getContentPane().add(showGenreCheckbox, showGenreCheckboxGBC);
		
		if (!context.isApplet()) {
			getContentPane().add(notificationEnabledCheckbox, notificationEnabledCheckboxGBC);
		}
		
		getContentPane().add(okButton, okButtonGBC);
		getContentPane().add(cancelButton, cancelButtonGBC);

		getRootPane().setDefaultButton(okButton);

		// -- close/submission behaviour
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addKeyListener(new EscapeKeyListener());
		localeCombo.addKeyListener(new EscapeKeyListener());
		showGenreCheckbox.addKeyListener(new EscapeKeyListener());
		notificationEnabledCheckbox.addKeyListener(new EscapeKeyListener());
		okButton.addKeyListener(new EscapeKeyListener());
		cancelButton.addKeyListener(new EscapeKeyListener());

		// -- center dialog when shown / manage field selection
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				try {
					setUndecorated(false);
				} catch (Throwable ignore){}
				center();
				setValuesFromContextConfig();
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

	protected void setValuesFromContextConfig() {
		showGenreCheckbox.setSelected(context.getConfig().isShowGenre());
		notificationEnabledCheckbox.setSelected(context.getConfig().isNotificationEnabled());
		localeCombo.setSelectedItem(context.getConfig().getLocale());
	}

	protected void enableDialog(boolean enable) {
		if (enable) {
			setTitle(DIALOG_TITLE);
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setTitle(DIALOG_TITLE + " - " + ResourceManager.getLabel("dialog.prefs.wait", context.getConfig().getLocale()));
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		okButton.setEnabled(enable);
		cancelButton.setEnabled(enable);
	}

	// -- INNER CLASS(ES)

	class CancelButtonActionListener implements ActionListener {
		protected JDialog dialog;

		public CancelButtonActionListener(JDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
		}
	}

	class OKButtonActionListener implements ActionListener {
		protected PreferencesDialog dialog;

		public OKButtonActionListener(PreferencesDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			// -- show genre
			if (showGenreCheckbox.isEnabled()) {
				context.getConfig().setShowGenre(showGenreCheckbox.isSelected());
				if (context.getConfig().isShowGenre()){
					context.getGlobalContainer().showGenre();
				} else {
					context.getGlobalContainer().hideGenre();
				}
			} 
			
			// -- notification popups
			if (notificationEnabledCheckbox.isEnabled()) {
				if (!context.getConfig().isNotificationEnabled() == notificationEnabledCheckbox.isSelected()) {
					context.getConfig().setNotificationEnabled(notificationEnabledCheckbox.isSelected());
					JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel(
							"dialog.prefs.notification.change.message", context.getConfig().getLocale()), ResourceManager
							.getLabel("dialog.prefs.notification.change.title", context.getConfig().getLocale()),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}

			// -- locale
			if (!context.getConfig().getLocale().toString().equals(localeCombo.getSelectedItem().toString())) {
				JOptionPane.showMessageDialog(rootContainer, ResourceManager.getLabel("dialog.prefs.locale.change.message",
						context.getConfig().getLocale()), ResourceManager.getLabel("dialog.prefs.locale.change.title",
						context.getConfig().getLocale()), JOptionPane.INFORMATION_MESSAGE);
				context.getConfig().setLocale((Locale) localeCombo.getSelectedItem());
			}
			
			// -- hide dialog
			dialog.setVisible(false);
			
			// -- save configuration
			Thread t = new Thread(new Runnable(){
				public void run() {
					try {
						ConfigurationManager.saveConfiguration(context.getConfig());
					} catch (FireflyClientException ex) {
						ex.printStackTrace();
					}
				}
			});
			t.start();
			
		}
	}
	
	class EscapeKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				setVisible(false);
			}
		}
	}

	class EnterKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				okButton.doClick();
			}
		}
	}

	class FieldFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent focusEvent) {
			JTextField field = (JTextField) focusEvent.getSource();
			field.selectAll();
		}
	}
	
}

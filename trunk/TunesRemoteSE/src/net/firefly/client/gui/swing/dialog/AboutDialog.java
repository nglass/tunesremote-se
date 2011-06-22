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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.MissingResourceException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.firefly.client.Version;
import net.firefly.client.controller.ResourceManager;
import net.firefly.client.model.configuration.Configuration;
import net.firefly.client.tools.FireflyClientException;

public class AboutDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -6005128319121780861L;

	protected String DIALOG_TITLE;

	// -- ATTRIBUTE(S)
	protected Configuration config;

	protected Frame rootContainer;

	protected int width;

	protected int height;

	protected JButton okButton;
	
	// Opens Desktop Browser and browses to address
	class Browser implements HyperlinkListener {
		 
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            	try {
					Desktop.getDesktop().browse(e.getURL().toURI());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
            }
        }
    }

	protected Browser browser = new Browser();
	
	// -- CONSTRUCTOR(S)

	public AboutDialog(Configuration config, Frame rootContainer) {
		super(rootContainer, true);
		this.config = config;
		this.rootContainer = rootContainer;
		this.DIALOG_TITLE = ResourceManager.getLabel("dialog.about.title", config.getLocale());
		initialize();
	}

	// -- METHOD(S)
	protected void initialize() {
		// -- title
		setTitle(DIALOG_TITLE);
		
		// -- size
		this.width = 600;
		this.height = 500;

		// -- tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		
		// -- labels
		JLabel versionLabel = new JLabel("<html><b>" + Version.APPLICATION_NAME + " - " + Version.getVersion());
		JLabel homeDirLabel = new JLabel("<html>Configuation: " + config.getConfigRootDirectory());
		JLabel authorLabel = new JLabel("<html><i>&copy; Nick Glass 2011");

		// -- contact pane
		JEditorPane contactPane = new JEditorPane();
		try {
			String content = ResourceManager.loadHtml("contact");
			contactPane = new JEditorPane("text/html", content);
			contactPane.addHyperlinkListener(this.browser);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		} catch (FireflyClientException e) {
			e.printStackTrace();
		}
		contactPane.setEditable(false);
		contactPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contactPane.setMargin(new Insets(5, 5, 5, 5));
		contactPane.setBackground(Color.WHITE);

		JScrollPane contactScrollPane = new JScrollPane(contactPane);
		contactScrollPane.setBorder(new LineBorder(new Color(230,230,230),1));
		
		// -- resources pane
		JEditorPane resourcesPane = new JEditorPane();
		try {
			String content = ResourceManager.loadHtml("resources");
			resourcesPane = new JEditorPane("text/html", content);
			resourcesPane.addHyperlinkListener(this.browser);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		} catch (FireflyClientException e) {
			e.printStackTrace();
		}
		resourcesPane.setEditable(false);
		resourcesPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		resourcesPane.setMargin(new Insets(5, 5, 5, 5));
		resourcesPane.setBackground(Color.WHITE);

		JScrollPane resourcesScrollPane = new JScrollPane(resourcesPane);
		resourcesScrollPane.setBorder(new LineBorder(new Color(230,230,230),1));

		// -- licence pane
		JEditorPane licencePane = new JEditorPane();
		try {
			String content = ResourceManager.getLicence();
			licencePane = new JEditorPane("text/plain", content);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		} catch (FireflyClientException e) {
			e.printStackTrace();
		}
		licencePane.setEditable(false);
		licencePane.setBorder(new EmptyBorder(0, 0, 0, 0));
		licencePane.setMargin(new Insets(5, 5, 5, 5));
		licencePane.setBackground(Color.WHITE);

		JScrollPane licenceScrollPane = new JScrollPane(licencePane);
		licenceScrollPane.setBorder(new LineBorder(new Color(230,230,230),1));

		tabbedPane.addTab("Licence", licenceScrollPane);
		tabbedPane.addTab("Contact", contactScrollPane);
		tabbedPane.addTab("Resources", resourcesScrollPane);
		
		// -- buttons
		okButton = new JButton(ResourceManager.getLabel("dialog.about.button.ok", config.getLocale()));
		okButton.setFocusPainted(false);
		okButton.addActionListener(new OKButtonActionListener(this));

		// -- add elements in the layout

		GridBagConstraints versionLabelGBC = new GridBagConstraints(0, 0, 1, 1, 100, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);

		GridBagConstraints authorLabelGBC = new GridBagConstraints(0, 1, 1, 1, 100, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);
		
		GridBagConstraints homeDirLabelGBC = new GridBagConstraints(0, 2, 1, 1, 100, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);

		GridBagConstraints tabbedPaneGBC = new GridBagConstraints(0, 3, 1, 1, 100, 100, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
		GridBagConstraints okButtonGBC = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(15, 5, 2, 5), 1, 1);

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(versionLabel, versionLabelGBC);
		getContentPane().add(authorLabel, authorLabelGBC);
		getContentPane().add(homeDirLabel, homeDirLabelGBC);
		getContentPane().add(tabbedPane, tabbedPaneGBC);
		getContentPane().add(okButton, okButtonGBC);

		// -- close/submission behaviour
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addKeyListener(new EscapeKeyListener());
		addKeyListener(new EnterKeyListener());
		okButton.addKeyListener(new EscapeKeyListener());
		okButton.addKeyListener(new EnterKeyListener());

		// -- center dialog when shown / manage field selection
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				try {
					setUndecorated(false);
				} catch (Throwable ignore){}
				center();
			}
		});

		UIManager.addPropertyChangeListener(this);

	}

	// -- center the dialog
	protected void center() {
		if (rootContainer != null) {
			Rectangle rcRect = rootContainer.getBounds();
			int x = rcRect.x + (rcRect.width / 2) - (width / 2), y = rcRect.y + (rcRect.height / 2) - (height / 2);
			setBounds(x, y, width, height);
		} else {
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((screen.width - width) / 2, (screen.height - height) / 2, width, height);
		}
		validate();
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(this);
			return;
		}
	}

	// -- INNER CLASS(ES)

	class OKButtonActionListener implements ActionListener {
		protected JDialog dialog;

		public OKButtonActionListener(JDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
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
}

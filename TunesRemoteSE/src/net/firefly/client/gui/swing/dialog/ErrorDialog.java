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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.firefly.client.controller.ResourceManager;

public class ErrorDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 665154188150835744L;

	protected String DIALOG_TITLE;
	
	// -- ATTRIBUTE(S)
	protected Frame rootContainer;

	protected Locale locale;

	protected String message;

	protected String stacktrace;

	protected JComponent detailsArea;

	protected JButton showDetailButton;

	protected JButton okButton;

	protected Icon icon;

	protected boolean isDetailShown = false;

	protected JPanel mainPanel;

	protected JPanel subpanel;

	// -- CONSTRUCTOR(S)
	protected ErrorDialog(Frame rootContainer, String message, String title, Throwable th, Locale locale) {
		super(rootContainer, true);
		this.DIALOG_TITLE = title;
		this.rootContainer = rootContainer;
		this.message = message;
		this.icon = UIManager.getIcon("OptionPane.errorIcon");
		this.locale = locale;
		StringWriter writer = new StringWriter();
		th.printStackTrace(new PrintWriter(writer));
		writer.flush();
		this.stacktrace = writer.toString();
		initialize();
	}

	// -- METHOD(S)
	protected void initialize() {
		
		// -- title
		setTitle(DIALOG_TITLE);
		mainPanel = new JPanel(new GridBagLayout());

		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JTextArea messageArea = new JTextArea();
		messageArea.setText(message);
		messageArea.setColumns(30);
		messageArea.setFont(rootContainer.getFont());
		messageArea.setForeground(rootContainer.getForeground());
		messageArea.setOpaque(false);
		messageArea.setEditable(false);
		messageArea.setLineWrap(true);

		GridBagConstraints iconGBC = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 1);
		mainPanel.add(new JLabel(icon), iconGBC);

		GridBagConstraints messageAreaGBC = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 1);
		mainPanel.add(messageArea, messageAreaGBC);

		GridBagConstraints buttonsPanelGBC = new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 1);
		mainPanel.add(createButtonsPanel(), buttonsPanelGBC);

		JTextArea details = new JTextArea();
		details.setColumns(60);
		details.setFont(rootContainer.getFont());
		details.setText(stacktrace);
		details.setEditable(false);

		detailsArea = new JPanel(new BorderLayout(0, 10));
		detailsArea.add(new JSeparator(), BorderLayout.NORTH);
		JScrollPane detailsScrollPane = new JScrollPane(details);
		detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		detailsScrollPane.setPreferredSize(new Dimension(300, 150));
		detailsArea.add(detailsScrollPane, BorderLayout.CENTER);

		subpanel = new JPanel(new BorderLayout());

		GridBagConstraints subpanelGBC = new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1);
		mainPanel.add(subpanel, subpanelGBC);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
		setModal(true);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				try {
					setUndecorated(false);
				} catch (Throwable ignore){}
				center();
				okButton.requestFocus();
			}
		});

		UIManager.addPropertyChangeListener(this);
	}

	// -- center the dialog
	protected void center() {
		pack();
		Rectangle rcRect = rootContainer.getBounds();
		int x = rcRect.x + (rcRect.width / 2) - (getWidth() / 2), y = rcRect.y + (rcRect.height / 2) - (getHeight() / 2);
		setBounds(x, y, getWidth(), getHeight());
		setBounds(0,0, 300, 100);
		validate();
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("lookAndFeel")) {
			//SwingUtilities.updateComponentTreeUI(this);
			return;
		}
	}

	public static void showDialog(Frame rootContainer, String message, String title, Throwable t, Locale locale) {
		if (!rootContainer.isVisible()){
			rootContainer.setVisible(true);
		}
		ErrorDialog errorDialog = new ErrorDialog(rootContainer, message, title, t, locale);
		errorDialog.setVisible(true);
	}

	protected JPanel createButtonsPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		showDetailButton = new JButton(ResourceManager.getLabel("dialog.error.button.details.more", locale));
		showDetailButton.addActionListener(new ShowDetailButtonAction());
		showDetailButton.addKeyListener(new EscapeKeyListener());
		panel.add(showDetailButton);

		okButton = new JButton(ResourceManager.getLabel("dialog.error.button.ok", locale));
		okButton.addActionListener(new OKButtonAction());
		okButton.addKeyListener(new EscapeKeyListener());
		panel.add(okButton);
		
		getRootPane().setDefaultButton(okButton);

		return panel;
	}

	protected class OKButtonAction extends AbstractAction {
		private static final long serialVersionUID = 4723767411303065589L;

		public void actionPerformed(ActionEvent evt) {
			dispose();
		}
	}

	protected class ShowDetailButtonAction extends AbstractAction {

		private static final long serialVersionUID = 1471316674524309884L;

		public void actionPerformed(ActionEvent evt) {
			if (isDetailShown) {
				subpanel.remove(detailsArea);
				isDetailShown = false;
				showDetailButton.setText(ResourceManager.getLabel("dialog.error.button.details.more", locale));
			} else {
				subpanel.add(detailsArea, BorderLayout.CENTER);
				showDetailButton.setText(ResourceManager.getLabel("dialog.error.button.details.less", locale));
				isDetailShown = true;
			}
			pack();
		}
	}

	class EscapeKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				dispose();
			}
		}
	}
}

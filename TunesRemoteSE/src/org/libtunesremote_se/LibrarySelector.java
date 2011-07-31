package org.libtunesremote_se;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.tunesremote.daap.Session;

public class LibrarySelector extends JDialog implements ListDataListener {

	private static final long serialVersionUID = 2893337391529305727L;
	private JList addressList;
	private PairingDatabase pairingDatabase;

	protected int width;

	protected int height;
	
	protected Frame rootContainer;
	
	protected DefaultListModel model;
	
	protected String lastSession;
	
	private void close() {
		this.setVisible(false);
		CloseOnLastWindow.unregisterWindow();
		this.dispose();
	}
	
	public LibrarySelector(final TunesRemoteSessionCallback callback, final Frame rootContainer) {
		super(rootContainer, true);
		setTitle("Select Library:");
		final JDialog frame = this;

		this.rootContainer = rootContainer;
		// -- size
		this.width = 300;
		this.height = 200;
		
		pairingDatabase = new PairingDatabase(TunesService.getConfigDirectory());
		lastSession = pairingDatabase.getLastSession();
		
		model = TunesService.getServiceList();
		model.addListDataListener(this);
		
		addressList = new JList(model);	   
		addressList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addressList.setSelectedIndex(-1);
		addressList.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(addressList);
		initSelection();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton okButton = new JButton("OK");
		frame.getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);		

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (addressList.getSelectedValue() instanceof LibraryDetails) {
					LibraryDetails l = (LibraryDetails) addressList.getSelectedValue();
					
					String code = pairingDatabase.findCode(l.getServiceName());
					if (code != null) {
						
						// Log into session
						Session session = null;
						try {
							session = new Session(l.getAddress(), code);
						} catch (Exception exception) {
							JOptionPane.showMessageDialog
								(frame, "Error: Could not start session", "Error", JOptionPane.ERROR_MESSAGE);
							session = null;
							exception.printStackTrace();
						}
						
						// start gui
						if (session != null) {
							frame.setVisible(false);
							callback.newSession(l, session);
							pairingDatabase.setLastSession(l.getServiceName());
							close();
						}
						
					} else {
						JOptionPane.showMessageDialog(frame, "Error: Not yet paired", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});


		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		//Set Up Window Exit
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		GridBagConstraints scrollGBC = new GridBagConstraints(0, 0, 1, 1, 100, 100, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);

		GridBagConstraints buttonsGBC = new GridBagConstraints(0, 1, 1, 1, 100, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);
		
		frame.getContentPane().setLayout(new GridBagLayout());
		frame.getContentPane().add(listScrollPane,scrollGBC);	
		frame.getContentPane().add(buttonPane,buttonsGBC);
		
		//Size, Title and setVisible				 
		frame.pack();
		CloseOnLastWindow.registerWindow();
		
		// -- center dialog when shown / manage field selection
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				try {
					setUndecorated(false);
				} catch (Throwable ignore){}
				center();
			}
		});
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

	public void initSelection() {
	   // If user has not modified selection then
      // Automatically highlight last Session if found
      if (addressList.getSelectedIndex() == -1 && lastSession != null) {
         for (int i = 0; i < model.size(); i++) {
            Object o = model.get(i);
            if (o instanceof LibraryDetails) {
               LibraryDetails l = (LibraryDetails) o;
               if (l.getServiceName().equals(lastSession)) {
                  addressList.setSelectedIndex(i);
                  break;
               }
            }
         }
      }
	}
	
   @Override
   public void contentsChanged(ListDataEvent arg0) {
      initSelection();
   }

   @Override
   public void intervalAdded(ListDataEvent arg0) {}

   @Override
   public void intervalRemoved(ListDataEvent arg0) {}
}

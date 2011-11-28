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
package net.firefly.client.gui.swing.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tunesremote.daap.Speaker;
import org.tunesremote.daap.SpeakerControl;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.MasterVolumeChangedEvent;
import net.firefly.client.gui.context.listeners.MasterVolumeChangedEventListener;
import net.firefly.client.gui.swing.other.VolumeSlider;

public class SpeakersDialog extends JDialog implements ItemListener, MasterVolumeChangedEventListener {

   private static final long serialVersionUID = 3026149876056352286L;
   
   private static long MASTER_VOLUME = -1;

   protected String DIALOG_TITLE;

	// -- ATTRIBUTE(S)
	protected Context context;

	protected Frame rootContainer;

	protected int width;

	protected int height;
	
	protected JScrollPane scrollPane;
	
	protected JPanel panel;
	
	protected SpeakerControl speakerControl = null;
	
	protected List<Speaker> speakers = null;
	
	protected List<VolumeSlider> sliders = null;
	
	protected ImageIcon volumeLowIcon;
	
	protected ImageIcon volumeHighIcon;
	
	protected JLabel masterVolumeLabel, masterLowLabel, masterHighLabel;
	
	protected VolumeSlider masterSlider;
	
	protected double masterVolume = 0.0;

	// -- CONSTRUCTOR(S)

	public SpeakersDialog(Context context, Frame rootContainer) {
		super(rootContainer, true);
		this.context = context;
		this.rootContainer = rootContainer;
		this.DIALOG_TITLE = ResourceManager.getLabel("speakers.multiple", context.getConfig().getLocale());
		this.speakers = new ArrayList<Speaker>();
		this.sliders = new ArrayList<VolumeSlider>();
		
		initialize();
	}

	// -- METHOD(S)
	protected void initialize() {
      this.volumeLowIcon = new ImageIcon(getClass()
            .getResource("/net/firefly/client/resources/images/volume-low.png"));
      this.volumeHighIcon = new ImageIcon(getClass().getResource(
            "/net/firefly/client/resources/images/volume-high.png"));
	   
	   // -- title
		setTitle(DIALOG_TITLE);
		this.setModal(false);
		
		// -- size
		this.width = 600;
		this.height = 500;
		
		this.panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.addKeyListener(new EscapeKeyListener());
		
		this.masterVolumeLabel = new JLabel(ResourceManager.getLabel("speakers.mastervolume", context.getConfig().getLocale()));
      this.masterLowLabel = newVolumeLowLabel();
      this.masterHighLabel = newVolumeHighLabel();
	   this.scrollPane = new JScrollPane(panel);
	   scrollPane.addKeyListener(new EscapeKeyListener());

		// -- add elements in the layout

		GridBagConstraints scrollPaneGBC = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(15, 5, 15, 5), 1, 1);

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(scrollPane, scrollPaneGBC);

		// -- close/submission behaviour
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addKeyListener(new EscapeKeyListener());

		// -- center dialog when shown / manage field selection
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
			   width = Math.min(ce.getComponent().getPreferredSize().width, 600);
		      height = Math.min(ce.getComponent().getPreferredSize().height, 500);
				try {
					setUndecorated(false);
				} catch (Throwable ignore){}
				center();
			}
		});
		
		context.getPlayer().addMasterVolumeChangedEventListener(this);
	}

	private JLabel newVolumeLowLabel() {
	   JLabel volumeLowLabel = new JLabel(volumeLowIcon);
	   volumeLowLabel.setOpaque(false);
      volumeLowLabel.setVerticalAlignment(SwingConstants.CENTER);
      volumeLowLabel.setBackground(null);
      volumeLowLabel.setIconTextGap(0);
      return volumeLowLabel;
	}
	
	private JLabel newVolumeHighLabel() {
	   JLabel volumeHighLabel = new JLabel(volumeHighIcon);
      volumeHighLabel.setOpaque(false);
      volumeHighLabel.setVerticalAlignment(SwingConstants.CENTER);
      volumeHighLabel.setBackground(null);
      volumeHighLabel.setIconTextGap(0);
	   return volumeHighLabel;
	}
	
	private VolumeSlider newVolumeSlider(long speakerId, double value) {
	   VolumeSlider volumeSlider = new VolumeSlider(value);
      volumeSlider.addChangeListener(new VolumeListener(speakerId));
      volumeSlider.addKeyListener(new EscapeKeyListener());
      return volumeSlider;
	}
	
	public void updateSpeakers() {
	   JLabel volumeHighLabel, volumeLowLabel;
	   VolumeSlider volumeSlider;
	   GridBagConstraints labelGBC, volumeLowGBC, volumeHighGBC, volumeSliderGBC, separatorGBC;
	   int activeCount = 0;
	   
	   panel.removeAll();
	   sliders.clear();
	   
	   speakerControl = new SpeakerControl(context.getSession());
	   masterVolume = speakerControl.getMasterVolume();
	   speakers = speakerControl.getSpeakers();
	
	   this.masterSlider = newVolumeSlider(MASTER_VOLUME, masterVolume);
	   
	   labelGBC = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(20, 5, 20, 5), 1, 1);
      volumeLowGBC = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(20, 0, 20, 0), 1, 1);
      volumeSliderGBC = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(20, 0, 20, 0), 1, 1);
      volumeHighGBC = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(20, 2, 20, 15), 1, 1);
      
      panel.add(masterVolumeLabel, labelGBC);
      panel.add(masterLowLabel, volumeLowGBC);
      panel.add(masterSlider, volumeSliderGBC);
      panel.add(masterHighLabel, volumeHighGBC);
      
	   for (int i=0; i<speakers.size(); i++) {
	      Speaker s = speakers.get(i);
	      
	      JSeparator separator = new JSeparator();
	      JCheckBox checkbox = new JCheckBox(s.getName(), s.isActive());
	      checkbox.setActionCommand(s.getIdAsHex());
	      checkbox.addItemListener(this);
	      checkbox.addKeyListener(new EscapeKeyListener());
	      
	      volumeLowLabel = newVolumeLowLabel();
	      volumeHighLabel = newVolumeHighLabel();
	      volumeSlider = newVolumeSlider(s.getId(), s.getAbsoluteVolume());
	      sliders.add(volumeSlider);
	      
	      if (s.isActive()) {
	         activeCount++;
	      } else {
	         volumeSlider.setEnabled(false);
	      }
	      
	      separatorGBC = new GridBagConstraints(0, 2*i+1, 4, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
               new Insets(0, 5, 0, 5), 1, 1);
	      labelGBC = new GridBagConstraints(0, 2*i+2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
	            new Insets(15, 5, 15, 5), 1, 1);
	      volumeLowGBC = new GridBagConstraints(1, 2*i+2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
               new Insets(15, 0, 15, 0), 1, 1);
	      volumeSliderGBC = new GridBagConstraints(2, 2*i+2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
               new Insets(15, 0, 15, 0), 1, 1);
	      volumeHighGBC = new GridBagConstraints(3, 2*i+2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
               new Insets(15, 2, 15, 15), 1, 1);
	      
	      panel.add(separator, separatorGBC);
	      panel.add(checkbox, labelGBC);
	      panel.add(volumeLowLabel, volumeLowGBC);
	      panel.add(volumeSlider, volumeSliderGBC);
	      panel.add(volumeHighLabel, volumeHighGBC);
	   }
	   
	   if (activeCount <= 1) {
	      for (VolumeSlider slider : sliders) {
	         slider.setEnabled(false);
	      }
	   }
	   
	   panel.validate();
	   context.getPlayer().fireMasterVolumeChanged(new MasterVolumeChangedEvent(masterVolume));
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

	class EscapeKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				setVisible(false);
			}
		}
	}

	//
	// Checkbox events
	//
   @Override
   public void itemStateChanged(ItemEvent e) {
      JCheckBox source = (JCheckBox)e.getItemSelectable();
      String command = source.getActionCommand();
      
      for (Speaker s : speakers) {
         if (s.getIdAsHex().equals(command)) {
            s.setActive(e.getStateChange() == ItemEvent.SELECTED);
            break;
         }
      }
      
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            speakerControl.setSpeakers(speakers);
            updateSpeakers();
         }
      });
   }

   //
   // Volume Slider events
   //
   private class VolumeListener implements ChangeListener {
      protected long speakerId;
      
      public VolumeListener(long speakerId) {
         this.speakerId = speakerId;
      }
      
      @Override
      public void stateChanged(ChangeEvent e) {
         VolumeSlider source = (VolumeSlider)e.getSource();
         
         // if the value is updating then just consume the event
         if (source.isVolumeChanging()) {
            source.setVolumeChanging(false);
            return;
         }
         
         if (!source.isEnabled()) {
            return;
         }
         
         if (speakerId != MASTER_VOLUME) {
            double newMasterVolume = speakerControl.setSpeakerVolume
               (speakers, speakerId, (double)source.getValue() / 10000.0, masterVolume);
            
            if (newMasterVolume != masterVolume) {
               masterVolume = newMasterVolume;
               masterSlider.setVolume(masterVolume);
               context.getPlayer().fireMasterVolumeChanged(new MasterVolumeChangedEvent(newMasterVolume));
            }
              
         } else {
            context.getPlayer().setVolume(source.getValue() / 10000);
         }
      } 
   }

   @Override
   public void onMasterVolumeChange(MasterVolumeChangedEvent evt) {
      double masterVolume = evt.getNewMasterVolume();
      
      if (this.masterVolume == masterVolume || masterSlider == null ) return;
      
      this.masterVolume = masterVolume;
      masterSlider.setVolume(masterVolume);
      
      Iterator<Speaker> itsp = speakers.iterator();
      Iterator<VolumeSlider> itsl = sliders.iterator();
      
      while (itsp.hasNext()) {
         Speaker sp = itsp.next();
         VolumeSlider sl = itsl.next();
         
         sp.calculateAbsoluteFromMaster(masterVolume);
         
         if (sl.isEnabled()) {
            sl.setVolume(sp.getAbsoluteVolume());
         }
      }
   }
}

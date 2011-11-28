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
package net.firefly.client.gui.swing.panel;

import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.MasterVolumeChangedEvent;
import net.firefly.client.gui.context.listeners.MasterVolumeChangedEventListener;
import net.firefly.client.gui.swing.other.VolumeSlider;

public class VolumePanel extends JPanel implements ChangeListener, MasterVolumeChangedEventListener {

	private static final long serialVersionUID = -4189993561956134241L;
	
	protected Context context;
	
	protected VolumeSlider volumeSlider;

	public VolumePanel(Context context) {
		this.context = context;
		initialize();
	}

	protected void initialize() {
		ImageIcon volumeLowIcon = new ImageIcon(getClass()
				.getResource("/net/firefly/client/resources/images/volume-low.png"));
		ImageIcon volumeHighIcon = new ImageIcon(getClass().getResource(
				"/net/firefly/client/resources/images/volume-high.png"));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JLabel volumeLowLabel = new JLabel(volumeLowIcon);
		JLabel volumeHighLabel = new JLabel(volumeHighIcon);
		this.volumeSlider = new VolumeSlider((int)context.getPlayer().getInitialVolume() * 10000);
		volumeSlider.addChangeListener(this);

		volumeLowLabel.setOpaque(false);
		volumeLowLabel.setVerticalAlignment(SwingConstants.CENTER);
		volumeLowLabel.setBackground(null);
		volumeLowLabel.setIconTextGap(0);
		volumeLowLabel.setBorder(new EmptyBorder(new Insets(2, 0, 0, 0)));

		volumeHighLabel.setOpaque(false);
		volumeHighLabel.setVerticalAlignment(SwingConstants.CENTER);
		volumeHighLabel.setBackground(null);
		volumeHighLabel.setIconTextGap(0);
		volumeHighLabel.setBorder(new EmptyBorder(new Insets(2, 4, 0, 0)));

		add(volumeLowLabel);
		add(volumeSlider);
		add(volumeHighLabel);
		
		context.getPlayer().addMasterVolumeChangedEventListener(this);
	}

   @Override
   public void stateChanged(ChangeEvent e) {
      VolumeSlider volumeSlider = (VolumeSlider) e.getSource();
      
      if (volumeSlider.isVolumeChanging()) {
         volumeSlider.setVolumeChanging(false);
         return;
      }
      
      context.getPlayer().setVolume(volumeSlider.getVolume());
   }
   
   @Override
   public void onMasterVolumeChange(MasterVolumeChangedEvent evt) {
      volumeSlider.setVolume(evt.getNewMasterVolume());
   }
}

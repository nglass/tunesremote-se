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
package net.firefly.client.gui.swing.button;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.tunesremote.daap.Speaker;
import org.tunesremote.daap.SpeakerControl;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.swing.dialog.SpeakersDialog;

public class SpeakerButton extends JLabel implements ActionListener {

   private static final long serialVersionUID = 792550431288693505L;

   protected Context context;

   protected Frame rootContainer;

   protected ImageIcon disabledIcon;

   protected ImageIcon currentIcon;

   protected ImageIcon pressedIcon;

   protected JPopupMenu popup;
   
   protected Boolean popupVisible = false;

   protected SpeakerControl speakerControl = null;
   
   protected List<Speaker> speakers = null;

   final static String multipleCommand = "selectMultiple";
   
   protected SpeakersDialog speakersDialog;

   public SpeakerButton(Context context, Frame rootContainer) {
      this.context = context;
      this.rootContainer = rootContainer;
      this.speakerControl = new SpeakerControl(context.getSession());
      initialize();
   }

   public SpeakerButton getSpeakerButton() {
      return this;
   }

   protected void initialize() {
      this.popup = new JPopupMenu();
      this.speakersDialog = new SpeakersDialog(context, rootContainer);

      this.disabledIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/speakers-off.png"));
      this.pressedIcon = new ImageIcon(getClass().getResource("/net/firefly/client/resources/images/speakers-pressed.png"));
      this.currentIcon = this.disabledIcon;

      setToolTipText(ResourceManager.getLabel("speakers.tooltip", context.getConfig().getLocale()));

      setOpaque(false);
      setVerticalAlignment(SwingConstants.CENTER);
      setIcon(this.currentIcon);

      setBackground(null);
      setIconTextGap(0);
      setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

      addMouseListener(new java.awt.event.MouseAdapter() {
         public void mousePressed(java.awt.event.MouseEvent e) {				
            if (popupVisible) {
               popup.setVisible(false);
            } else {
               int activeCount = 0;

               setIcon(pressedIcon);
               popup.removeAll();

               speakers = speakerControl.getSpeakers();

               ButtonGroup group = new ButtonGroup();

               for (Speaker s : speakers) {
                  JRadioButton speakerItem = new JRadioButton(s.getName());
                  speakerItem.setActionCommand(s.getIdAsHex());
                  speakerItem.addActionListener(getSpeakerButton());
                  group.add(speakerItem);

                  if (s.isActive()) {
                     speakerItem.setSelected(true);
                     speakerItem.requestFocus();
                     activeCount++;
                  }

                  popup.add(speakerItem);
               }
               
               if (speakers.size() > 1) {
                  popup.addSeparator();
   
                  JRadioButton multiple = new JRadioButton(
                        ResourceManager.getLabel("speakers.multiple", context.getConfig().getLocale()) + "...");
                  multiple.setActionCommand(multipleCommand);
                  multiple.addActionListener(getSpeakerButton());
                  group.add(multiple);
                  popup.add(multiple);
                  if (activeCount > 1) {
                     multiple.setSelected(true);
                     multiple.requestFocus();
                  }
               }

               popup.show(e.getComponent(), -1, e.getComponent().getHeight());
            }
         }
      });

      popup.addPopupMenuListener(new PopupMenuListener() {
         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            setIcon(currentIcon);
         }

         @Override
         public void popupMenuCanceled(PopupMenuEvent e) {
            setIcon(currentIcon); 
         }
      });
      
      // Only show the button if the speakers command is supported
      this.setVisible(false);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            speakers = speakerControl.getSpeakers();
            if (speakers.size() > 0) {
               setVisible(true);
            }
         }
      }); 
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      if (command == multipleCommand) {
         speakersDialog.updateSpeakers();
         speakersDialog.setVisible(true);
      } else {
         for (ListIterator<Speaker> it = speakers.listIterator();
               it.hasNext(); ) {
            Speaker s = it.next();

            if (s.getIdAsHex().equals(command)) {
               s.setActive(true);
            } else {
               s.setActive(false);
            }
         }

         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               speakerControl.setSpeakers(speakers);
            }
         }); 
      }
      
      popup.setVisible(false);
      setIcon(currentIcon);
   }
}

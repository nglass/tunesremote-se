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
 * Copyright 2012 Nick Glass
 */
package org.libtunesremote_se;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PairingDialog extends JDialog {

   private static final long serialVersionUID = 5099650180407804921L;

   public final static String TAG = PairingServer.class.toString();
   
   protected int width;

   protected int height;
   
   protected JLabel status;
   
   private static PairingDialog instance = null;
   
   private ExecutorService executor = Executors.newFixedThreadPool(1);
   
   private FutureTask<Integer> pairingFuture = null;
   
   protected boolean closing = false;
   
   protected String code;
   
   private CodeLabel[] codeLabel;
   
   protected final Random random = new Random();
   
   private static Image icon;
   
   public static void setIcon(Image image) {
      PairingDialog.icon = image;
   }

   protected PairingDialog() {
      super((Frame)null, true);
      final JDialog frame = this;
      
      this.setTitle("Pairing");
      this.setIconImage(icon);
      
      this.width = 350;
      this.height = 200;
      
      JLabel passcodeLabel = new JLabel("Passcode");
      
      JPanel codePane = new JPanel();
      codePane.setLayout(new FlowLayout(FlowLayout.CENTER));
      
      codeLabel = new CodeLabel[4];
      for (int i=0; i<codeLabel.length; i++) {
         codeLabel[i] = new CodeLabel("0");
         codePane.add(codeLabel[i]);
      }
      
      JLabel label = new JLabel("Enter the above code in your media player");

      status = new JLabel(" ");
      
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            close();
         }
      });
      
      GridBagConstraints passcodeGBC = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);

      GridBagConstraints codeGBC = new GridBagConstraints(0, 1, 1, 1, 100, 0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);
      
      GridBagConstraints labelGBC = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);
      
      GridBagConstraints statusGBC = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);
      
      GridBagConstraints closeGBC = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 1, 1);
      
      frame.getContentPane().setLayout(new GridBagLayout());
      frame.getContentPane().add(passcodeLabel,passcodeGBC);
      frame.getContentPane().add(codePane, codeGBC);
      frame.getContentPane().add(label,labelGBC);
      frame.getContentPane().add(status,statusGBC);
      frame.getContentPane().add(closeButton,closeGBC);
      
      frame.pack();
      
      //Set Up Window Exit
      this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            close();
         }
      });
      
      center();
   }
   
   public static void showPairingDialog() {
      if (instance == null) {
         instance = new PairingDialog();
      }
      instance.startPairing();
      instance.setVisible(true);
   }
   
   private class pairingTask implements Callable<Integer> {
      @Override
      public Integer call() throws Exception {
         int result = PairingServer.getInstance().pair(code);
         updateStatus(result);
         return new Integer(result);
      }
   };
   
   public void startPairing() {
      closing = false;
      status.setText(" ");

      code = String.format("%04d", random.nextInt(10000));
      for (int i=0; i<codeLabel.length; i++) {
         codeLabel[i].setText(Character.toString(code.charAt(i)));
      }
      
      pairingFuture = new FutureTask<Integer>(new pairingTask());
      executor.execute(pairingFuture);
   }
   
   // wait for pairing process to stop and close window
   public void close() {
      if (!closing) {
         closing = true;
         status.setForeground(Color.BLUE);
         if (!pairingFuture.isDone()) {
            status.setText("Shutting down pairing service...");
         }
         PairingServer.getInstance().cancelPairing();
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  pairingFuture.get();
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               } catch (ExecutionException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               instance.setVisible(false);
            }
         });
      } 
   }
   
   public void updateStatus(final int result) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            switch (result) {
            case PairingServer.SUCCESS:
               status.setForeground(Color.BLUE);
               status.setText("Successfully Paired!");
               break;
            case PairingServer.CLOSED:
               status.setText(" ");
               break;
            default:
               status.setForeground(Color.RED);
               status.setText("Pairing failed.");
               break;
            }
         }
      });
   }
   
   public class CodeLabel extends JLabel {
      private static final long serialVersionUID = -7023851558301851820L;

      public CodeLabel(String s) {
         super(s);
         this.setFont(getFont().deriveFont(32.0f));
         this.setBackground(Color.white);
         this.setOpaque(true);
         this.setBorder(BorderFactory.createLineBorder(Color.black));
      }
   }
   
   protected void center() {
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds((screen.width - width) / 2, (screen.height - height) / 2, width, height);
      validate();
   }
}

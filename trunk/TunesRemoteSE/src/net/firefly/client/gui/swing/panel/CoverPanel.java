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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.firefly.client.controller.ResourceManager;
import net.firefly.client.gui.context.Context;
import net.firefly.client.gui.context.events.ContextResetEvent;
import net.firefly.client.gui.context.listeners.ContextResetEventListener;
import net.firefly.client.model.data.Song;
import net.firefly.client.player.events.PlayerStatusChangedEvent;
import net.firefly.client.player.listeners.PlayerStatusChangedEventListener;
import net.firefly.client.tools.ImageTools;

public class CoverPanel extends JPanel implements
      PlayerStatusChangedEventListener, ContextResetEventListener {

   private static final long serialVersionUID = -8283748713804471106L;

   protected BufferedImage DEFAULT_COVER;

   public static final int COVER_MARGIN = 10;
   public static final int SONG_INFO_HEIGHT = 45;
   protected static Color BACKGROUND_COLOR = null;
   protected static Color SONG_INFO_COLOR = Color.BLACK;
   protected static boolean MARGIN_TOP = false;

   protected Context context;

   protected JLabel cover;

   protected BufferedImage coverImage;

   public CoverPanel(Context context) {
      super();
      this.context = context;
      initialize();
   }

   protected void initialize() {
      try {
         DEFAULT_COVER = ImageIO.read(ResourceManager.loadImage("nocover.png"));
      } catch (IOException e) {
         e.printStackTrace();
      }

      setBorder(new EmptyBorder(0, 0, 0, 0));
      if (BACKGROUND_COLOR != null) {
         setBackground(BACKGROUND_COLOR);
      }

      cover = new JLabel();
      cover.setBorder(new EmptyBorder((MARGIN_TOP ? COVER_MARGIN : 0),
            COVER_MARGIN, 0, COVER_MARGIN));

      setLayout(new BorderLayout());
      add(cover, BorderLayout.NORTH);
      addComponentListener(new java.awt.event.ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            if (coverImage != null) {
               displayCover(coverImage);
            }
         }
      });
      context.getPlayer().addPlayerStatusChangedEventListener(this);

      context.addContextResetEventListener(this);

      coverImage = DEFAULT_COVER;
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            // displayCover(DEFAULT_COVER);
            updateCover();
         }
      });
   }

   public void onPlayerStatusChange(PlayerStatusChangedEvent evt) {
      updateCover();
   }

   public void updateCover() {
      if (context.getPlayer().getCover() != null) {
         try {
            BufferedImage bufferedImage = ImageIO
                  .read(new ByteArrayInputStream(context.getPlayer().getCover()));
            displayCover(bufferedImage);
            coverImage = bufferedImage;
         } catch (Throwable t) {
            displayCover(DEFAULT_COVER);
            coverImage = DEFAULT_COVER;
         }
      } else {
         displayCover(DEFAULT_COVER);
         coverImage = DEFAULT_COVER;
      }
   }

   private void displayCover(BufferedImage bufferedImage) {
      int originalWidth = bufferedImage.getWidth();
      int originalHeight = bufferedImage.getHeight();
      int coverWidth = ((getWidth() != 0) ? getWidth() : 150)
            - (2 * COVER_MARGIN);
      int coverHeight = coverWidth * originalHeight / originalWidth;
      if (originalHeight > originalWidth) {
         coverHeight = ((getWidth() != 0) ? getWidth() : 150)
               - (2 * COVER_MARGIN);
         coverWidth = coverHeight * originalWidth / originalHeight;
      }
      BufferedImage tmp = ImageTools.getSmoothScaledInstance(bufferedImage,
            coverWidth, coverHeight);
      tmp = ImageTools.createReflectBottom(tmp, SONG_INFO_HEIGHT);
      tmp = overlaySongInfo(context.getPlayer().getPlayingSong().getSong(),
            tmp, coverHeight);
      cover.setIcon(new ImageIcon(tmp));
   }

   private BufferedImage overlaySongInfo(Song s, BufferedImage cover,
         int startOffset) {
      if (s == null) {
         return cover;
      }
      int finalCoverWidth = ((getWidth() != 0) ? getWidth() : 150)
            - (2 * COVER_MARGIN);
      String artist = null;
      String album = null;
      String year = "";
      String title = "";
      String albumYear = "";

      if (s.getArtist() != null) {
         artist = s.getArtist().toString();
      }
      if (artist == null || artist.trim().length() == 0) {
         artist = ResourceManager.getLabel("table.unknown.artist", context
               .getConfig().getLocale());
      }

      if (s.getAlbum() != null) {
         album = s.getAlbum().toString();
      }
      if (s.getAlbum() == null || album.trim().length() == 0) {
         album = ResourceManager.getLabel("table.unknown.album", context
               .getConfig().getLocale());
      }

      if (s.getYear() != null && s.getYear().trim().length() > 0
            && !"0".equals(s.getYear().trim())) {
         year = " (" + s.getYear() + ") ";
      }
      if (s.getTitle() != null && s.getTitle().trim().length() > 0) {
         title = s.getTitle();
      }
      albumYear = album + year;

      BufferedImage result = new BufferedImage(finalCoverWidth, cover
            .getHeight(), cover.getType());
      Graphics2D g = result.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

      // -- draw centered cover
      g.drawImage(cover, finalCoverWidth / 2 - cover.getWidth() / 2, 0, cover
            .getWidth(), cover.getHeight(), null);

      // -- draw artist name (centered)
      g.setFont(this.getFont().deriveFont(Font.BOLD));
      TextLayout tl = new TextLayout(artist, g.getFont(), g
            .getFontRenderContext());
      Rectangle2D artistBounds = tl.getBounds();
      double x = (finalCoverWidth - artistBounds.getWidth()) / 2
            - artistBounds.getX();
      double y = startOffset + 15;
      g.setPaint(SONG_INFO_COLOR);
      tl.draw(g, (float) x, (float) y);

      // -- draw album+year (centered)
      g.setFont(this.getFont().deriveFont(Font.ITALIC));
      tl = new TextLayout(albumYear, g.getFont(), g.getFontRenderContext());
      Rectangle2D albumYearBounds = tl.getBounds();
      x = (finalCoverWidth - albumYearBounds.getWidth()) / 2
            - albumYearBounds.getX();
      y = startOffset + 29;
      tl.draw(g, (float) x, (float) y);

      // -- draw title (centered)
      if (title != null && title.length() > 0) {
         g.setFont(this.getFont());
         tl = new TextLayout(title, g.getFont(), g.getFontRenderContext());
         Rectangle2D titleBounds = tl.getBounds();
         x = (finalCoverWidth - titleBounds.getWidth()) / 2
               - titleBounds.getX();
         y = startOffset + 43;
         tl.draw(g, (float) x, (float) y);
      }
      g.dispose();
      return result;
   }

   public void onContextReset(ContextResetEvent evt) {
      displayCover(DEFAULT_COVER);
      coverImage = DEFAULT_COVER;
   }

   public BufferedImage getCoverImage() {
      return coverImage;
   }

}

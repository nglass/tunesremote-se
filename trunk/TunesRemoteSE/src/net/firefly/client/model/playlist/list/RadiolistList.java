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
package net.firefly.client.model.playlist.list;

import net.firefly.client.model.playlist.PlaylistStatus;

public class RadiolistList extends PlaylistList {
   
   protected String name;
   
   protected PlaylistStatus status;
   
   public RadiolistList() {
      super();
      this.status = PlaylistStatus.NOT_LOADED;
   }
   
   public PlaylistStatus getStatus() {
      return status;
   }
   
   public void setStatus(PlaylistStatus status) {
      this.status = status;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public String getName() {
      return this.name;
   }
   
   public String toString() {
      return name;
   }
   
}

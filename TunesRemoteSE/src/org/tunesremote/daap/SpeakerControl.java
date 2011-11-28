/*
    TunesRemote+ - http://code.google.com/p/tunesremote-plus/
    
    Copyright (C) 2008 Jeffrey Sharkey, http://jsharkey.org/
    Copyright (C) 2010 TunesRemote+, http://code.google.com/p/tunesremote-plus/
    Copyright (C) 2011 Daniel Thommes
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    The Initial Developer of the Original Code is Jeffrey Sharkey.
    Portions created by Jeffrey Sharkey are
    Copyright (C) 2008. Jeffrey Sharkey, http://jsharkey.org/
    All Rights Reserved.
 */
package org.tunesremote.daap;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class SpeakerControl {

   public final static String TAG = SpeakerControl.class.toString();
   
   protected Session session;
   
   public SpeakerControl(Session session) {
      this.session = session;
   }

   /**
    * Reads the list of available speakers
    * @return list of available speakers
    */
   public List<Speaker> getSpeakers() {

      List<Speaker> speakers = new ArrayList<Speaker>();

      try {
         Log.d(TAG, "getSpeakers() requesting...");

         String temp = String.format("%s/ctrl-int/1/getspeakers?session-id=%s", session.getRequestBase(), session.sessionId);

         byte[] raw = RequestHelper.request(temp, false);

         Response response = ResponseParser.performParse(raw);

         Response casp = response.getNested("casp");

         List<Response> mdclArray = casp.findArray("mdcl");

         // The master volume is required to compute the speakers' absolute
         // volume
         double masterVolume = getMasterVolume();

         for (Response mdcl : mdclArray) {
            Speaker speaker = new Speaker();
            speaker.setName(mdcl.getString("minm"));
            long id = mdcl.getNumberLong("msma");
            speaker.setId(id);
            Log.d(TAG, "Speaker = " + speaker.getName());
            speaker.setRelativeVolume((float) mdcl.getNumberLong("cmvo"));
            boolean isActive = mdcl.containsKey("caia");
            speaker.setActive(isActive);
            speaker.calculateAbsoluteFromMaster(masterVolume);
            speakers.add(speaker);
         }

      } catch (Exception e) {
         Log.e(TAG, "Could not get speakers: ", e);
      }

      return speakers;

   }

   /**
    * Sets (activates or deactivates) the speakers as defined in the given list.
    * @param speakers all speakers to read the active flag from
    */
   public void setSpeakers(List<Speaker> speakers) {

      try {
         Log.d(TAG, "setSpeakers() requesting...");

         String idsString = "";
         boolean first = true;
         // The list of speakers to activate is a comma-separated string with
         // the hex versions of the speakers' IDs
         for (Speaker speaker : speakers) {
            if (speaker.isActive()) {
               if (!first) {
                  idsString += ",";
               } else {
                  first = false;
               }
               idsString += speaker.getIdAsHex();
            }
         }

         String url = String.format("%s/ctrl-int/1/setspeakers?speaker-id=%s&session-id=%s", session.getRequestBase(), idsString, session.sessionId);

         RequestHelper.request(url, false);

      } catch (Exception e) {
         Log.e(TAG, "Could not set speakers: ", e);
      }
   }

   public double getMasterVolume() {
      try {
         // http://192.168.254.128:3689/ctrl-int/1/getproperty?properties=dmcp.volume&session-id=130883770
         Response resp = RequestHelper.requestParsed(
                  String.format("%s/ctrl-int/1/getproperty?properties=dmcp.volume&session-id=%s", session.getRequestBase(), session.sessionId), false);
         return resp.getNested("cmgt").getNumberLong("cmvo");
      } catch (Exception e) {
         e.printStackTrace();
      }
      return -1;

      /*
       * cmgt --+ mstt 4 000000c8 == 200 cmvo 4 00000054 == 84
       */
   }
   
   /**
    * Sets the volume of a single speaker. 
    * Updates the values for the other speakers.
    * Returns the new master volume
    * @param speakers the list of speakers to update
    * @param speakerId ID of the speaker to set the volume of
    * @param newVolume the new volume to set
    * @param masterVolume the current master volume
    * @return newMasterVolume
    */
   public double setSpeakerVolume(List<Speaker> speakers, long speakerId, double newVolume, double masterVolume) {
      double maxVolume = 0.0, secondMaxVolume = 0.0, newMasterVolume = masterVolume, formerVolume = 0.0;
      Speaker speaker = null;

      // first find our loudest two speakers
      for (Speaker s : speakers) {
         if (s.isActive()) {
            double vol = s.getAbsoluteVolume();
            if (vol > maxVolume) {
               secondMaxVolume = maxVolume;
               maxVolume = vol;
            } else if (vol > secondMaxVolume) {
               secondMaxVolume = vol;
            }
            
            if (s.getId() == speakerId) {
               speaker = s;
               formerVolume = vol;
            }
         }
      }
      
      // if we didn't find the requested speaker then give up now
      if (speaker == null) return masterVolume;
      
      // Now adjust the volume
      try {
         /*************************************************************
          * If this speaker will become or is currently the loudest or is the only activated speaker, it will be
          * controlled via the master volume.
          *************************************************************/
         if (newVolume > masterVolume || formerVolume == maxVolume) {
            if (newVolume < secondMaxVolume) {
               // First equalize the volume of this speaker with the second
               // loudest
               setAbsoluteVolume(speakerId, secondMaxVolume);
               newMasterVolume = secondMaxVolume;
               
               double relativeVolume = newVolume * 100.0 / secondMaxVolume;
               // then go on by decreasing the relative volume of this speaker
               setRelativeVolume(speakerId, relativeVolume);
               
               speaker.setRelativeVolume(relativeVolume);
               speaker.calculateAbsoluteFromMaster(secondMaxVolume);
            } else {
               // the speaker will remain the loudest, so just control the
               // absolute volume (master volume)
               setAbsoluteVolume(speakerId, newVolume);
               newMasterVolume = newVolume;
               speaker.setAbsoluteVolume(newVolume);
            }
            
            // Master volume changed recalculate relative volumes
            for (Speaker s : speakers) {
               s.calculateRelativeFromMaster(newMasterVolume);
            }
         }
         /*************************************************************
          * Otherwise its relative volume will be controlled
          *************************************************************/
         else {
            double relativeVolume = newVolume * 100.0 / masterVolume;
            setRelativeVolume(speakerId, relativeVolume);
            
            speaker.setRelativeVolume(relativeVolume);
            speaker.calculateAbsoluteFromMaster(masterVolume);
         }
      } catch (Exception e) {
         Log.e(TAG, "Error when setting speaker volume: ", e);
      }
      
      return newMasterVolume;
   }

   /**
    * Helper to control a speakers's absolute volume. This uses the URL parameters
    * <code>setproperty?dmcp.volume=%d&include-speaker-id=%s</code> which results in iTunes controlling the master
    * volume and the selected speaker synchronously.
    * @param speakerId ID of the speaker to control
    * @param absoluteVolume the volume to set absolutely
    * @throws Exception
    */
   private void setAbsoluteVolume(long speakerId, double absoluteVolume) throws Exception {
      String url;
      url = String.format("%s/ctrl-int/1/setproperty?dmcp.volume=%d&include-speaker-id=%s&session-id=%s", session.getRequestBase(), (long)absoluteVolume,
               speakerId, session.sessionId);
      RequestHelper.request(url, false);
   }

   /**
    * Helper to control a speaker's relative volume. This relative volume is a value between 0 and 100 describing the
    * relative volume of a speaker in comparison to the master volume. For this the URL parameters
    * <code>%s/ctrl-int/1/setproperty?speaker-id=%s&dmcp.volume=%d</code> are used.
    * @param speakerId ID of the speaker to control
    * @param relativeVolume the relative volume to set
    * @throws Exception
    */
   private void setRelativeVolume(long speakerId, double relativeVolume) throws Exception {
      String url;
      url = String.format("%s/ctrl-int/1/setproperty?speaker-id=%s&dmcp.volume=%d&session-id=%s", session.getRequestBase(), speakerId, (long)relativeVolume,
               session.sessionId);
      RequestHelper.request(url, false);
   } 
}

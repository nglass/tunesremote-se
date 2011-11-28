package net.firefly.client.gui.context.events;

import java.util.EventObject;

public class MasterVolumeChangedEvent extends EventObject {

   private static final long serialVersionUID = -79725463382135004L;

   public MasterVolumeChangedEvent(double newMasterVolume) {
      super(new Double(newMasterVolume));
   }

   public double getNewMasterVolume() {
      return ((Double) source).doubleValue();
   }
}

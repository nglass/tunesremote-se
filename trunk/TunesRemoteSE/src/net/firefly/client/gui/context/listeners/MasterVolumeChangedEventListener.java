package net.firefly.client.gui.context.listeners;

import java.util.EventListener;

import net.firefly.client.gui.context.events.MasterVolumeChangedEvent;

public interface MasterVolumeChangedEventListener extends EventListener {

   public void onMasterVolumeChange(MasterVolumeChangedEvent evt);

}


package com.caved_in.commons.listeners;

import com.caved_in.commons.world.WorldHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadedListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoaded(WorldLoadEvent event) {
		WorldHandler.handleWorldWeather(event.getWorld());
	}
}

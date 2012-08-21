package com.volumetricpixels.lockdown;

import org.spout.api.Spout;
import org.spout.api.scheduler.TaskPriority;

public class LockdownAPI {
    
    private static Lockdown plugin;
    
    public static void addBypassedPlayer(String player) {
        plugin.add(player);
    }
    
    public static void addTempBypassedPlayer(String player, long seconds) {
        plugin.add(player);
        Spout.getScheduler().scheduleAsyncDelayedTask(plugin, new TimedRemoveFromList(plugin, player, seconds), 0, TaskPriority.HIGHEST);
    }
    
    public static void removeBypassedPlayer(String player) {
        plugin.remove(player);
    }
    
    static void setPlugin(Lockdown plugin) {
        if (plugin != null) {
            LockdownAPI.plugin = plugin;
        }
    }
    
    // No new instances
    private LockdownAPI() {}
    
}

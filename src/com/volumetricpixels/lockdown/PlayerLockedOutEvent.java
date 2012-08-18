package com.volumetricpixels.lockdown;

import org.spout.api.event.HandlerList;
import org.spout.api.event.player.PlayerEvent;
import org.spout.api.entity.Player;
 
public class PlayerLockedOutEvent extends PlayerEvent {
   
    private static HandlerList handlers = new HandlerList();
 
    public PlayerLockedOutEvent(Player p) {
        super(p);
    }
 
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
   
    public static HandlerList getHandlerList() {
        return handlers;
    }
   
}
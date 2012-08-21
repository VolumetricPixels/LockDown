package com.volumetricpixels.lockdown;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.chat.ChatSection;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.event.EventHandler;
import org.spout.api.event.EventManager;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.player.PlayerJoinEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.entity.Player;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class Lockdown extends CommonPlugin implements Listener, CommandExecutor {
    
    private EventManager em;
    private YamlConfiguration config;
    
    // Config fields
    private List<String> bypassedPlayers;
    private boolean lockdown;
    private String lockedOutMessage;

    @Override
    public void onEnable() {
        LockdownAPI.setPlugin(this);
        this.config = new YamlConfiguration(new File(this.getDataFolder(), "config.yml"));
        
        try {
            if (!config.getFile().exists()) {
                config.getFile().createNewFile();
            }
        } catch (Exception e) {}
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        refresh();
        
        this.em = Spout.getEventManager();
        em.registerEvents(this, this);
        this.getEngine().getRootCommand().addSubCommand(this, "lockdown").setExecutor(this);
        this.getEngine().getRootCommand().addSubCommand(this, "ldadd").setExecutor(this);
        this.getEngine().getRootCommand().addSubCommand(this, "ldremove").setExecutor(this);
    }

    @Override
    public void onDisable() {}

    @Override
    public void processCommand(CommandSource source, Command cmd, CommandContext context) throws CommandException {
        String name = cmd.getPreferredName().toLowerCase();
        boolean ld = name.equals("lockdown");
        boolean add = name.equals("ldadd");
        boolean remove = name.equals("ldremove");
        
        List<ChatSection> csl = context.getRawArgs();
        
        if (ld) {
            if (csl.size() < 1) {
                source.sendMessage(ChatStyle.RED, "Usage: /Lockdown Toggle - Toggle Lockdown");
            } else if (csl.get(0).getPlainString().equalsIgnoreCase("toggle")) {
                if (source.hasPermission("lockdown.toggle")) {
                    lockdown = !lockdown;
                    config.getNode("Lockdown").setValue(lockdown);
                    source.sendMessage(ChatStyle.GRAY, "Lockdown Activated: " + String.valueOf(lockdown));
                    
                    if (lockdown) {
                        getLogger().info("Lockdown activated! Kicking players not on list!");
                        getLogger().info("Activated by: " + source.getName());
                        for (Player p : this.getEngine().getOnlinePlayers()) {
                            if (!bypassedPlayers.contains(p.getName())) {
                                p.kick(lockedOutMessage);
                            }
                        }
                    } else {
                        getLogger().info("Lockdown deactivated by: " + source.getName());
                    }
                } else {
                    source.sendMessage(ChatStyle.RED, "I think not!");
                }
            } else {
                source.sendMessage(ChatStyle.RED, "Usage: /Lockdown Toggle - Toggle Lockdown");
            }
            return;
        } else if (add) {
            if (csl.size() < 1) {
                source.sendMessage(ChatStyle.RED, "Usage: /LdAdd PlayerName - Add PlayerName to the list");
            } else {
                if (source.hasPermission("lockdown.editlist")) {
                    add(csl.get(0).getPlainString());
                    source.sendMessage("Added: " + csl.get(0).getPlainString() + " to Lockdown bypass list!");
                } else {
                    source.sendMessage(ChatStyle.RED, "I think not!");
                }
            }
            return;
        } else if (remove) {
            if (csl.size() < 1) {
                source.sendMessage(ChatStyle.RED, "Usage: /LdRemove PlayerName - Add PlayerName to the list");
            } else {
                if (source.hasPermission("lockdown.editlist")) {
                    remove(csl.get(0).getPlainString());
                    source.sendMessage("Removed: " + csl.get(0).getPlainString() + " from Lockdown bypass list!");
                } else {
                    source.sendMessage(ChatStyle.RED, "I think not!");
                }
            }
            return;
        }
    }
    
    @EventHandler(order = Order.LATEST_IGNORE_CANCELLED)
    public void handleJoin(PlayerJoinEvent e) {
        refresh();
        Player p = e.getPlayer();
        if (lockdown) {
            if (!bypassedPlayers.contains(p.getName())) {
                p.kick(lockedOutMessage);
                em.callEvent(new PlayerLockedOutEvent(p));
                getLogger().info("Locked Out: " + p.getName());
            }
        }
    }
    
    void remove(Object toRemove) {
        bypassedPlayers.remove(toRemove);
        refresh();
    }
    
    void add(String toAdd) {
        bypassedPlayers.add(toAdd);
        refresh();
    }
    
    private void refresh() {
        if ((this.bypassedPlayers = config.getNode("Bypassed-Players").getStringList()) == null) {
            config.getNode("Bypassed-Players").setValue(new ArrayList<String>());
            refresh();
        } else {
            config.getNode("Bypassed-Players").setValue(bypassedPlayers);
        }
        if (String.valueOf((this.lockdown = config.getNode("Lockdown").getBoolean())) == null) {
            config.getNode("Lockdown").setValue(false);
            refresh();
        } else {
            config.getNode("Lockdown").setValue(lockdown);
        }
        if ((this.lockedOutMessage = config.getNode("Locked-Out-Message").getString()) == null) {
            config.getNode("Locked-Out-Message").setValue("Server in lockdown!");
            refresh();
        } else {
            config.getNode("Locked-Out-Message").setValue(lockedOutMessage);
        }
    }
    
}

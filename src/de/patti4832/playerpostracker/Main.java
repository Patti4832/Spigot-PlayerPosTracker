package de.patti4832.playerpostracker;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    Tracker t;

    @Override
    public void onEnable() {
        System.out.println("[PlayerPosTracker] Enabling PlayerPosTracker");
        this.getCommand("tracker").setExecutor(new CommandReceiver(this));
        t = new Tracker(this);
    }

    @Override
    public void onDisable() {
        System.out.println("[PlayerPosTracker] Disabling PlayerPosTracker");
        t.stop();
    }
}

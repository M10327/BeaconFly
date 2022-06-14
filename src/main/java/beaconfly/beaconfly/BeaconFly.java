package beaconfly.beaconfly;

import beaconfly.beaconfly.Commands.bfly;
import beaconfly.beaconfly.Handlers.BeaconHandler;
import beaconfly.beaconfly.Handlers.PlayerHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeaconFly extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new BeaconHandler(this, getConfig());
        new PlayerHandler(this, getConfig());

        getCommand("bfly").setExecutor(new bfly());
        saveConfig();
    }

    @Override
    public void onDisable() {
        // saveConfig();
    }
}

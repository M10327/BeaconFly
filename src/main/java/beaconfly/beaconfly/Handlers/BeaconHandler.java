package beaconfly.beaconfly.Handlers;

import beaconfly.beaconfly.BeaconFly;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Beacon;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BeaconHandler implements Listener {
    FileConfiguration config;
    public BeaconHandler(BeaconFly plugin, FileConfiguration cfg) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }

    // removes the beacon from the config when it gets destroyed or otherwise depowered
    @EventHandler (priority = EventPriority.LOW)
    public void onBeaconBreak(BeaconDeactivatedEvent event){
        if ((config.getConfigurationSection("Beacons").contains(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation())))){
            config.getConfigurationSection("Beacons").set(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation()), null);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void beaconApplyEffect(BeaconEffectEvent event){
        // on beacon effect removes beacon from config if its not tier 4
        if ((config.getConfigurationSection("Beacons").contains(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation())))){
            if (event.getBlock().getState() instanceof Beacon beacon){
                if (beacon.getTier() != 4){
                    config.getConfigurationSection("Beacons").set(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation()), null);
                    Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
                }
            }
        }
        // on beacon effect adds beacon to config if its tier 4
        else if (!(config.getConfigurationSection("Beacons").contains(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation())))){
            if (event.getBlock().getState() instanceof Beacon beacon){
                if (beacon.getTier() == 4){
                    config.getConfigurationSection("Beacons").set(blockConfigFormat(event.getBlock().getWorld().getName(), event.getBlock().getLocation()), true);
                    Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
                }
            }
        }
    }

    public String blockConfigFormat(String name, Location loc){
        return name + "/" + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();
    }

}

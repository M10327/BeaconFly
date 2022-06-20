package beaconfly.beaconfly.Handlers;

import beaconfly.beaconfly.BeaconFly;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerHandler implements Listener {
    FileConfiguration config;
    public PlayerHandler(BeaconFly plugin, FileConfiguration cfg) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = cfg;
    }

    // just adds the player to the config if they are not already in it
    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent event){
        if (!(config.getConfigurationSection("Players").contains(event.getPlayer().getUniqueId().toString()))){
            config.getConfigurationSection("Players").set(event.getPlayer().getUniqueId().toString(), true);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
    }

    // probably not necessary, but it makes sure that the player is in the config for sure
    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerJump(PlayerJumpEvent event){
        if (!(config.getConfigurationSection("Players").contains(event.getPlayer().getUniqueId().toString()))){
            config.getConfigurationSection("Players").set(event.getPlayer().getUniqueId().toString(), true);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event){
        Location loc = event.getPlayer().getLocation();
        boolean isInBeacon = false;
        if ((event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || event.getPlayer().getGameMode().equals(GameMode.ADVENTURE))
                && event.getPlayer().getInventory().getChestplate() != null && event.getPlayer().hasPermission("beaconfly.use")
                && (config.getConfigurationSection("Players").getBoolean(event.getPlayer().getUniqueId().toString()))){
            if (event.getPlayer().getInventory().getChestplate().getType().equals(Material.ELYTRA)){
                for (String str : config.getConfigurationSection("Beacons").getKeys(false)){
                    String[] beacon = str.split("/");
                    if (beacon[0].equals(event.getPlayer().getWorld().getName())){
                        Double[] b = new Double[3];
                        b[0] = Double.parseDouble(beacon[1]);
                        b[1] = Double.parseDouble(beacon[2]);
                        b[2] = Double.parseDouble(beacon[3]);

                        if (b[0] - 50.0 <=  loc.getX() && loc.getX() <= b[0] + 51.0
                                && b[1] - 50.0 <=  loc.getY() && loc.getY() <= b[1] + ((double) event.getPlayer().getWorld().getMaxHeight())
                                &&b[2] - 50.0 <=  loc.getZ() && loc.getZ() <= b[2] + 51.0){

                            isInBeacon = true;
                            break;
                        }
                    }
                }
                if (isInBeacon) {
                    if (!event.getPlayer().getAllowFlight()) {
                        event.getPlayer().setAllowFlight(true);
                    }
                }
                else{
                    disableFlight(event.getPlayer());
                }
            }
        }
        else if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)){
            disableFlight(event.getPlayer());
        }
    }

    public void disableFlight(Player player){
        if (player.isFlying()){
            player.setFlying(false);
            if (config.getBoolean("CancelFlySlowfall")){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, config.getInt("SlowfallDuration") * 20, 0));
            }
            if (config.getBoolean("CancelFlyBlockNextFall")){
                player.setMetadata("blockNextFall", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BeaconFly")), true));
                Bukkit.getScheduler().scheduleSyncDelayedTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BeaconFly")), () -> {
                    if (player.hasMetadata("blockNextFall")){
                        player.removeMetadata("blockNextFall", Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BeaconFly")));
                    }
                }, (long) config.getDouble("BlockNextFallDuration") * 20);
            }
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerFallDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player player)){
            return;
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            if (player.hasMetadata("blockNextFall")){
                event.setCancelled(true);
                player.removeMetadata("blockNextFall", Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BeaconFly")));
            }
        }
    }
}

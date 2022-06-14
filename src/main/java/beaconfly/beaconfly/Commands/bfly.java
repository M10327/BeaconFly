package beaconfly.beaconfly.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class bfly implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        FileConfiguration config = Bukkit.getPluginManager().getPlugin("BeaconFly").getConfig();
        if (!(config.getConfigurationSection("Players").contains(player.getUniqueId().toString()))){
            config.getConfigurationSection("Players").set(player.getUniqueId().toString(), true);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
        else if (!config.getConfigurationSection("Players").getBoolean(player.getUniqueId().toString())){
            config.getConfigurationSection("Players").set(player.getUniqueId().toString(), true);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
        else {
            config.getConfigurationSection("Players").set(player.getUniqueId().toString(), false);
            Bukkit.getPluginManager().getPlugin("BeaconFly").saveConfig();
        }
        player.sendMessage("Beacon flight has been set to " + config.getConfigurationSection("Players").getBoolean(player.getUniqueId().toString()));
        return true;
    }
}

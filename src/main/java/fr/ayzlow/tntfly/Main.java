package fr.ayzlow.tntfly;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {

    private boolean isEnabled;
    private double horizontalMotion;
    private double verticalMotion;
    private boolean requirePermission;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        isEnabled = config.getBoolean("enabled");
        horizontalMotion = config.getDouble("horizontal", 3.0);
        verticalMotion = config.getDouble("vertical", 0.75);
        requirePermission = config.getBoolean("permission", false);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TNTFly Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TNTFly Plugin Disabled!");
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        if (!isEnabled) return;

        if (event.getEntity() instanceof Entity && event.getEntity().getType().toString().equals("PRIMED_TNT")) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().distance(event.getEntity().getLocation()) <= 5) {
                    if (!requirePermission || player.hasPermission("tntfly.use")) {

                        Vector direction = player.getLocation().toVector().subtract(event.getEntity().getLocation().toVector());

                        direction.setY(verticalMotion);

                        direction.normalize().multiply(horizontalMotion);

                        player.setVelocity(direction);

                        player.damage(0);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(false);
            }
        }
    }
}

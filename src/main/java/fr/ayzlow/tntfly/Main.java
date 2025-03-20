package fr.ayzlow.tntfly;

import fr.ayzlow.tntfly.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {

    private boolean isEnabled;
    private double horizontalMotion;
    private double verticalMotion;
    private boolean requirePermission;
    private boolean fallDamageEnabled;
    private double fallDamageMultiplier;
    private double flyRayon;

    @Override
    public void onEnable() {
        VersionChecker.someMethod();
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        isEnabled = config.getBoolean("enabled");
        horizontalMotion = config.getDouble("horizontal", 3.0);
        verticalMotion = config.getDouble("vertical", 0.75);
        requirePermission = config.getBoolean("permission", false);
        fallDamageEnabled = config.getBoolean("fall-damage.enabled", false);
        fallDamageMultiplier = config.getDouble("fall-damage.damage", 1);
        flyRayon = config.getDouble("rayon", 5);

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[TNT-Fly] Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("[TNT-Fly] Plugin Disabled!");
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent event) {
        if (!isEnabled) return;

        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();

            event.setCancelled(true);

            for (Player player : Bukkit.getOnlinePlayers()) {
                double distance = player.getLocation().distance(tnt.getLocation());

                if (distance <= flyRayon) {
                    if (!requirePermission || player.hasPermission("tntfly.use")) {
                        Vector direction = player.getLocation().toVector().subtract(tnt.getLocation().toVector());
                        direction.setY(verticalMotion);

                        double distanceFactor = 1 - Math.pow((distance / flyRayon), 2);
                        direction.normalize().multiply(horizontalMotion * distanceFactor);

                        player.setVelocity(direction);

                        Bukkit.getScheduler().runTask(this, () -> {
                            player.getWorld().createExplosion(
                                    tnt.getLocation().getX(),
                                    tnt.getLocation().getY(),
                                    tnt.getLocation().getZ(),
                                    0f, false, false);
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (fallDamageEnabled) {
                double baseFallDamage = event.getDamage();

                if (fallDamageMultiplier == 0) {
                    event.setCancelled(true);
                    return;
                }

                double newFallDamage = baseFallDamage * fallDamageMultiplier;

                event.setDamage(newFallDamage);
            }
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(false);
            }
        }
    }
}

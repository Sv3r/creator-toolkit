package be.sv3r.util;

import be.sv3r.CreatorToolkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class EntityUtil {
    public static void spawnFirework(Location location, Color color, long detonateTime) {
        ItemStack fireworkItem = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) fireworkItem.getItemMeta();
        meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.BALL).withFlicker().withTrail().build());
        meta.setPower(3);
        fireworkItem.setItemMeta(meta);

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        firework.setItem(fireworkItem);

        CreatorToolkit.getScheduler().runTaskLater(CreatorToolkit.getPlugin(), firework::detonate, detonateTime);
    }
}

package be.sv3r.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Map;

public class TeleportStick implements Listener {

    private final Map<Player, Entity> selectedEntitys = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack blazeRod = new ItemStack(Material.BLAZE_ROD, 1);

        if (player.getInventory().getItemInMainHand().isSimilar(blazeRod) && player.hasPermission("creatortoolkit.teleportstick")) {
            selectedEntitys.put(player, event.getRightClicked());

            TextComponent entitySaved = Component.text("\ue007 ")
                    .append(Component.text(event.getRightClicked().getName(), TextColor.color(0xa9822b)))
                    .append(Component.text(" saved", TextColor.color(0xffffff)));

            player.sendMessage(entitySaved);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack blazeRod = new ItemStack(Material.BLAZE_ROD, 1);
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().isSimilar(blazeRod) && player.hasPermission("creatortoolkit.teleportstick")) {
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR)) {

                Entity selectedEntity = selectedEntitys.get(player);

                if (selectedEntity != null) {
                    RayTraceResult rayTraceResult = player.rayTraceBlocks(100);

                    if (rayTraceResult != null) {

                        TextComponent teleportEnity = Component.text("\ue007 ")
                                .append(Component.text("Teleported ", TextColor.color(0xffffff)))
                                .append(Component.text(selectedEntity.getName(), TextColor.color(0xa9822b)));

                        Block targetBlock = rayTraceResult.getHitBlock();

                        int x = targetBlock.getX();
                        int y = targetBlock.getY();
                        int z = targetBlock.getZ();

                        selectedEntity.teleport(new Location(player.getWorld(), x, y, z));
                        selectedEntitys.remove(player);
                        player.sendMessage(teleportEnity);

                    } else {

                        TextComponent noBlockHit = Component.text("\ue007 ")
                                .append(Component.text("Je bent te ver", TextColor.color(0xffffff)));

                        player.sendMessage(noBlockHit);
                    }
                } else {

                    TextComponent noEntitySelected = Component.text("\ue007 ")
                            .append(Component.text("Je hebt geen entity geselecteerd", TextColor.color(0xffffff)));

                    player.sendMessage(noEntitySelected);
                }
            }
        }
    }
}

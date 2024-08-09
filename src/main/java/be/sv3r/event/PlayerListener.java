package be.sv3r.event;

import be.sv3r.util.Keys;
import be.sv3r.util.Permissions;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final Map<UUID, Entity> selectedEntities = new HashMap<>();
    private final static TextComponent teleportStickPrefix = Component.text("[").style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
            .append(Component.text("Teleport Stick").style(Style.style(NamedTextColor.DARK_RED, TextDecoration.BOLD)))
            .append(Component.text("]").style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.BOLD)));

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        @NotNull ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getItemMeta() != null) {
            PersistentDataContainerView mainHandItemContainer = itemInMainHand.getPersistentDataContainer();
            if (mainHandItemContainer.has(Keys.TELEPORT_STICK)) {
                if (checkTeleportStickPermission(player)) return;

                selectedEntities.put(player.getUniqueId(), event.getRightClicked());

                Component entitySelectedMessage = teleportStickMessage(
                        Component.text(event.getRightClicked().getName()).style(Style.style(NamedTextColor.RED, TextDecoration.BOLD))
                                .appendSpace().append(Component.text("selected.").color(NamedTextColor.GRAY))
                );
                player.sendMessage(entitySelectedMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        @NotNull ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() == null) return;

        PersistentDataContainerView mainHandItemContainer = player.getInventory().getItemInMainHand().getPersistentDataContainer();
        if (mainHandItemContainer.has(Keys.TELEPORT_STICK)) {
            if (checkTeleportStickPermission(player)) return;

            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                Entity selectedEntity = selectedEntities.get(player.getUniqueId());
                RayTraceResult rayTraceResult = player.rayTraceBlocks(100.0D);

                if (selectedEntity == null) return;
                if (rayTraceResult == null) {
                    return;
                }

                Block targetBlock = rayTraceResult.getHitBlock();

                if (targetBlock == null) return;

                Location location = targetBlock.getLocation();
                selectedEntity.teleport(location.add(0.0D, 1.0D, 0.0D));
                selectedEntities.remove(player.getUniqueId());

                Component entityTeleportedMessage = teleportStickMessage(
                        Component.text(selectedEntity.getName()).style(Style.style(NamedTextColor.RED, TextDecoration.BOLD))
                                .appendSpace().append(Component.text("teleported.").color(NamedTextColor.GRAY))
                );

                player.sendMessage(entityTeleportedMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        @NotNull ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (checkTeleportStickPermission(player)) return;
        if (itemInMainHand.getItemMeta() == null) return;

        PersistentDataContainerView mainHandItemContainer = player.getInventory().getItemInMainHand().getPersistentDataContainer();
        if (mainHandItemContainer.has(Keys.TELEPORT_STICK)) {
            event.setCancelled(true);
        }
    }

    private static boolean checkTeleportStickPermission(Player player) {
        if (!player.hasPermission(Permissions.TELEPORT_STICK)) {
            player.sendMessage(Component.text("You don't have permission to use the teleport stick.").color(NamedTextColor.RED));
            return true;
        }
        return false;
    }

    private static Component teleportStickMessage(Component component) {
        return teleportStickPrefix.appendSpace().append(component);
    }
}
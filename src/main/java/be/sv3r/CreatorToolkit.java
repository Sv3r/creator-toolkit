package be.sv3r;

import be.sv3r.command.CreatorCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class CreatorToolkit extends JavaPlugin {
    public static final String ADMIN_PERMISSION = "creator.admin";

    public static CreatorToolkit getPlugin() {
        return getPlugin(CreatorToolkit.class);
    }

    public static BukkitScheduler getScheduler() {
        return getPlugin().getServer().getScheduler();
    }

    @Override
    public void onEnable() {
        registerCommands();
    }

    private void registerCommands() {
        @NotNull LifecycleEventManager<Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("creator", "", new CreatorCommand());
        });
    }
}
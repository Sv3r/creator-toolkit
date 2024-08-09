package be.sv3r;

import be.sv3r.command.CreatorAdminCommand;
import be.sv3r.util.TeleportStick;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unused"})
public class CreatorToolkit extends JavaPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger("creator");

    public static CreatorToolkit getPlugin() {
        return getPlugin(CreatorToolkit.class);
    }

    @Override
    public void onEnable() {
        registerCommands();

        getServer().getPluginManager().registerEvents(new TeleportStick(), this);
    }

    private void registerCommands() {
        final LifecycleEventManager<Plugin> lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            new CreatorAdminCommand().register(this, commands);
        });
    }
}
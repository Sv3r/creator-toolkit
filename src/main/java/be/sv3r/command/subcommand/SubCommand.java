package be.sv3r.command.subcommand;

import be.sv3r.CreatorToolkit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface SubCommand {
    String getCommand();

    String getUsage();

    void execute(CommandSender commandSender, String[] args);

    Collection<String> suggest(CommandSender commandSender, String[] args);

    default void sendUsage(Audience target) {
        target.sendMessage(
                Component.text(getUsage())
                        .color(NamedTextColor.RED)
        );
    }

    default Boolean canUse(CommandSender sender) {
        return sender instanceof Player;
    }

    default String permission() {
        return CreatorToolkit.ADMIN_PERMISSION;
    }
}

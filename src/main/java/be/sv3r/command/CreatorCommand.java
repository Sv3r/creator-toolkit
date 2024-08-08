package be.sv3r.command;

import be.sv3r.command.subcommand.CountdownCommand;
import be.sv3r.command.subcommand.SubCommand;
import be.sv3r.command.subcommand.WinCommand;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class CreatorCommand implements BasicCommand {
    public static final List<SubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new WinCommand());
        subCommands.add(new CountdownCommand());
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                CommandSender sender = commandSourceStack.getSender();
                if (!sender.hasPermission(subCommand.permission())) {
                    sender.sendMessage(
                            Component.text("You don't have permission to execute this command.")
                                    .color(NamedTextColor.RED)
                    );
                    return;
                }
                if (!subCommand.canUse(sender)) {
                    sender.sendMessage(
                            Component.text("Only players can execute this command.")
                                    .color(NamedTextColor.RED)
                    );
                    return;
                }

                if (args[0].equalsIgnoreCase(subCommand.getCommand())) {
                    subCommand.execute(commandSourceStack.getSender(), args);
                }
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length == 0) {
            return subCommands.stream().map(SubCommand::getCommand).collect(Collectors.toSet());
        } else {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getCommand())) {
                    return subCommand.suggest(commandSourceStack.getSender(), args);
                }
            }
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}

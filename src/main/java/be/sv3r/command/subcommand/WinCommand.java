package be.sv3r.command.subcommand;

import be.sv3r.CreatorToolkit;
import be.sv3r.util.EntityUtil;
import be.sv3r.util.task.CountdownRunnable;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class WinCommand implements SubCommand {
    @Override
    public String getCommand() {
        return "win";
    }

    @Override
    public String getUsage() {
        return "/creator win [<player>]";
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length != 2) {
            sendUsage(commandSender);
            return;
        }

        Player winner = Bukkit.getPlayer(args[1]);
        if (winner != null) {
            CountdownRunnable countdownRunnable = new CountdownRunnable(
                    CreatorToolkit.getPlugin(),
                    10,
                    () -> beforeCountdown(winner),
                    WinCommand::afterCountdown,
                    (runnable) -> duringCountdown(winner)
            );
            countdownRunnable.scheduleTimer();
        }
    }

    @Override
    public Collection<String> suggest(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet());
        }
        return Set.of();
    }

    private static void beforeCountdown(Player winner) {
        CreatorToolkit.getPlugin().getServer().getOnlinePlayers().forEach(
                player -> {
                    Sound sound = Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.MASTER, 1F, 1F);
                    player.playSound(sound);

                    showWinTitle(player, winner);
                }
        );
    }

    private static void afterCountdown() {
    }

    private static void duringCountdown(Player winner) {
        EntityUtil.spawnFirework(winner.getLocation().add(0, 1, 0), Color.fromRGB(255, 136, 0), 20L);
    }

    private static void showWinTitle(final Audience target, final Player winner) {
        final Component mainTitle = Component.text().content(winner.getName()).color(TextColor.fromHexString("#ff8800"))
                .append(Component.text().content(" wint!").color(TextColor.fromHexString("#ffd9bf"))).build();

        final Title title = Title.title(mainTitle, Component.empty());
        target.showTitle(title);
    }
}
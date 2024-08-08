package be.sv3r.command.subcommand;

import be.sv3r.CreatorToolkit;
import be.sv3r.util.CommandUtil;
import be.sv3r.util.task.CountdownRunnable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public class CountdownCommand implements SubCommand {
    @Override
    public String getCommand() {
        return "countdown";
    }

    @Override
    public String getUsage() {
        return "/creator countdown [<duringTitle>] [<stopTitle>] [<duration>]";
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        String[] compiledArgs = CommandUtil.quotedSpaces(args);

        if (compiledArgs.length != 4) {
            sendUsage(commandSender);
            return;
        }

        String duringTitle = compiledArgs[1];
        String stopTitle = compiledArgs[2];
        int duration;

        try {
            duration = Integer.parseInt(compiledArgs[3]);
        } catch (Exception e) {
            sendUsage(commandSender);
            return;
        }

        CountdownRunnable countdownRunnable = new CountdownRunnable(
                CreatorToolkit.getPlugin(),
                duration,
                CountdownCommand::startCountdown,
                () -> stopCountdown(stopTitle),
                (runnable) -> duringCountdown(runnable, duringTitle)
        );
        countdownRunnable.scheduleTimer();
    }

    @Override
    public Collection<String> suggest(CommandSender commandSender, String[] args) {
        return List.of();
    }

    private static void startCountdown() {
    }

    private static void stopCountdown(String stopTitle) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(1000));

            final Component mainTitle = Component.text(stopTitle).style(Style.style(TextDecoration.BOLD, NamedTextColor.RED));

            final Title title = Title.title(mainTitle, Component.empty(), times);
            player.showTitle(title);

            Sound sound = Sound.sound(Key.key("item.goat_horn.sound.0"), Sound.Source.MASTER, 1F, 1F);
            player.playSound(sound, Sound.Emitter.self());
        });

    }

    private static void duringCountdown(CountdownRunnable runnable, String duringTitle) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(1000));

            final Component mainTitle = Component.text(duringTitle, NamedTextColor.RED).style(Style.style(TextDecoration.BOLD, NamedTextColor.RED));
            final Component subtitle = Component.text(">" + runnable.getSecondsLeft() + "<").style(Style.style(TextDecoration.BOLD, NamedTextColor.WHITE));

            final Title title = Title.title(mainTitle, subtitle, times);
            player.showTitle(title);

            Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1F, 1F);
            player.playSound(sound, Sound.Emitter.self());
        });
    }
}

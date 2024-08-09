package be.sv3r.command;

import be.sv3r.CreatorToolkit;
import be.sv3r.command.annotation.SubCommand;
import be.sv3r.task.CountdownTask;
import be.sv3r.util.EntityUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

public class CreatorAdminCommand extends AnnotatedCommand {
    public CreatorAdminCommand() {
        super("creatoradmin", "Creator admin command.", List.of("ca"));
    }

    @Override
    public int onRoot(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    //<editor-fold desc="win subcommand">
    @SubCommand
    public ArgumentBuilder<CommandSourceStack, ?> onWin() {
        return Commands.literal("win")
                .then(Commands.argument("player", ArgumentTypes.player()).executes((source) -> {
                            CommandSourceStack sourceStack = source.getSource();
                            Player winner = source.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(sourceStack).getFirst();

                            CountdownTask countdownTask = new CountdownTask(CreatorToolkit.getPlugin(), 5,
                                    () -> beforeWinCountdown(winner),
                                    () -> {
                                    },
                                    (runnable) -> duringWinCountdown(winner)
                            );
                            countdownTask.scheduleTask();
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    private static void beforeWinCountdown(Player winner) {
        CreatorToolkit.getPlugin().getServer().getOnlinePlayers().forEach(
                player -> {
                    Sound sound = Sound.sound(Key.key("ui.toast.challenge_complete"), Sound.Source.MASTER, 1F, 1F);
                    player.playSound(sound);

                    showWinTitle(player, winner);
                }
        );
    }

    private static void duringWinCountdown(Player winner) {
        EntityUtil.spawnFirework(winner.getLocation().add(0, 1, 0), Color.ORANGE, 20L);
    }

    private static void showWinTitle(final Audience target, final Player winner) {
        final Component mainTitle = Component.text().content(winner.getName()).color(TextColor.fromHexString("#ff8800"))
                .append(Component.text().content(" wint!").color(TextColor.fromHexString("#ffd9bf"))).build();

        final Title title = Title.title(mainTitle, Component.empty());
        target.showTitle(title);
    }
    //</editor-fold>

    //<editor-fold desc="countdown subcommand">
    @SubCommand
    public ArgumentBuilder<CommandSourceStack, ?> onCountdown() {
        return Commands.literal("countdown")
                .then(Commands.argument("countdownTitle", ArgumentTypes.component())
                        .then(Commands.argument("stopTitle", ArgumentTypes.component())
                                .then(Commands.argument("color", ArgumentTypes.namedColor())
                                        .then(Commands.argument("duration", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    Component countdownTitle = context.getArgument("countdownTitle", Component.class);
                                                    Component stopTitle = context.getArgument("stopTitle", Component.class);
                                                    int duration = context.getArgument("duration", Integer.class);
                                                    NamedTextColor color = context.getArgument("color", NamedTextColor.class);

                                                    CountdownTask countdownRunnable = new CountdownTask(
                                                            CreatorToolkit.getPlugin(),
                                                            duration,
                                                            () -> {
                                                            },
                                                            () -> stopCountdownCountdown(stopTitle, color),
                                                            (task) -> duringCountdownCountdown(task, countdownTitle, color)
                                                    );
                                                    countdownRunnable.scheduleTask();

                                                    return 0;
                                                })
                                        )
                                )
                        ));
    }

    private static void stopCountdownCountdown(Component stopTitle, NamedTextColor color) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(1000));

            final Title title = Title.title(stopTitle.style(Style.style(color, TextDecoration.BOLD)), Component.empty(), times);
            player.showTitle(title);

            Sound sound = Sound.sound(Key.key("item.goat_horn.sound.0"), Sound.Source.MASTER, 1F, 1F);
            player.playSound(sound, Sound.Emitter.self());
        });

    }

    private static void duringCountdownCountdown(CountdownTask task, Component duringTitle, NamedTextColor color) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final Component subtitle = Component.text(">" + task.getSecondsLeft() + "<").style(Style.style(TextDecoration.BOLD, NamedTextColor.WHITE));

            final Title.Times times = Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1000), Duration.ofMillis(1000));
            final Title title = Title.title(duringTitle.style(Style.style(color, TextDecoration.BOLD)), subtitle, times);
            player.showTitle(title);

            Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1F, 1F);
            player.playSound(sound, Sound.Emitter.self());
        });
    }
    //</editor-fold>
}

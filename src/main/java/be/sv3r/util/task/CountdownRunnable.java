package be.sv3r.util.task;

import be.sv3r.CreatorToolkit;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public class CountdownRunnable implements Runnable {
    private final CreatorToolkit plugin;
    private Integer assignedTaskId;

    private final int seconds;
    private int secondsLeft;

    private final Consumer<CountdownRunnable> everySecond;
    private final Runnable beforeTimer;
    private final Runnable afterTimer;

    public CountdownRunnable(CreatorToolkit plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<CountdownRunnable> everySecond) {
        this.plugin = plugin;

        this.seconds = seconds;
        this.secondsLeft = seconds;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {
        if (secondsLeft < 1) {
            afterTimer.run();
            if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
            return;
        }

        if (secondsLeft == seconds) beforeTimer.run();
        everySecond.accept(this);
        secondsLeft--;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }
}
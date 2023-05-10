package at.nbsgames.explobattle.utility;

import at.nbsgames.explobattle.Main;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.ref.Cleaner;

public class BukkitTaskWrapper {

    final BukkitTask task;
    public BukkitTaskWrapper(String name, BukkitTask task) {
        this.task = task;
        Cleaner.create().register(this, new CleanerRunnable(task, name));
    }

    public void cancel() {
        if (task != null) task.cancel();
    }

    public boolean isCancelled() {
        return task.isCancelled();
    }

    private static class CleanerRunnable implements Runnable {
        BukkitTask task;
        String name;

        private CleanerRunnable(BukkitTask task, String name) {
            this.task = task;
            this.name = name;
        }

        @Override
        public void run() {
            if(!task.isCancelled()){
                task.cancel();
                JavaPlugin.getPlugin(Main.class).getLogger().warning("Time \"" + name + "\" wasn't cancelled. Cancelling now");
            }
        }
    }
}

package matthewa.chaosmod;

public interface RepeatingAction extends Action {
    // Since spigot won't let you spawn entities without causing an IllegalStateException, this is where you put the code that cannot be run in an asynchronous task.
    Runnable runnable = null;
    long time = 0;
    // Freq is amount of ticks to wait in between each "iteration"
    // Times run is a variable where each time the task is run, it will add to it until it's the target. NOTE: 1 tick is a twentieth of a second, so there are 20 of them in a second
    double freq = 2;
    int timesRun = 0;
    int targetTime = (int) ((time * 20) / freq);
    long getTime();
    int getRunnableId();
}

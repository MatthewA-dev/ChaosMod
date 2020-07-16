package matthewa.chaosmod;

import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.util.HashMap;

public interface Action {
    int runnableId = -1;
    // This is the runnable that contains the action being preformed on the players, if you want to create a new action, I recommend using the ChaosMod.getPlayers() command
    // because this will exclude the players that have been chosen to be excluded.
    BukkitRunnable run = null;
    // Prob is the probability of this action being chosen. Choose 1.
    double prob = 1.0;
    // Name is what's going to display in the chat when the action is run.
    String name = "";
    void doAction();
    double getProb();
    void load(JSONObject object);
    JSONObject save();
    String getName();
    // Absolute name is set because some actions have changing names for displaying in chat. Set the absolute name here, like "Entity Spawn"
    String getAbsoluteName();
    void invokeAction(HashMap<String,String> args);
    default String getHelp() {
        return "There is no help for this action.";
    }
}

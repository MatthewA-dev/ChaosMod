package matthewa.chaosmod.Actions.Repeating;

import matthewa.chaosmod.ChaosMod;
import matthewa.chaosmod.RepeatingAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;

public class ArrowRain implements RepeatingAction {
    public String help = "Spawns an arrow rain upon your victim.\n" +
            "Arguments:\n" +
            "-player: Specify the player, specify @a for everyone\n" +
            "-height: The height of which to spawn your arrows at. Default 10\n" +
            "-time: How long to execute the task for. Default 10\n" +
            "-frequency: How frequent this task runs, for example if you specify 5, it will wait 5 ticks inbetween spawning arrows. Default 3";
    double height = 10;
    int runnableId;
    double prob = 1;
    String name = "Arrow Rain";
    long time = 10;
    double freq = 3;
    private int timesRun = 0;
    private final int targetTime = (int) ((time * 20) / freq);
    Runnable run = new Runnable(){
        @Override
        public void run() {
            timesRun += 1;
            Bukkit.getScheduler().runTask(ChaosMod.getSelf(), runnable);
            if(timesRun == targetTime){
                timesRun = 0;
                Bukkit.getScheduler().cancelTask(runnableId);
            }
        }
    };
    Runnable runnable = () -> {
        Collection<? extends Player> ps = ChaosMod.getPlayers();
        for (Player p: ps) {
            p.getWorld().spawnEntity(p.getLocation().add(0,height,0), EntityType.ARROW);
        }
    };
    @Override
    public void doAction() {
        runnableId = Bukkit.getScheduler().runTaskTimerAsynchronously(ChaosMod.getSelf(), run, 0, (long) freq).getTaskId();
    }

    @Override
    public double getProb() {
        return prob;
    }

    @Override
    public void load(JSONObject object) {
        try {
            prob = Double.parseDouble(String.valueOf( object.get("Probability")));
            time = Long.parseLong(String.valueOf(object.get("Time")));
            freq = Double.parseDouble(String.valueOf(object.get("Frequency")));
            height = Double.parseDouble(String.valueOf(object.get("Height")));
            name =  String.valueOf(object.get("Name"));
        }catch(JSONException | IllegalFormatConversionException | NumberFormatException  ignored){ }
    }
    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("Name", name);
        obj.put("Probability", prob);
        obj.put("Time", time);
        obj.put("Frequency", freq);
        obj.put("Height", height);
        return obj;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbsoluteName() {
        return name;
    }

    @Override
    public void invokeAction(HashMap<String, String> args) {
        if(args.get("player").equals("@a") ) {
            doAction();
        }else{
            double frequency;
            long length;
            double tallness = height;
            try {
                frequency = Double.parseDouble(args.get("frequency"));
            } catch (NumberFormatException | NullPointerException e) {
                frequency = freq;
            }
            try {
                length = Long.parseLong(args.get("time"));
            } catch (NumberFormatException | NullPointerException e) {
                length = time;
            }
            try {
                tallness = Double.parseDouble(args.get("height"));
            } catch (NumberFormatException | NullPointerException e) {
                tallness = height;
            }
            int amount = (int) ((length * 20) / frequency);
            final int[] times = {0};
            double finalTallness = tallness;
            Runnable spawnArrow = () -> {
                Player p = Bukkit.getPlayer(args.get("player"));
                p.getWorld().spawnEntity(p.getLocation().add(0, finalTallness,0), EntityType.ARROW);
            };
            Runnable tempRun = () -> {
                times[0] += 1;
                Bukkit.getScheduler().runTask(ChaosMod.getSelf(), spawnArrow);
                if (times[0] == amount) {
                    timesRun = 0;
                    Bukkit.getScheduler().cancelTask(runnableId);
                }
            };
            runnableId = Bukkit.getScheduler().runTaskTimerAsynchronously(ChaosMod.getSelf(), tempRun, 0, (long) frequency).getTaskId();
        }
    }

    @Override
    public String getHelp() {
        return help;
    }

    public int getRunnableId() {
        return runnableId;
    }

    @Override
    public long getTime() {
        return time;
    }
}

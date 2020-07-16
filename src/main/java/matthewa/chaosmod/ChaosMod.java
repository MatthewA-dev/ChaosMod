package matthewa.chaosmod;

import matthewa.chaosmod.Actions.OnCommandCompletion;
import matthewa.chaosmod.Actions.Regular.FullHealth;
import matthewa.chaosmod.Actions.Regular.GiveItem;
import matthewa.chaosmod.Actions.Regular.SandTrap;
import matthewa.chaosmod.Actions.Regular.SpawnEntity;
import matthewa.chaosmod.Actions.Repeating.ArrowRain;
import matthewa.chaosmod.Commands.ChaosModCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public final class ChaosMod extends JavaPlugin {
    public BukkitTask task;
    public static double delay = 10;
    public Runnable chaosTask = new Runnable() {
        @Override
        public void run() {
            if(state){Bukkit.getScheduler().runTask(self,runTask);}}};
    public Runnable runTask = this::doAction;
    public static ChaosMod self;
    public boolean state = false;
    public static ArrayList<Action> actionList = new ArrayList<Action>(Arrays.asList(
            new SpawnEntity(),
            new FullHealth(),
            new ArrowRain(),
            new GiveItem(),
            new SandTrap()));
    public static ArrayList<String> actionIds = new ArrayList<>();
    @Override
    public void onEnable() {
        self = this;
        getCommand("chaos").setExecutor(new ChaosModCommand());
        getCommand("chaos").setTabCompleter(new OnCommandCompletion());
        try {
            loadProbs();
        } catch (FileNotFoundException ignored) {}
        for (Action action: actionList) {
            actionIds.addAll(parseIds(action));
        }
        System.out.println(actionIds);
    }

    @Override
    public void onDisable(){
        try {
            saveProbs();
        } catch (IOException ignored) {}
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static void loadProbs() throws FileNotFoundException {
        Bukkit.getScheduler().cancelTasks(self);
        File config = new File("plugins\\ChaosConfig.json");
        JSONObject obj;
        Scanner scan = new Scanner(config);
        StringBuilder JString = new StringBuilder();
        try {
            while(scan.hasNext()) {
                JString.append(scan.nextLine());
            }
            obj = new JSONObject(JString.toString().replace("\n",""));
        }catch(JSONException | NoSuchElementException e){
            Bukkit.getLogger().info("Nothing in ChaosConfig.json, ignoring.");
            return;
        }
        try {
            delay = Double.parseDouble(String.valueOf(obj.get("Delay")));
        }catch(NumberFormatException | JSONException ignored){
            System.out.println("An error occurred while reading delay, setting default 10.");
        }
        for (Action act: actionList) {
            try {
                act.load((JSONObject) obj.get(act.getName()));
            }catch(JSONException ignored){
                System.out.println("An error occurred while reading " + act.getName());
            }
        }
    }

    public static void saveProbs() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("Delay",delay);
        for (Action act: actionList) {
            obj.put(act.getAbsoluteName(), act.save());
        }
        File config = new File("plugins\\ChaosConfig.json");
        FileWriter fw = new FileWriter(config);
        fw.write(obj.toString());
        fw.flush();
        fw.close();
    }

    public void doAction(){
        Action action = null;
        double totalWeight = 0.0d;
        for (Action actionTemp: actionList) {
            totalWeight += actionTemp.getProb();
        }
        // I can't think of a good name here, but this variable is supposed to keep the while loop going if the task is already running in order to not extend already running repeating actions.
        // If it's false, then it isn't running and it will continue to find a new one that isn't running. I do not recommend that you have only one action in your list, or have at least one non repeating action as this will timeout the server.
        boolean isRunning = true;
        while(isRunning){
            // Sum of weights algorithm. Repeats until there is one that isn't currently running.
            int randomIndex = -1;
            double random = Math.random() * totalWeight;
            for (int j = 0; j < actionList.size(); ++j)
            {
                random -= actionList.get(j).getProb();
                if (random <= 0.0d)
                {
                    randomIndex = j;
                    break;
                }
            }
            action = actionList.get(randomIndex);
            try {
                if(action instanceof RepeatingAction) {
                    RepeatingAction act = (RepeatingAction) action;
                    isRunning = Bukkit.getScheduler().isCurrentlyRunning(act.getRunnableId());
                }else{
                    isRunning = false;
                }
            }catch(IllegalStateException | NullPointerException e){
                isRunning = false;
            }
        }
        action.doAction();
        if(action instanceof RepeatingAction){
            Bukkit.broadcastMessage(action.getName() + " for " + ((RepeatingAction) action).getTime() + " seconds");
        }else {
            Bukkit.broadcastMessage(action.getName());
        }
    }

    public List<String> parseIds(Action action){
        ArrayList<String> ids = new ArrayList<>();
        if(action instanceof SubsetAction){
            String actionId = action.getAbsoluteName().replace(" ","");
            SubsetAction subAction = (SubsetAction) action;
            ArrayList<Action> acts = subAction.getActionList();
            for(Action act: acts) {
                List<String> idList = parseIds(act);
                for (String id: idList) {
                    ids.add(actionId + "." +  id);
                }
            }
        }else{
            return Collections.singletonList(action.getName().replace(" ", ""));
        }
        return ids;
    }

    public static HashMap<String,String> parseArgs(List<String> args){
        HashMap<String,String> dictArgs = new HashMap<String,String>();
        for (int i = 0; i + 1 < args.size(); i++) {
            String arg = args.get(i);
            if(arg.startsWith("-")){
                try {
                    dictArgs.put(arg.replace("-",""), args.get(i + 1));
                }catch(ArrayIndexOutOfBoundsException e){
                    return null;
                }
            }
        }
        return dictArgs;
    }

    public static void updateChaos(){
        try {
            if (self.state && !Bukkit.getScheduler().isCurrentlyRunning(self.task.getTaskId())) {
                self.task = Bukkit.getScheduler().runTaskTimerAsynchronously(self,self.chaosTask, 0, (long) (20 * delay));
                Bukkit.broadcastMessage(ChatColor.GREEN + "Enabling ChaosMod");
            } else {
                Bukkit.getScheduler().cancelTask(self.task.getTaskId());
                Bukkit.broadcastMessage(ChatColor.RED + "Disabling ChaosMod");
                Bukkit.getScheduler().cancelTasks(self);
            }
        }catch(IllegalStateException | NullPointerException e){
            if(self.state){
                self.task = Bukkit.getScheduler().runTaskTimerAsynchronously(self,self.chaosTask, 0, (long) (20 * delay));
                Bukkit.broadcastMessage(ChatColor.GREEN + "Enabling ChaosMod");
            }
        }
    }
    // Returns formatted online players. If you are making your own actions, use this for players as it contains the players that will be excluded.
    public static java.util.Collection<? extends org.bukkit.entity.Player> getPlayers(){
        List<Player> players = (List<Player>) Bukkit.getServer().getOnlinePlayers();
        for (Player p: ChaosModCommand.specs) {
            players.remove(p);
        }
        return players;
    }
    // Returns action based off of an action id structured as EntitySpawn.SpawnCow
    public static Action getAction(String id){
        String[] idSections = id.split("\\.");
        Action tempAction = null;
        for (String idSection : idSections) {
            if (tempAction == null) {
                for (Action act : actionList) {
                    if (act.getName().replace(" ", "").equals(idSection)) {
                        tempAction = act;
                        break;
                    }
                }
            } else {
                if (tempAction instanceof SubsetAction) {
                    SubsetAction subAction = (SubsetAction) tempAction;
                    for (Action act : subAction.getActionList()) {
                        if (act.getName().replace(" ", "").equals(idSection)) {
                            tempAction = act;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
        }
        return tempAction;
    }
    public static boolean getState(){
        return self.state;
    }
    public static ChaosMod getSelf() { return self; }
    public static void setState(boolean chaosState) {
        self.state = chaosState;
    }
    public static ArrayList<String> getActionIds() {
        return actionIds;
    }

}
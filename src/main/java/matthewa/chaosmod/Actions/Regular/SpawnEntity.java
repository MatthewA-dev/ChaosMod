package matthewa.chaosmod.Actions.Regular;

import matthewa.chaosmod.Action;
import matthewa.chaosmod.Actions.Regular.ActionSubsets.Entities.Cow;
import matthewa.chaosmod.Actions.Regular.ActionSubsets.Entities.Sheep;
import matthewa.chaosmod.SubsetAction;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;

public class SpawnEntity implements SubsetAction {
    public String help = "If you specify this, it's going to execute a random spawn entity task on the given player.\n" +
            "Arguments:\n" +
            "-player: Specify the player, specify @a for everyone";
    public double prob = 1;
    public String name = "Spawn Entity";
    public ArrayList<Action> spawns = new ArrayList<>(Arrays.asList(
            new Sheep(),
            new Cow()));
    @Override
    public void doAction() {
        double total = 0;
        for (Action spawn: spawns) {
            total += spawn.getProb();
        }
        int randomIndex = -1;
        double random = Math.random() * total;
        for (int j = 0; j < spawns.size(); ++j)
        {
            random -= spawns.get(j).getProb();
            if (random <= 0.0d)
            {
                randomIndex = j;
                break;
            }
        }
        Action spawn = spawns.get(randomIndex);
        spawn.doAction();
        this.name = spawn.getName();
    }
    @Override
    public void load(JSONObject object) {
        try {
            prob = Double.parseDouble( String.valueOf(object.get("Probability")));
            JSONObject spawnObj = new JSONObject(object.get("Actions"));
            for (Action spawn: spawns) {
                spawn.load(new JSONObject(spawnObj.get(spawn.getName())));
            }
            name = String.valueOf(object.get("Name"));
        }catch(JSONException | IllegalFormatConversionException | NumberFormatException  ignored){ }
    }
    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("Name", "Spawn Entity");
        obj.put("Probability", prob);
        JSONObject actions = new JSONObject();
        for (Action spawn: spawns) {
            actions.put(spawn.getName(),spawn.save());
        }
        obj.put("Actions",actions);
        return obj;
    }
    @Override
    public double getProb() {
        return prob;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getAbsoluteName() {
        return "Spawn Entity";
    }

    @Override
    public void invokeAction(HashMap<String, String> args) {
        if(args.get("player") == null ||args.get("player").equals("@a")){
            doAction();
        }else{
            double total = 0;
            for (Action spawn: spawns) {
                total += spawn.getProb();
            }
            int randomIndex = -1;
            double random = Math.random() * total;
            for (int j = 0; j < spawns.size(); ++j)
            {
                random -= spawns.get(j).getProb();
                if (random <= 0.0d)
                {
                    randomIndex = j;
                    break;
                }
            }
            Action spawn = spawns.get(randomIndex);
            spawn.invokeAction(args);
            this.name = spawn.getName();
        }
    }

    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public ArrayList<Action> getActionList() {
        return spawns;
    }
}

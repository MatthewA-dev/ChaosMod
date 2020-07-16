package matthewa.chaosmod.Actions.Regular.ActionSubsets.Entities;

import matthewa.chaosmod.Action;
import matthewa.chaosmod.ChaosMod;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;

public class Sheep implements Action {
    public String help = "Spawns a sheep.\n" +
            "Arguments:\n" +
            "-player: Specify the player, specify @a for everyone";
    public String name = "Spawn Sheep";
    public Double prob = 1.0;
    @Override
    public void doAction() {
        Collection<? extends Player> ps = ChaosMod.getPlayers();
        for (Player p: ps) {
            p.getWorld().spawnEntity(p.getLocation(), EntityType.SHEEP);
        }
    }
    @Override
    public void load(JSONObject object) {
        prob = Double.parseDouble((String) object.get("Probability"));
        name = (String) object.get("Name");
    }

    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("Probability",prob);
        obj.put("Name",name);
        return obj;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getAbsoluteName() {
        return name;
    }

    @Override
    public void invokeAction(HashMap<String, String> args) {
        if(args.get("player").equals("@a")){
            doAction();
        }else{
            Bukkit.getPlayer(args.get("player")).getWorld().spawnEntity(Bukkit.getPlayer(args.get("player")).getLocation(), EntityType.SHEEP);
        }
    }

    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public double getProb() { return prob; }
}

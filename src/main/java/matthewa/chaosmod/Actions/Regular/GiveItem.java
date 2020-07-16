package matthewa.chaosmod.Actions.Regular;

import matthewa.chaosmod.Action;
import matthewa.chaosmod.ChaosMod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;

public class GiveItem implements Action {
    public String help = "Gives a random item to you.\n" +
            "Arguments:\n" +
            "-player: Specify the player, specify @a for everyone";
    public double prob = 1;
    public String name = "Give Item";
    @Override
    public void doAction() {
        Collection<? extends Player> ps = ChaosMod.getPlayers();
        for (Player p: ps) {
            p.getInventory().addItem(new ItemStack(Material.DIRT));
        }
    }
    @Override
    public double getProb() {
        return prob;
    }

    @Override
    public void load(JSONObject object) {
        try {
            prob = Double.parseDouble(String.valueOf(object.get("Probability")));
            name =  String.valueOf(object.get("Name"));
        }catch(JSONException | IllegalFormatConversionException | NumberFormatException ignored){ }
    }

    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("Name", name);
        obj.put("Probability", prob);
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
        if(args.get("player").equals("@a")){
            doAction();
        }else{
            Bukkit.getPlayer(args.get("player")).getInventory().addItem(new ItemStack(Material.DIRT));
        }
    }

    @Override
    public String getHelp() {
        return help;
    }

}

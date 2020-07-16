package matthewa.chaosmod.Actions.Regular;

import matthewa.chaosmod.Action;
import matthewa.chaosmod.ChaosMod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;

public class SandTrap implements Action {
    public String help = "Spawns a sand trap below you.\n" +
            "Arguments:\n" +
            "-player: Specify the player, specify @a for everyone. Defaults to self\n" +
            "-depth: How deep to make the sand trap. Default 7\n" +
            "-width: How wide to make the sand trap. Default 3\n" +
            "-length: How long to make the sand trap. Default 3";
    String name = "Sand Trap";
    Double prob = 2.0;
    int depth = 7;
    int width = 3;
    int length = 3;
    @Override
    public void doAction() {
        Collection<? extends Player> ps = ChaosMod.getPlayers();
        for (Player p: ps) {
            for (int y = 0; y < depth; y++) {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < length ; z++) {
                        if(y == 0) {
                            p.getWorld().getBlockAt(p.getLocation().add(x - width / 2, y, z - length / 2)).setType(Material.SAND);
                        }else{
                            p.getWorld().getBlockAt(p.getLocation().add(x - width / 2, y, z - length / 2)).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    @Override
    public void load(JSONObject object) {
        try{
            prob = Double.parseDouble(String.valueOf(object.get("Probability")));
            depth = Integer.parseInt(String.valueOf(object.get("Depth")));
            width = Integer.parseInt(String.valueOf(object.get("Width")));
            length = Integer.parseInt(String.valueOf(object.get("Length")));
            name = (String) object.get("Name");
        }catch(JSONException | IllegalFormatConversionException | NumberFormatException  ignored){ }
    }

    @Override
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("Probability",prob);
        obj.put("Depth",depth);
        obj.put("Width",width);
        obj.put("Length",length);
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
            int tempWidth = width;
            int tempDepth = depth;
            int tempLength = length;
            try {
                tempWidth = Integer.parseInt(args.get("width"));
            } catch (NumberFormatException | NullPointerException ignored) { }
            try {
                tempDepth = Integer.parseInt(args.get("depth"));
            } catch (NumberFormatException | NullPointerException ignored) { }
            try {
                tempLength = Integer.parseInt(args.get("length"));
            } catch (NumberFormatException | NullPointerException ignored) { }
            System.out.println(tempDepth + String.valueOf(tempDepth) + tempLength);
            Player p = Bukkit.getPlayer(args.get("player"));
            for (int y = 0; y < tempDepth; y++) {
                for (int z = 0; z < tempWidth; z++) {
                    for (int x = 0; x < tempLength ; x++) {
                        if(y == 0) {
                            System.out.println("SAND");
                            p.getWorld().getBlockAt(p.getLocation().add(tempWidth - width / 2, tempDepth, tempLength - length / 2)).setType(Material.SAND);
                        }else{
                            System.out.println("AIR!!!!!");
                            p.getWorld().getBlockAt(p.getLocation().add(tempWidth - width / 2, tempDepth, tempLength - length / 2)).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return help;
    }

    @Override
    public double getProb() { return prob; }
}

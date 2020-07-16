package matthewa.chaosmod.Actions;

import matthewa.chaosmod.ChaosMod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class OnCommandCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch(args.length) {
            case 1:
                return Arrays.asList("toggle", "enable", "disable", "reload", "save", "spectators", "run", "cancelTasks", "help");
            case 2:
                if(args[0].equals("spectators")) {
                    return Arrays.asList("add", "remove", "list");
                }else if(args[0].equals("run") || args[0].equals("help")){
                    return ChaosMod.getActionIds();
                }
                break;
            case 3:
                if(args[1].equals("add") || args[1].equals("remove")) {
                    Collection<? extends Player> ps = Bukkit.getOnlinePlayers();
                    List<String> psName = new ArrayList<>();
                    for (Player p : ps) {
                        psName.add(p.getName());
                    }
                    return psName;
                }
                break;
        }
        return Collections.emptyList();
    }
}

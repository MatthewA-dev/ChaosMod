package matthewa.chaosmod.Commands;

import matthewa.chaosmod.Action;
import matthewa.chaosmod.ChaosMod;
import matthewa.chaosmod.RepeatingAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChaosModCommand implements CommandExecutor {
    public static ArrayList<Player> specs = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        try {
            switch (args[0]) {
                default:
                    throw new NullPointerException();
                case "toggle":
                    ChaosMod.setState(!ChaosMod.getState());
                    ChaosMod.updateChaos();
                    break;
                case "enable":
                    ChaosMod.setState(true);
                    ChaosMod.updateChaos();
                    break;
                case "disable":
                    ChaosMod.setState(false);
                    ChaosMod.updateChaos();
                    break;
                case "reload":
                    ChaosMod.loadProbs();
                    commandSender.sendMessage( ChatColor.GREEN + "Loaded probabilities.");
                    break;
                case "save":
                    ChaosMod.saveProbs();
                    commandSender.sendMessage( ChatColor.GREEN + "Saved.");
                    break;
                case "spectators":
                    switch(args[1]){
                        default:
                            throw new ArrayIndexOutOfBoundsException();
                        case "add":
                            try {
                                Player p = Bukkit.getPlayer(args[2]);
                                if (p == null){
                                    throw new ArrayIndexOutOfBoundsException();
                                }
                                specs.add(Bukkit.getPlayer(args[2]));
                                commandSender.sendMessage(ChatColor.GREEN + "Player added.");
                            }catch(ArrayIndexOutOfBoundsException e){
                                commandSender.sendMessage(ChatColor.RED + "No player found.");
                            }
                            break;
                        case "remove":
                            try {
                                int index = specs.indexOf(Bukkit.getPlayer(args[2]));
                                if (index == -1){
                                    throw new ArrayIndexOutOfBoundsException();
                                }
                                specs.remove(index);
                                commandSender.sendMessage(ChatColor.GREEN + "Player removed.");
                            }catch(ArrayIndexOutOfBoundsException e){
                                commandSender.sendMessage(ChatColor.RED + "No player found.");
                            }
                            break;
                        case "list":
                            commandSender.sendMessage("The players currently in the spectators are " + specs.toString().replace("[","").replace("]",""));
                            break;
                    }
                case "run":
                    Action act = ChaosMod.getAction(args[1]);
                    ArrayList<String> argsParsed = new ArrayList<String>(Arrays.asList(args));
                    //It's 2 because we need to exclude the run and id
                    for (int i = 0; i < 2; i++) {
                        argsParsed.remove(0);
                    }
                    HashMap<String,String> hashMap = ChaosMod.parseArgs(argsParsed);
                    if(hashMap.get("player") == null || Bukkit.getPlayer(hashMap.get("player")) == null){
                        if(commandSender instanceof Player) {
                            hashMap.put("player", commandSender.getName());
                        }else{
                            hashMap.put("player","@a");
                        }
                    }
                    if(act == null){
                        commandSender.sendMessage(ChatColor.RED + "Action not found");
                        return true;
                    }
                    act.invokeAction(hashMap);
                    break;
                case "cancelTasks":
                    Bukkit.getScheduler().cancelTasks(ChaosMod.getSelf());
                    ChaosMod.setState(false);
                    break;
                case "help":
                    Action action = null;
                    try {
                        action = ChaosMod.getAction(args[1]);
                    }catch(ArrayIndexOutOfBoundsException e){
                        commandSender.sendMessage("Welcome to ChaosMod!\n" +
                                "You have a few options.\n" +
                                "toggle: This toggles the state of the plugin. If its enabled it will be disabled and vice versa.\n" +
                                "help: With no arguments, it shows this help message. However, if you put the id of an action, it will show the help file for this action.\n" +
                                "run: Specify an id for an action to run it. See the help file for the action for parameters.\n" +
                                "reload: This reloads the probabilities from chaosConfig.json if it was modified.\n" +
                                "save: This creates a new chaosConfig.json using the probabilities in ram.\n" +
                                "cancelTasks: This cancels all tasks in the plugin, in case you made a task run for years on accident.\n" +
                                "spectators: Spectators don't have actions preformed on them. Specify spectators add <playername> to add and spectators remove <playername>. You can list the spectators using spectators list.\n" +
                                "disable and enable: Disable and enable the random actions respectively.\n" +
                                "This information is available in the readme.txt.");
                        break;
                    }
                    if(action == null){
                        commandSender.sendMessage(ChatColor.RED + "Action not found");
                        break;
                    }
                    if(action instanceof RepeatingAction) {
                        commandSender.sendMessage(ChatColor.BOLD + (action.getAbsoluteName() + " : " + args[1] + " : " + "Timed Action"));
                    }else{
                        commandSender.sendMessage(ChatColor.BOLD + (action.getAbsoluteName() + " : " + args[1] + " : " + "Regular Action"));
                    }
                    commandSender.sendMessage(action.getHelp());
                    break;
            }
        } catch(ArrayIndexOutOfBoundsException | NullPointerException e){
            commandSender.sendMessage(ChatColor.RED + "Invalid Syntax.");
        } catch (IOException e) {
            commandSender.sendMessage(ChatColor.RED + "An IO error occurred. Read the console log to see.");
            Bukkit.getLogger().warning(String.valueOf(e.getCause()));
        }
        return true;
    }
}

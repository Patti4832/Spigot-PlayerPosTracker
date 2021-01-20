package de.patti4832.playerpostracker;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandReceiver implements CommandExecutor {
    Main main;
    final Gson gson = new Gson();

    public CommandReceiver(Main m){
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.isOp()|| !(sender instanceof Player)){
            if(command.getName().equals("tracker")){
                if(args.length==1 && (args[0].equals("on") || args[0].equals("off") || args[0].equals("get") || args[0].equals("list") || args[0].equals("quiet"))){
                    switch (args[0]) {
                        case "on":
                            trackerOn(sender.getName());    //TODO players only (not console)
                            return true;
                        case "off":
                            trackerOff(sender.getName());
                            return true;
                        case "get":
                            String tmp = trackerGet(sender.getName());
                            if (tmp != null) {
                                sender.sendMessage(tmp);
                                return true;
                            }
                        case "list":
                            tmp = getTrackedPlayers();
                            if (tmp != null) {
                                sender.sendMessage(tmp);
                                return true;
                            }
                            else{
                                sender.sendMessage("Error");
                                return true;
                            }
                        case "quiet":
                            toggleQuiet();
                            return true;
                    }
                }
                else if(args.length==2 && (args[0].equals("on") || args[0].equals("off") || args[0].equals("get"))){
                    switch (args[0]) {
                        case "on":
                            trackerOn(args[1]);
                            return true;
                        case "off":
                            trackerOff(args[1]);
                            return true;
                        case "get":
                            String tmp = trackerGet(args[1]);
                            if (tmp != null) {
                                sender.sendMessage(tmp);
                                return true;
                            }
                    }
                }
            }
        }
        return false;
    }

    private void toggleQuiet(){
        try{
            main.saveDefaultConfig();
            boolean status = main.getConfig().getBoolean("quiet");
            status=!status;
            main.getConfig().set("quiet", status);
            main.saveConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void trackerOn(String player){
        try{
            main.saveDefaultConfig();
            List tmp = main.getConfig().getList("tracked-players");
            tmp.add(player);
            main.getConfig().set("tracked-players", tmp);
            main.saveConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getTrackedPlayers(){
        List<String> tmp = new ArrayList<>();
        try{
            main.saveDefaultConfig();
            List players = main.getConfig().getList("tracked-players");
            System.out.println("[PlayerPosTracker] "+players.toString());
            //convert to string list
            for (Object object : players) {
                tmp.add(object != null ? object.toString() : null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Tracked players: " + gson.toJson(tmp);
    }

    private void trackerOff(String player){
        try{
            main.saveDefaultConfig();
            List tmp = main.getConfig().getList("tracked-players");
            tmp.remove(player);
            main.getConfig().set("tracked-players", tmp);
            main.saveConfig();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String trackerGet(String player){
        return "Position of " + player + ": "+ gson.toJson(getPos(player));
    }

    private Tracker.Pos getPos(String player){
        assert player != null;

        Player p = main.getServer().getPlayer(player);
        if(p!=null){
            Location loc = p.getLocation();
            Tracker.Pos pos = new Tracker.Pos();
            try {
                pos.player = player;
                pos.world = loc.getWorld().getName();
                pos.x = (int) (loc.getX() + .5);
                pos.y = (int) (loc.getY() + .0);
                pos.z = (int) (loc.getZ() + .5);
            }catch (Exception e){
                System.out.println("[PlayerPosTracker] Error while tracking player " + player);
                return null;
            }

            return pos;
        }else
            return null;    //Player offline
    }
}

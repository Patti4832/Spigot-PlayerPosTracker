package de.patti4832.playerpostracker;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class Tracker {
    private static final Type REVIEW_TYPE = new TypeToken<List<Positions>>() {}.getType();

    Main main;
    Timer timer;
    final String logfile = "poslog.json";

    private final long delay = 10L;   //Timer delay in seconds

    final Gson gson = new Gson();
    List<Positions> positionsList = new ArrayList<>();

    public Tracker(Main m){
        main = m;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                looper();
            }
        };

        scheduler.scheduleSyncRepeatingTask(main,task,0L,delay*20);
    }

    private void looper(){
        List<String> list = getPlayersFromConfig();
        List<Pos> posList = new ArrayList<>();
        Pos tmp;

        //Get position of all players
        for(String s : list){
            tmp = getPos(s);
            if(tmp != null){
                posList.add(tmp);
            }
        }

        if(posList.size()>0) {
            Positions pos = new Positions();
            pos.posList = posList;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            pos.time = timestamp.toString();

            positionsList.add(pos);

            try {
                saveLog();
            } catch (Exception e){
                System.out.println("[PlayerPosTracker] Error writing to logfile!");
            }
        }
    }

    public void stop(){
        timer.cancel();
        saveLog();
    }

    private void createFile(){
        try {
            File f = new File(main.getDataFolder().getAbsolutePath()+"/"+logfile);
            FileWriter writer = new FileWriter(f);
            String json = gson.toJson(new ArrayList<>());
            writer.write(json);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<String> getPlayersFromConfig(){
        List<String> tmp = new ArrayList<>();
        try{
            main.saveDefaultConfig();
            List players = main.getConfig().getList("tracked-players");
            //convert to string list
            for (Object object : players) {
                tmp.add(object != null ? object.toString() : null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tmp;
    }

    private void saveLog(){
        String json = gson.toJson(positionsList);

        //To console
        boolean status = main.getConfig().getBoolean("quiet");
        if(!status)
            System.out.println(json);

        //To file
        String content = null;
        try {
            File f = new File(main.getDataFolder().getAbsolutePath()+"/"+logfile);
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            content = br.lines().collect(Collectors.joining());
            br.close();
            reader.close();
        }catch (Exception e){
            createFile();
            e.printStackTrace();
        }

        List<Positions> tmp = new ArrayList<>();

        //Check if file had content
        if(content!=null){
            tmp = gson.fromJson(content, REVIEW_TYPE);
        }

        //Check if file is full
        if(tmp.size() + positionsList.size() >= 10000){
            for(int i = 0; i < positionsList.size(); i++)
                tmp.remove(0);
        }

        //Append to list
        tmp.addAll(positionsList);

        if(tmp.size()>0){
            json = gson.toJson(tmp);

            //Write to file
            try {
                File f = new File(main.getDataFolder().getAbsolutePath()+"/"+logfile);
                FileWriter writer = new FileWriter(f);
                writer.write(json);
                writer.close();
            }catch (Exception e){
                e.printStackTrace();    //TODO prevent data loss
            }
        }

        //Clear tmp list
        positionsList.clear();
    }

    private Pos getPos(String player){
        assert player != null;

        Player p = main.getServer().getPlayer(player);
        if(p!=null){
            Location loc = p.getLocation();
            Pos pos = new Pos();
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

    public static class Positions{
        public String time;
        List<Pos> posList = new ArrayList<>();
    }

    public static class Pos{
        public String player;
        public String world;
        public int x, y, z;
    }
}

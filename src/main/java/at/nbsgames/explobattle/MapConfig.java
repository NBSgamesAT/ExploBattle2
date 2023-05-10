package at.nbsgames.explobattle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapConfig{

    public static final String ARENA_PREFIX = "arenas";
    public static final String ARENA_PREFIX_WITH_POINT = ARENA_PREFIX + ".";

    private File file;
    private String fileName;
    private FileConfiguration conf;

    /**
     *
     * @param fileName The name of the file without the .yml extension
     * @param plugin
     */
    public MapConfig(String fileName, JavaPlugin plugin) throws IOException {
        this.fileName = plugin.getDataFolder().getAbsolutePath() + File.separator + fileName + ".yml";
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        this.file = new File(this.fileName);

        if(!this.file.exists()) this.file.createNewFile();

        this.conf = YamlConfiguration.loadConfiguration(this.file);
        LinkedList<String> list = new LinkedList<>();
        list.add("This file saves all the arena data and their configurations.");
        this.conf.options().setHeader(list);
        conf.save(file);
    }

    public static String locationToBlockLocationString(Location loc){
        return loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld().getName();
    }
    public static String locationToEntityLocationString(Location loc){
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getPitch() + ";" + loc.getYaw() + ";" + loc.getWorld().getName();
    }

    private void printStackTrace(IOException e){
        System.out.println("Something went wrong handling the file: " + this.fileName);
        e.printStackTrace();
    }
    public String getString(String key){
        return conf.getString(key);
    }
    public int getInt(String key){
        return conf.getInt(key);
    }
    public long getLong(String key){
        return conf.getLong(key);
    }
    public boolean getBoolean(String key){
        return conf.getBoolean(key);
    }
    public double getDouble(String key){
        return conf.getDouble(key);
    }
    public boolean set(String key, Object value){
        conf.set(key, value);
        return this.save();
    }

    public boolean exists(String key){
        return conf.contains(key);
    }
    public List<String> getListOfKeys(String key){
        if(!conf.contains(key)) return null;
        ConfigurationSection section = conf.getConfigurationSection(key);
        return section.getKeys(false).stream().toList();
    }

    public boolean multichange(MultiChange change){
        change.run(this.conf);
        return this.save();
    }

    public boolean save(){
        try {
            conf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getStringList(String key){
        return this.conf.getStringList(key);
    }

    public Location getBlockLocation(String key){
        String stringLocation = this.conf.getString(key);
        if(stringLocation == null) return null;
        String[] parts = stringLocation.split(";", 4);
        Location loc = new Location(Bukkit.getWorld(parts[3]), Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        return loc;
    }

    public Location getEntityLocation(String key){
        String stringLocation = this.conf.getString(key);
        if(stringLocation == null) return null;
        String[] parts = stringLocation.split(";", 6);
        Location loc = new Location(Bukkit.getWorld(parts[5]), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
        loc.setPitch(Float.parseFloat(parts[3]));
        loc.setYaw(Float.parseFloat(parts[4]));
        return loc;
    }
    public static Location getEntityLocationByLocationString(String strg){
        if(strg == null) return null;
        String[] parts = strg.split(";", 6);
        Location loc = new Location(Bukkit.getWorld(parts[5]), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
        loc.setPitch(Float.parseFloat(parts[3]));
        loc.setYaw(Float.parseFloat(parts[4]));
        return loc;
    }


    public interface MultiChange{
        void run(FileConfiguration conf);
    }



}

package at.nbsgames.explobattle;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class MapConfig{

    private File file;
    private String fileName;
    private FileConfiguration conf;

    /**
     *
     * @param fileName The name of the file without the .yml extension
     * @param plugin
     */
    public MapConfig(String fileName, JavaPlugin plugin) throws IOException {
        this.fileName = plugin.getDataFolder().getAbsolutePath() + File.pathSeparator + fileName + ".yml";
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        this.file = new File(fileName);

        if(!this.file.exists()) this.file.createNewFile();
        this.conf = YamlConfiguration.loadConfiguration(this.file);
        LinkedList<String> list = new LinkedList<>();
        list.add("This file saves all the arena data and their configurations.");
        this.conf.options().setHeader(list);
        conf.save(file);
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
    public void set(String key, Object value){
        conf.set(key, value);
    }
    public void save(){
        try {
            conf.save(file);
        } catch (IOException e) {
            this.printStackTrace(e);
        }
    }



}

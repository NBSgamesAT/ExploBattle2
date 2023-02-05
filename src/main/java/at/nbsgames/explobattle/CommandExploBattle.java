package at.nbsgames.explobattle;

import at.nbsgames.explobattle.command_system.*;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.enums.EnumPermissions;
import at.nbsgames.explobattle.events.RightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

class CommandExploBattle {

    public static final String ARENA_PREFIX = "arenas";
    public static final String ARENA_PREFIX_WITH_POINT = ARENA_PREFIX + ".";

    private static final NbsArgumentWorker MAP_ARGUMENT_WORKER = new NbsArgumentWorker() {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            if(JavaPlugin.getPlugin(Main.class).getMapConfig().exists(ARENA_PREFIX + "." + input)) return input;
            throw new FailedToObjectifyException("Could not find map called " + input);
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return JavaPlugin.getPlugin(Main.class).getMapConfig().getListOfKeys(ARENA_PREFIX);
        }
    };

    static NbsGroupCommand getCommand(Main main){

        NbsGroupCommand gc = new NbsGroupCommand("explobattle", "Explobattle Main Command");

        NbsGroupCommand arena = new NbsGroupCommand("arena", "Command for arena management");


        arena.addSubCommand(new NbsActionCommand("create", "Create an explobattle arena", EnumPermissions.ARENA_INFO.toString(), (sender, command, label, args) -> {
            String name = (String) args[0];
            main.getMapConfig().set(ARENA_PREFIX_WITH_POINT + name + ".enabled", false);
            sender.sendMessage(ChatColor.GREEN + "Arena " + name + " has been created. Please add spawn points and a sign location for the map.");
            Arena.loadArena(main, name);
            return true;
        }).setArguments(new NbsArguments("name", "The name for the arena", EnumMainArgs.STRING)));

        arena.addSubCommand(new NbsActionCommand("delete", "Deletes the explobattle arena", EnumPermissions.ARENA_DELETE.toString(), (sender, command, label, args) -> {
            String name = (String) args[0];
            main.getMapConfig().set(ARENA_PREFIX_WITH_POINT + name + ".enabled", false);
            sender.sendMessage(ChatColor.GREEN + "Arena " + name + " has been deleted.");

            return true;
        }).setArguments(new NbsArguments("name", "The name for the arena", MAP_ARGUMENT_WORKER)));

        arena.addSubCommand(new NbsActionCommand("list", "Lists all the explobattle arenas", EnumPermissions.ARENA_INFO.toString(), (sender, command, label, args) -> {
            List<String> maps = main.getMapConfig().getListOfKeys(ARENA_PREFIX_WITH_POINT);
            String mapString = String.join(", ", maps);
            sender.sendMessage(Component.text("All available arenas: \n").color(NamedTextColor.GREEN));
            sender.sendMessage(Component.text(mapString).color(TextColor.color(0x00FF00)));
            return true;
        }));

        arena.addSubCommand(new NbsActionCommand("enable", "Enable an Arena", EnumPermissions.ARENA_INFO.toString(), (sender, command, label, args) -> {
            String map = (String) args[0];
            boolean enabled = (boolean) args[1];

            main.getMapConfig().set(ARENA_PREFIX_WITH_POINT + map + ".enabled", enabled);
            Arena.loadArena(main, map);
            sender.sendMessage(Component.text("Arena " + map + " should now be " + (enabled ? "enabled." : "disabled.")).color(NamedTextColor.GREEN));

            return true;
        }).setArguments(new NbsArguments("name", "The name for the arena", MAP_ARGUMENT_WORKER),
                new NbsArguments("state", "The new state, either true or false", EnumMainArgs.BOOLEAN)));

        arena.addSubCommand(new NbsActionCommand("setsign", "Set the sign for a given arena", EnumPermissions.ARENA_SET_SIGN.toString(), (sender, command, label, args) -> {
            String mapName = (String) args[0];
            Player p = (Player) sender;
            Block block = p.getTargetBlock(5);
            if(block == null || !(block.getState() instanceof Sign)){
                main.getLogger().info(block.getState().toString());
                p.sendMessage(Component.text("You have to look at a sign (Maximal distance 5 block)").color(NamedTextColor.RED));
                return true;
            }

            main.getMapConfig().set(ARENA_PREFIX_WITH_POINT + mapName + ".sign", MapConfig.locationToBlockLocationString(block.getLocation()));
            sender.sendMessage(Component.text("The sign has been set").color(NamedTextColor.GREEN));
            Arena.loadArena(main, mapName);

            return true;
        }).setPlayerOnly(true).setArguments(new NbsArguments("arena", "The name of the arena", MAP_ARGUMENT_WORKER)));

        NbsGroupCommand spawns = new NbsGroupCommand("spawn", "Manage Spawnpoints for a given spawn");

        spawns.addSubCommand(new NbsActionCommand("add", "Adds a spawn point", EnumPermissions.ARENA_MANAGE_SPAWNS.toString(), (sender, command, label, args) -> {
            String arenaName = (String) args[0];
            Player p = (Player) sender;
            main.getMapConfig().multichange(conf -> {
                if(conf.contains(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns")){
                    List<String> list = conf.getStringList(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns");
                    p.sendMessage(Component.text("A spawn with id " + list.size() + " has been added to the arena " + arenaName).color(NamedTextColor.GREEN));
                    list.add(MapConfig.locationToEntityLocationString(p.getLocation()));
                    conf.set(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns", list);
                }
                else{
                    conf.set(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns", Arrays.stream((new String[]{MapConfig.locationToEntityLocationString(p.getLocation())})).toList());
                    p.sendMessage(Component.text("A spawn with id 0 has been added to the arena " + arenaName).color(NamedTextColor.GREEN));
                }
            });

            Arena.loadArena(main, arenaName);
            return true;
        }).setPlayerOnly(true).setArguments(new NbsArguments("arena", "The arena that the spawn point is in", MAP_ARGUMENT_WORKER)));

        spawns.addSubCommand(new NbsActionCommand("remove", "Removes a spawn point.", EnumPermissions.ARENA_MANAGE_SPAWNS.toString(), (sender, command, label, args) -> {
            String arenaName = (String) args[0];
            int spawnNumber = (int) args[1];
            if(spawnNumber > 0){
                sender.sendMessage(Component.text("A spawn point number cannot be smaller than 0").color(NamedTextColor.RED));
                return true;
            }
            Player p = (Player) sender;
            main.getMapConfig().multichange(conf -> {
                List<String> list = conf.getStringList(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns");
                p.sendMessage(Component.text("A spawn with id " + spawnNumber + " has been removed from the arena " + arenaName).color(NamedTextColor.GREEN));
                if(spawnNumber >= list.size()){
                    sender.sendMessage(Component.text("The chosen spawn point " + spawnNumber + " does not exist;").color(NamedTextColor.RED));
                    return;
                }
                list.remove(spawnNumber);
                conf.set(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns", list);
                Arena.loadArena(main, arenaName);
            });
            return true;
        }).setPlayerOnly(true).setArguments(new NbsArguments("arena", "The arena that the spawn point is in", MAP_ARGUMENT_WORKER),
                new NbsArguments("number", "The number of the spawn point", EnumMainArgs.INT)));

        spawns.addSubCommand(new NbsActionCommand("list", "Lists all spawn points of an arena", EnumPermissions.ARENA_MANAGE_SPAWNS.toString(), (sender, command, label, args) -> {
            String arenaName = (String) args[0];
            List<String> spawnLocs = main.getMapConfig().getStringList(ARENA_PREFIX_WITH_POINT + arenaName + ".spawns");
            sender.sendMessage(Component.text("Arena " + arenaName + " has " + spawnLocs.size() + " spawns\n").color(NamedTextColor.GREEN));
            for(int pos = 0; pos < spawnLocs.size(); pos++){
                sender.sendMessage(Component.text(pos + ": " + spawnLocs.get(pos)).color(NamedTextColor.GRAY));
            }
            if(spawnLocs.size() == 0){
                sender.sendMessage(Component.text("You have yet to add any spawn points").color(NamedTextColor.RED));
            }
            return true;
        }).setArguments(new NbsArguments("arena", "The arena of the spawn points", MAP_ARGUMENT_WORKER)));

        arena.addSubCommand(spawns);


        gc.addSubCommand(new NbsActionCommand("leave", "Leaves the currently joined ExploBattle", ((sender, command, label, args) -> {
            boolean info = Arena.removePlayerFromSystem((Player) sender);
            if(info){
                String strg = main.getConfig().getString(EnumConfigStrings.TEXT_PLAYER_LEAVE.toString()).replace("(PLAYER)", sender.getName());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', strg));
            }
            else{
                String strg = main.getConfig().getString(EnumConfigStrings.PLAYER_JOIN_FAILURE.toString()).replace("(PLAYER)", sender.getName());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', strg));
            }
            return true;
        })).setPlayerOnly(true));

        gc.addSubCommand(new NbsActionCommand("items", "Let's you optain the four explobattle items", EnumPermissions.USE_WEAPONS_OUTSIDE_ARENA.toString(), ((sender, command, label, args) -> {
            Player p = (Player) sender;

            ItemStack gun = makeItemWithName(Material.NETHERITE_HOE, RightClickEvent.GUN);

            ItemStack grenade = makeItemWithName(Material.TNT, RightClickEvent.GRENADE);

            ItemStack uGrenade = makeItemWithName(Material.SNOW, RightClickEvent.ULTRA_GRENADE);

            ItemStack bazooka = makeItemWithName(Material.NETHERITE_AXE, RightClickEvent.BAZOOKA);

            p.getInventory().addItem(gun, grenade, uGrenade, bazooka);
            sender.sendMessage(Component.text("All special weapons have been given to you").color(NamedTextColor.GREEN));
            return true;
        })).setPlayerOnly(true));


        gc.addSubCommand(arena);

        return gc;
    }
    public static ItemStack makeItemWithName(Material mat, Component name){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        item.setItemMeta(meta);
        return item;
    }
}

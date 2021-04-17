package lemon.is.awesome.catacombs;

import lemon.is.awesome.catacombs.dungeon.Dungeon;
import lemon.is.awesome.catacombs.groups.Group;
import lemon.is.awesome.catacombs.groups.GroupCommands;
import lemon.is.awesome.catacombs.groups.GroupInvite;
import lemon.is.awesome.catacombs.shop.ShopCommand;
import lemon.is.awesome.catacombs.shop.ShopEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static lemon.is.awesome.catacombs.Utilities.getPath;
import static lemon.is.awesome.catacombs.Utilities.suid;

/*
    Hello! My name is Lemon! If you are reading this, I have left the staff team and you are either looking through my
code for this project or you need to work on it to add features to the gamemode, assuming it sticks around. It better.
I will be guiding you through my disgusting monstrosity of code so you know what's happening!
*/

public final class Catacombs extends JavaPlugin {

    // This is a dictionary/map containing all the dungeons. The key is the ID, which is assigned to the dungeon upon
    // creation. This is to seperate the dungeon from others, so it can be destantiated later.
    private static final Hashtable<Integer, Dungeon> dungeons = new Hashtable<>();
    // This is the id counter. When a new dungeon is made, it is given this id and the counter is incremented.
    public static int id = 1;
    public static Set<Material> breaklist = new HashSet<>();
    public static World mainWorld;
    public static Location nextGame;
    public static Set<Group> groups = new HashSet<>();
    public static Set<GroupInvite> invites = new HashSet<>();
    public static ArrayList<Comb> combs = new ArrayList<>();

    public static boolean shopEnabled = true;

    public static int healthprice;
    public static int speedprice;
    public static int damageprice;

    @Override
    public void onEnable() {
        // This initializes the commands. If you are new to Spigot, this is bad. Don't do it like this.
        Commands c = new Commands(); GroupCommands gc = new GroupCommands();
        refresh();

        Objects.requireNonNull(getCommand("generate")).setExecutor(c);
        Objects.requireNonNull(getCommand("destroydungeon")).setExecutor(c);
        Objects.requireNonNull(getCommand("lemonsFinalMessage")).setExecutor(c);
        Objects.requireNonNull(getCommand("catacoins")).setExecutor(c);

        Objects.requireNonNull(getCommand("invitegroup")).setExecutor(gc);
        Objects.requireNonNull(getCommand("leavegroup")).setExecutor(gc);
        Objects.requireNonNull(getCommand("joingroup")).setExecutor(gc);

        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand());

        getServer().getPluginManager().registerEvents(new SignHandler(),this);
        getServer().getPluginManager().registerEvents(new BlockEvents(),this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(),this);
        getServer().getPluginManager().registerEvents(new ShopEvents(),this);

        Bukkit.getLogger().info("Catacombs Loaded! --Lemon is Cool B)");

        // This creates a task that runs the step function of each dungeon every seconds.
        // Wait 20 -> ticks, 20 ticks per second, once per second.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Integer dungeonID : dungeons.keySet()) {
                dungeons.get(dungeonID).step();
            }
            for (GroupInvite invite : invites) {
                invite.step();
            }
        }, 20, 20);
    }

    public static void transferBalance(Player p) {
        Comb comb = getComb(p);
        int bal = comb.getBalance() + p.getTotalExperience();
        comb.setBalance(bal);
    }

    public static Comb getComb(Player p) {
        for (Comb comb : combs) {
            if (comb.uuid.equals(suid(p))) {return comb;}
        } return new Comb(p);
    }

    public static void addDungeon(Dungeon dungeon) {
        // Increments ID and adds a dungeon with the given id to the Hashtable.
        dungeons.put(id, dungeon); id++;
    }

    public static void refresh() {
        updateWorld(); updateBlocklist(); updatePrices(); updateShopEnabled();
    }

    public static void startDungeon(String name, Group players, int size, Location loc) {
        loc = new Location(
                loc.getWorld(),
                loc.getX() + id * 100,
                50,
                loc.getZ()
        );
        Dungeon x = new Dungeon(name, players, loc, size);
        addDungeon(x); x.generate();
        for (Player p : players.players) {
            p.getInventory().clear(); p.updateInventory();
        }
    }

    public static Dungeon inGame(Player p) {
        Group g = getGroup(p);
        for (Dungeon dungeon : dungeons.values()) {
            if (dungeon.getPlayers() == g) {
                return dungeon;
            }
        }
        return null;
    }

    public static void updateBlocklist() {
        List<?> b = YamlConfiguration.loadConfiguration(new File(getPath(), "config.yml")).getList("breakable");
        for (int m = 0; m < (b != null ? b.size() : 0); m++) {
            breaklist.add(Material.getMaterial((String) b.get(m)));
        }
    }

    public static void updateShopEnabled() {
        shopEnabled = YamlConfiguration.loadConfiguration(new File(getPath(), "config.yml")).getBoolean("shop");
    }

    public static void updatePrices() {
        FileConfiguration p = YamlConfiguration.loadConfiguration(new File(getPath(), "config.yml"));
        healthprice = p.getInt("health_price");
        speedprice = p.getInt("speed_price");
        damageprice = p.getInt("damage_price");
    }

    public static void updateWorld() {
        mainWorld = Bukkit.getWorld(
                Objects.requireNonNull(YamlConfiguration.loadConfiguration(new File(getPath(), "config.yml")).getString("world"))
        );
        nextGame = new Location(mainWorld, nextGame != null ? nextGame.getX() : 1000, 50, 1000);
    }

    public static Collection<Dungeon> getDungeons() {
        return dungeons.values();
    }

    public static void removeDungeon(int id) {
        // Gets the dungeon at the id and runs its destroy function, then it removes it from the Hashtable.
        if (dungeons.containsKey(id)) dungeons.get(id).destroy();
        dungeons.remove(id);
        Catacombs.id--;
    }

    public static Group getGroup(Player p) {
        for (Group g : groups) {
            if (g.players.contains(p)) { return g; }
        }
        return new Group(new HashSet<>(Collections.singletonList(p)));
    }

    @Override
    public void onDisable() {
        for (Dungeon dungeon : getDungeons()) {
            dungeon.end();
        }
    }
}

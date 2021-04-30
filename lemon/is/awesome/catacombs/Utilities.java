package lemon.is.awesome.catacombs;

import lemon.is.awesome.catacombs.items.CItem;
import lemon.is.awesome.catacombs.loottable.LootTable;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.DefinedStructure;
import net.minecraft.server.v1_16_R3.DefinedStructureInfo;
import net.minecraft.server.v1_16_R3.EnumBlockMirror;
import net.minecraft.server.v1_16_R3.EnumBlockRotation;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static lemon.is.awesome.catacombs.whaaa.EnglishNumberToWords.convert;

public class Utilities {

    // I hate all of this.
    public static ArrayList<String> elitePowers = new ArrayList<>(Arrays.asList(
            "attack_arrow.yml",
            "attack_blinding.yml",
            "attack_confusing.yml",
            "attack_fire.yml",
            "attack_fireball.yml",
            "attack_freeze.yml",
            "attack_gravity.yml",
            "attack_lightning.yml",
            "attack_poison.yml",
            "attack_push.yml",
            "attack_vacuum.yml",
            "attack_weakness.yml",
            "attack_web.yml",
            "attack_wither.yml",
            "bonus_loot.yml",
            "bullet_hell.yml",
            "corpse.yml",
            "flame_pyre.yml",
            "flamethrower.yml",
            "gold_explosion.yml",
            "gold_shotgun.yml",
            "hyper_loot.yml",
            "implosion.yml",
            "invisibility.yml",
            "invulnerability_arrow.yml",
            "invulnerability_fall_damage.yml",
            "invulnerability_fire.yml",
            "invulnerability_knockback.yml",
            "meteor_shower.yml",
            "moonwalk.yml",
            "movement_speed.yml",
            "skeleton_pillar.yml",
            "skeleton_tracking_arrow.yml",
            "spirit_walk.yml",
            "summon_embers.yml",
            "summon_raug.yml",
            "summon_the_returned.yml",
            "taunt.yml",
            "zombie_bloat.yml",
            "zombie_friends.yml",
            "zombie_necronomicon.yml",
            "zombie_parents.yml"
    ));

    // I stole the structure stuff from Mr. Mastadons
    public static DefinedStructure loadSingleStructure(File source) throws IOException {
        DefinedStructure structure = new DefinedStructure();
        structure.b(NBTCompressedStreamTools.a(new FileInputStream(source)));
        return structure;
    }

    public static void insertSingleStructure(DefinedStructure structure, Location startEdge, EnumBlockRotation rotation) {
        WorldServer world = ((CraftWorld) startEdge.getWorld()).getHandle();
        DefinedStructureInfo structureInfo = new DefinedStructureInfo().a(EnumBlockMirror.NONE).a(rotation).a(false).a((ChunkCoordIntPair) null).a(new Random());

        BlockPosition position = new BlockPosition(startEdge.getBlockX(), startEdge.getBlockY(), startEdge.getBlockZ());

        structure.a(world, position, structureInfo, new Random());
    }

    // Returns the senders location
    public static Location getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        }
        if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    // Gets the path of the plugin folder
    public static String getPath() { return Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("Catacombs"))
            .getDataFolder().getAbsolutePath(); }

    // Appends one path to another
    public static String addToPath(String path, String path2) { return path + "/" + path2; }

    // Spawns a random structure
    public static void randomStructure(String folder, Location loc) {
        String path = getPath();
        String structsPath = addToPath(path, folder); // Path of structures
        String[] filenames = new File(structsPath).list(); // List of structures in the path
        if (filenames == null) {return;} // If no structures, curl up and die
        File[] files = new File[filenames.length]; // List of files the length of all the files long
        for (int i = 0; i < filenames.length; i++) {
            files[i] = new File(structsPath, filenames[i]);
        } // Iterates over all the files in the location adding them to the list
        Random rand = new Random(); // Random, duh.
        try {
            insertSingleStructure(loadSingleStructure(files[rand.nextInt(filenames.length)]), loc, EnumBlockRotation.NONE);
            // Inserts a structure at the location, a random one in the list with a block rotation of none
        } catch (IOException e) {
            e.printStackTrace();
            // Error handling lol
        }
    }

    public static ItemStack customMaterial(String material, int count, HashMap<String, CItem> custom) {
        if (custom.containsKey(material)) {
            return custom.get(material).get(count);
        } return new ItemStack(Objects.requireNonNull(Material.getMaterial(material)), count);
    }

    public static FileConfiguration loadConfig(String folder, String name) {
        return YamlConfiguration.loadConfiguration(new File(addToPath(getPath(), folder), name + ".yml"));
    } // Loads a config file in the plugin folder with a folder folder and name name

    // Gets the config of a dungeon of the given name (folder)
    public static FileConfiguration dungeonConfig(String folder) {
        return loadConfig(folder, "config");
    }

    // Turns tildes into numbers
    public static double relativeTilde(String tilde) {
        if (tilde.length() > 1) {
            return Double.parseDouble(tilde.substring(1)); // Returns the number after it
        }
        return 0; // If it was just a tilde, simply return 0
    }

    // Complex location parsing :eyes:
    public static Location commandLocation(CommandSender sender, int cStart, String[] args) {
        // cStart is the index of the argument the coordinates start at
        if (args[cStart].startsWith("~") && args[cStart + 1].startsWith("~") && args[cStart + 2].startsWith("~")) {
            Location sLocation = getLocation(sender);
            sLocation.setX(sLocation.getX() + relativeTilde(args[cStart]));
            sLocation.setY(sLocation.getY() + relativeTilde(args[cStart + 1]));
            sLocation.setZ(sLocation.getZ() + relativeTilde(args[cStart + 2]));
            // Checks if tildes are provided in the span of args, then returns the location with the relative values added
            return sLocation;
        }
        // Otherwise return a new location using the values provided
        return new Location(
                ((Entity) sender).getWorld(),
                Double.parseDouble(args[cStart]),
                Double.parseDouble(args[cStart+1]),
                Double.parseDouble(args[cStart+2])
        );
    }

    public static boolean cornersFree(Location leftCorner, int size) {
        // Checks if the corners of the 2d square formed when offsetting by size are free.
        World world = leftCorner.getWorld();
        return world.getBlockAt(leftCorner).isEmpty() &&
                world.getBlockAt(leftCorner.clone().add(0, 0, size - 1)).isEmpty() &&
                world.getBlockAt(leftCorner.clone().add(size - 1, 0, size - 1)).isEmpty() &&
                world.getBlockAt(leftCorner.clone().add(size - 1, 0, 0)).isEmpty();
    }

    public static boolean inBounds(Location loc, double xlimit, double zlimit) {
        return loc.getX() <= xlimit && loc.getZ() <= zlimit;
    } // It's self explanatory. If not, you don't belong here. Close IntelliJ, that better be what you're using.

    public static boolean cornersInBounds(Location loc, int size, double xlimit, double zlimit) {
        return inBounds(loc, xlimit, zlimit) &&
                inBounds(loc.clone().add(size - 1, 0, 0), xlimit, zlimit) &&
                inBounds(loc.clone().add(size - 1, 0, size - 1), xlimit, zlimit) &&
                inBounds(loc.clone().add(0, 0, size - 1), xlimit, zlimit);
    }
    // cornersFree x inBounds <3

    public static boolean aOk(Location leftCorner, int size, double xlimit, double zlimit) {
        return cornersFree(leftCorner, size) && cornersInBounds(leftCorner, size, xlimit, zlimit);
    }
    // Makes sure the corners are free AND in bounds so everything is a ok :-)

    public static boolean chance(int prob) {
        Random random = new Random();
        return random.nextInt(prob) == 1;
        // Do I really need to explain this??
    }

    public static ArrayList<Long> waitWhat(long number) {
        return convert(number).length() == number ? new ArrayList<Long>(){{add(number);}} :
                new ArrayList<Long>(){{add(number); addAll(waitWhat(convert(number).length()));}};
    } // Haha

    public static int randint(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
        // Generates number between min and max
    }

    public static String suid(Player p) {
        return String.valueOf(p.getUniqueId());
    }

    public static LootTable loadTable(String folder, String table, HashMap<String, CItem> items) {
        LootTable.LootTableBuilder tableBuilder = new LootTable.LootTableBuilder(); // New builder
        ConfigurationSection tableYaml = loadConfig(addToPath(folder, "tables"), table).getConfigurationSection("items");
        // YML of table
        assert tableYaml != null; // Still don't know what assert does :/
        for (String key : tableYaml.getKeys(false)) {
            int weight = Objects.requireNonNull(tableYaml.getConfigurationSection(key)).getInt("weight");
            int count = Objects.requireNonNull(tableYaml.getConfigurationSection(key)).getInt("count");
            tableBuilder.add(customMaterial(key, count, items), weight);
            // Adds an element to the loot table for every defined element in the yml
        }
        return tableBuilder.build(); // Builds table and returns it
    }

    public static boolean playerRadius(Location loc, double radius) {
        List<Player> players = loc.getWorld().getPlayers();
        for (Player player : players) {
            if (player.getLocation().distance(loc) <= radius) {return true;}
        } // For every player, if distance between it and target location < radius, return true
        return false;
    }

    public static ArrayList<ItemStack> dropTable(String folder, String table, HashMap<String, CItem> items) {
        FileConfiguration yml = loadConfig(addToPath(folder, "tables"), table);
        ArrayList<ItemStack> out = new ArrayList<>(); // List of items to return
        LootTable lootTable = loadTable(folder, table, items); // Gets the loot table at destination
        int count = randint(yml.getInt("min"), yml.getInt("max")); // Random number between min and max loot table item count
        for (int i = 0; i < count; i++) {
            out.add(lootTable.getRandom()); // Adds random item to out list for each item in drop count
        }
        return out; // Return :D
    }

    public static void scatterChest(ArrayList<ItemStack> items, Inventory chest) {
        Random random = new Random();
        boolean[] chosen = new boolean[chest.getSize()]; // Occupied slots
        for (ItemStack item : items) {
            int slot; do {
                slot = random.nextInt(chest.getSize());
            } while (chosen[slot]); chosen[slot] = true; // Loops until it generates a random number not chosen
            chest.setItem(slot, item); // Sets current item in chest to slot
        }
    }

    public static void lootHere(String folder, String table, Location loc, BlockFace blockFace, HashMap<String, CItem> items) {
        loc.getBlock().setType(Material.CHEST); // Places chest
        BlockData blockData = loc.getBlock().getBlockData(); // Gets data
        ((Directional) blockData).setFacing(blockFace); // Sets facing direction in blockdata
        loc.getBlock().setBlockData(blockData); // Sets blockdata
        Inventory chest = ((Chest) loc.getBlock().getState()).getBlockInventory(); // Gets chest inventory
        scatterChest(dropTable(folder, table, items), chest); // Scatters a dropped table in the chest
    }
}

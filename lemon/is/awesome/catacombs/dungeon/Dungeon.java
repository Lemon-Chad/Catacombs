package lemon.is.awesome.catacombs.dungeon;
import lemon.is.awesome.catacombs.Catacombs;
import lemon.is.awesome.catacombs.dungeonmob.MobSpawner;
import lemon.is.awesome.catacombs.dungeonmob.Mob;

import lemon.is.awesome.catacombs.groups.Group;
import lemon.is.awesome.catacombs.items.CItem;
import net.minecraft.server.v1_16_R3.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static lemon.is.awesome.catacombs.Utilities.*;

public class Dungeon {
    private final String folder; // The folder where the dungeon is located, inside the plugin folder
    private ArrayList<MobSpawner> spawners = new ArrayList<>(); // A list of the children spawners of the dungeon
    private final int id; // The dungeons id
    private final int tiling;
    private final Group players;
    private Location start;
    public ArrayList<Sign> exits;
    private final Location origin;
    private final int size;
    public final HashMap<String, CItem> items = new HashMap<>();
    private final ArrayList<Floor> floors = new ArrayList<>();
    private int floor = 0;
    private final Map<Integer, Floor> specialFloors = new HashMap<>();

    public Dungeon(String folder, Group players, Location origin, int size) {
        this.size = size; this.origin = origin;
        folder = addToPath("dungeons", folder);
        this.folder = folder;
        this.players = players;
        this.tiling = dungeonConfig(folder).getInt("tile_size");
        // Sets the variable tiling to the tile size of the dungeon
        String[] filenames = new File(addToPath(addToPath(getPath(), this.folder), "items")).list();
        if (filenames != null) {
            for (String filename : filenames) {
                String f = filename.replace(".yml", "");
                this.items.put(f, new CItem(this.folder, f));
            }
        }
        List<?> floorlist = dungeonConfig(folder).getList("floors");
        assert floorlist != null;
        for (Object o : floorlist) {
            floors.add(new Floor((String) o));
        }
        ConfigurationSection floorSection = dungeonConfig(folder).getConfigurationSection("special_floors");
        if (floorSection != null) {
            for (String key : floorSection.getKeys(false)) {
                specialFloors.put(Integer.parseInt(key), new Floor((String) floorSection.get(key)));
            }
        }
        Catacombs.addDungeon(this);
        // Adds the dungeon to the plugin map, so it runs its step function every second
        id = Catacombs.id;
        // Sets the id to the current dungeon id
    }
    public Group getPlayers() {
        return players;
    }
    public int getId() {
        return id;
    } // Gets the id of the dungeon, simple stuff

    public void parseSign(Location loc) {
        // This checks if the current block is a sign
        if (loc.getBlock().getType() == Material.OAK_SIGN || loc.getBlock().getType() == Material.OAK_WALL_SIGN) {
            Sign sign = (Sign) loc.getBlock().getState();
            // Gets the sign object and checks the first line for keywords
            switch (sign.getLine(0)) {
                case "chest":
                    lootHere(this.folder, sign.getLine(1), loc,
                            ((org.bukkit.material.Sign) loc.getBlock().getState().getData()).getFacing(),
                            this.items); break;
                    // Places a chest at the current location, using the dungeon folder as the folder, the name of the table
                    // as the second line of the sign, the sign location as the location, and the orientation of the sign as
                    // the direction of the chest.
                case "mob":
                    spawners.add(new MobSpawner(
                            new Mob(this.folder, sign.getLine(1), this.items),
                            Integer.parseInt(sign.getLine(2)),
                            loc,
                            Double.parseDouble(sign.getLine(3))
                    )); loc.getBlock().setType(Material.AIR); break;
                    // Adds a new spawner to the list of child spawners using the current dungeon folder as the folder, the
                    // second line of the sign as the custom mob name, the third line of the sign as the cooldown, and the
                    // fouth line of the sign as the range. Also, it uses the sign location as the spawner location. Duh.
                    // It then removes the sign
                case "start":
                    this.start = loc;
                    loc.getBlock().setType(Material.AIR); break;
                case "next floor":
                    sign.setLine(0, ""); sign.setLine(1, "§6§l[NEXT FLOOR]");
                    sign.update();
                    exits.add(sign); break;
                case "end game":
                    sign.setLine(0, ""); sign.setLine(1, "§a§l[EXIT]");
                    sign.update();
                    exits.add(sign); break;
                case "elite mob":
                    spawners.add(new MobSpawner(
                            sign.getLine(1),
                            Integer.parseInt(sign.getLine(2)),
                            loc,
                            Double.parseDouble(sign.getLine(3))
                    )); loc.getBlock().setType(Material.AIR); break;
                case "rand elite":
                    spawners.add(new MobSpawner(
                            sign.getLine(1),
                            Integer.parseInt(sign.getLine(2)),
                            loc,
                            Double.parseDouble(sign.getLine(3)),
                            floor
                    ));
            }
        }
    }

    public void regen(){
        this.destroy();
        this.generate();
    }

    public void end(){
        Catacombs.removeDungeon(id);
        for (Player player : players.players) {
            player.performCommand("spawn");
            player.getInventory().clear(); player.updateInventory();
        }
    }

    public void destroy(){
        for (MobSpawner spawner : spawners) {
            spawner.destroy(this.size);
        }
        spawners = new ArrayList<>();
        exits = new ArrayList<>();
        start = null;
        for (int x = 0; x <= size; x++) { for (int y = 0; y <= this.tiling * 3; y++) { for (int z = 0; z <= size; z++) {
            Block b = origin.clone().add(x, y, z).getBlock();
            if (b.getType() == Material.CHEST) {
                ((Chest) b.getState()).getBlockInventory().clear();
            } b.setType(Material.AIR);
        }}}
        for (int x = -1; x <= size + 1; x++) { for (int y = 0; y <= this.tiling; y++) {
            getWls(x, y).forEach(loc -> loc.getBlock().setType(Material.AIR));
        }}
        for (Entity current : this.origin.getWorld().getEntities()) {
            if (current instanceof Item) { current.remove(); }
        }
    }// Deletes everything

    private Collection<Location> getWls(int x, int y) {
        return Arrays.asList(
                new Location(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() - 1),
                new Location(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + size),
                new Location(origin.getWorld(), origin.getX() - 1, origin.getY() + y, origin.getZ() + x),
                new Location(origin.getWorld(), origin.getX() + size, origin.getY() + y, origin.getZ() + x)
        );
    }

    public void step(){
        if (players.players.size() == 0) {
            this.end(); return;
        }
        for (MobSpawner spawner : spawners) {
            spawner.step();
        }
    } // Increments the cooldown for every child spawner

    public void parseChunk(Location corner, int size) {
        for (int x = 0; x < size; x++) { for (int y = 0; y < size; y++) { for (int z = 0; z < size; z++) {
            parseSign(corner.clone().add(x, y, z));
        }}}
    } // Checks an area of size x height x size for signs and parses them. Starts from the given corner.

    public void generate() {
        exits = new ArrayList<>();
        floor++;
        double xlimit = origin.getX() + size; double zlimit = origin.getZ() + size;
        for (int x = -1; x <= size + 1; x++) { for (int y = 0; y <= this.tiling; y++) {
            getWls(x, y).forEach(loc -> loc.getBlock().setType(Material.BEDROCK));
        }}
        // Gets the absolute x and z positions to stop at

        boolean exit = false; boolean start = false;
        // Sets the variables for if the exit and start have been placed yet

        int rooms = 0;
        DefinedStructure start_s = null; DefinedStructure exit_s = null;
        try {
            start_s = loadSingleStructure(new File(addToPath(getPath(), this.folder), "start.nbt"));
            exit_s = loadSingleStructure(new File(addToPath(getPath(), this.folder), "exit.nbt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Amount of rooms spawned

        FileConfiguration c = dungeonConfig(this.folder);
        int minRooms = c.getKeys(false).contains("exit_rooms") ? c.getInt("exit_rooms") : 0;
        // Gets dungeon config

        Set<Location> next;
        Set<Location> locations = new HashSet<>(Collections.singletonList(origin));
        Floor currentFloor;
        if (specialFloors.containsKey(floor)) {
            currentFloor = specialFloors.get(floor);
        } else {
            currentFloor = floors.get(randint(0, floors.size() - 1));
        }
        // This loops over locations and adds the locations north and west of them to next, then sets locations to next.
        // This repeats until there are no locations left. It looks like this:
        /*
            - N O
            N O O
            O O X

            In this case, N is the next locations, O is the current locations, X is a location that is finished, and -
            is nothing.
        */

        while (locations.size() > 0) {
            next = new HashSet<>(); // Sets next to nothing, then iterates over the locations
            for (Location loc : locations) {
                rooms++;

                int size;
                if (rooms >= minRooms && !exit && aOk(loc, this.tiling, xlimit, zlimit)) {
                    assert exit_s != null;
                    insertSingleStructure(exit_s, loc, EnumBlockRotation.NONE);
                    parseChunk(loc, this.tiling);
                    size = this.tiling;
                    exit = true;
                } else if (!start && chance(this.size) && aOk(loc, this.tiling, xlimit, zlimit)) {
                    assert start_s != null;
                    insertSingleStructure(start_s, loc, EnumBlockRotation.NONE);
                    parseChunk(loc, this.tiling);
                    size = this.tiling;
                    start = true;
                } else {
                    size = currentFloor.roomInRange(loc, xlimit, zlimit); // Places a room if its in the size limit, returns room size/
                    // Will shrink room a size smaller if it is not in size limit until it simply does not place it.
                    parseChunk(loc, size);
                    // Parses signs
                }

                if (inBounds(loc.clone().add(size, 0, 0), xlimit, zlimit)) {
                    // This checks if the next location is in bounds and isn't already in the list. It is important to make
                    // sure it's not in the list already, as generation times will go up exponentially leading to crashes at
                    // small sizes.
                    next.add(loc.clone().add(size, 0, 0)); // If the room can expand to the left, add it to the list for the next iteration
                }
                // Same thing except for north below.
                if (inBounds(loc.clone().add(0, 0, size), xlimit, zlimit)) {
                    next.add(loc.clone().add(0, 0, size)); // If the room can expand forward, add it to the list for the next iteration
                }
            }
            locations = next; // Set the next iteration, repeat until there are no empty spots left
        }

        for (Player player : players.players) {
            Catacombs.getComb(player).setAttributes();
            player.teleport(this.start);
        }
    }

}

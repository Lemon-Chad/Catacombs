package lemon.is.awesome.catacombs.dungeon;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;

import static lemon.is.awesome.catacombs.Utilities.*;

public class Floor {
    private final String directory;
    public Floor(String folder) {
        this.directory = addToPath("floors", folder);
    }
    public int roomInRange(Location loc, double xlimit, double zlimit) {
        FileConfiguration c = dungeonConfig(directory); Random r = new Random();
        boolean doubles = c.getBoolean("doubles"); boolean triples = c.getBoolean("triples");
        boolean isTwice = false; boolean isThrice = false;

        if (triples) { isThrice = r.nextInt(101) <= Math.round(c.getDouble("triple_chance") * 100); }
        if (doubles) { isTwice = r.nextInt(101) <= Math.round(c.getDouble("double_chance") * 100) && !isThrice; }

        int size = c.getInt("tile_size");

        if (isThrice && aOk(loc, size * 3, xlimit, zlimit)) {
            randomStructure(addToPath(directory, "triples"), loc);
            return size * 3;
        } else if ((isTwice || isThrice) && aOk(loc, size * 2, xlimit, zlimit)) {
            randomStructure(addToPath(directory, "doubles"), loc);
            return size * 2;
        } else {
            if (!aOk(loc, size, xlimit, zlimit)) {return size;}
            randomStructure(addToPath(directory, "singles"), loc);
            return size;
        }
    }
}

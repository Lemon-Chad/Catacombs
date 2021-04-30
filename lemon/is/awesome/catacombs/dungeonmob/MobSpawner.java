package lemon.is.awesome.catacombs.dungeonmob;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

import static lemon.is.awesome.catacombs.Utilities.playerRadius;
import static lemon.is.awesome.catacombs.dungeonmob.Mob.alive;

public class MobSpawner {
    public Mob mob; // This uses the custom Mob class, this is the mob it will spawn
    private int cooldown; // A ticker that counts down until the next spawn
    private final int max_cooldown; // The time between spawns in seconds
    public final Location loc; // The location of the MobSpawner
    public final double aoe; // The range of the spawner
    public int level = 0;
    public int mobCount = 0;
    public ArrayList<Entity> mobs;
    private final String elitemob;
    private final boolean elite;
    private final boolean randelite;
    public MobSpawner(Mob mob, int cooldown, Location loc, double aoe) {
        this.mob = mob; this.cooldown = cooldown; this.max_cooldown = cooldown; this.loc = loc; this.aoe = aoe;
        this.mobs = new ArrayList<>();
        this.elite = false; this.elitemob = null;
        randelite = false;
        /*
            Now why is aoe squared? That seems dumb. The reason it is squared is because later on, I get the distance
         between the spawner location and all players, to see if somebody is in the range. To speed this up, I use
         distanceSquared, a function that is faster but returns the distance... squared. It is faster because it doesn't
         use the square root, so to counteract this I square the range of the spawner so it is equal. I do it in the
         assignment so it isn't repeated later.
         */
    }
    public MobSpawner(String elitemob, int cooldown, Location loc, double aoe) {
        this.elitemob = elitemob; this.cooldown = cooldown; this.loc = loc; this.aoe = aoe;
        this.mobs = new ArrayList<>();
        this.elite = true; this.mob = null; this.max_cooldown = cooldown;
        randelite = false;
    }
    public MobSpawner(String elitemob, int cooldown, Location loc, double aoe, int level) {
        this.elitemob = elitemob; this.cooldown = cooldown; this.loc = loc; this.aoe = aoe;
        this.mobs = new ArrayList<>(); this.level = level;
        this.elite = true; this.mob = null; this.max_cooldown = cooldown;
        randelite = false;
    }

    public void step(){
        // If a player is not in range, stop here
        if (!playerRadius(loc, aoe)) {return;}
        // Decrement the cooldown
        cooldown--;
        // If cooldown is less than or equal to 0, spawns the mob and resets the cooldown
        if (cooldown <= 0 && mobCount <= 10) {
            mobCount++;
            if (!elite) {
                this.mobs.add(mob.spawn(loc));
            } else {
                if (randelite)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            String.format("em spawnlocationcustom %s %s %s %s %s",
                                    elitemob,
                                    loc.getWorld().getName(),
                                    loc.getX(),
                                    loc.getY(),
                                    loc.getZ()));
                else {
                    String c = String.format("em spawnlocationcustomlevel %s %s %s %s %s %s",
                            elitemob,
                            level,
                            loc.getWorld().getName(),
                            Math.round(loc.getX()),
                            Math.round(loc.getY()),
                            Math.round(loc.getZ())
                    );
                    Bukkit.getLogger().info(c);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            c);
                }
            } cooldown = max_cooldown;
        }
    }

    public void destroy(int radius) {
        for (Entity mob : this.mobs) {
            if (alive(mob)) {
                mob.remove();
            }
        } this.mobs = new ArrayList<>();
    }
}

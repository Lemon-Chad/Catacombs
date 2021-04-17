package lemon.is.awesome.catacombs.dungeonmob;

import lemon.is.awesome.catacombs.items.CItem;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static lemon.is.awesome.catacombs.Utilities.*;

public class Mob {
    public EntityType entity;
    private final String name;
    private final ItemStack headArmor; private final ItemStack chestArmor;
    private final ItemStack legArmor; private final ItemStack bootArmor;
    private final ItemStack handItem;
    private final int damage; private final int health;
    public Mob(String dungeon, String name, HashMap<String, CItem> items) {
        FileConfiguration config = loadConfig(addToPath(dungeon, "mobs"), name); // The yml file containing the mob data
        entity = EntityType.fromName(config.getString("mob")); // Gets an EntityType using the name given in the mob data
        this.name = config.getString("name"); // Gets the name given
        Set<String> keys = config.getKeys(false); // List of all recorded keys
        // Loops through each optional parameter, sets them if they are given
        // Probably some better way to do this but idk how
        if(keys.contains("holding")){
            handItem = customMaterial(config.getString("holding"), 1, items);
        } else { handItem = null; }
        if(keys.contains("head")){
            headArmor = customMaterial(config.getString("head"), 1, items);
        } else { headArmor = null; }
        if(keys.contains("chest")){
            chestArmor = customMaterial(config.getString("chest"), 1, items);
        } else { chestArmor = null; }
        if(keys.contains("legs")){
            legArmor = customMaterial(config.getString("legs"), 1, items);
        } else { legArmor = null; }
        if(keys.contains("boots")){
            bootArmor = customMaterial(config.getString("boots"), 1, items);
        } else { bootArmor = null; }
        if(keys.contains("damage")){
            damage = config.getInt("damage");
        } else { damage = -294202; }
        if(keys.contains("health")){
            health = config.getInt("health");
        } else { health = -294202; } // I used -294202 as a subsitution for null
    }

    public Entity spawn(Location loc) { // Spawns custom mob
        Entity e = loc.getWorld().spawnEntity(loc, entity); // Spawns the entity and assigns it to a var
        e.setCustomName(name); // Sets the name to the custom name and makes it visible
        e.setCustomNameVisible(true);
        LivingEntity le = (LivingEntity) e; // Dumb
        EntityEquipment equipment = le.getEquipment(); // Gets the mobs equipment
        assert equipment != null; // idk IDE said to do it
        // Assigns all optional variables if they were given
        if (handItem != null) {equipment.setItem(EquipmentSlot.HAND, handItem, true);}
        if (bootArmor != null) {equipment.setItem(EquipmentSlot.FEET, bootArmor, true);}
        if (legArmor != null) {equipment.setItem(EquipmentSlot.LEGS, legArmor, true);}
        if (chestArmor != null) {equipment.setItem(EquipmentSlot.CHEST, chestArmor, true);}
        if (headArmor != null) {equipment.setItem(EquipmentSlot.HEAD, headArmor, true);}
        if (damage != -294202) {
            Objects.requireNonNull(le.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(damage);
        }
        if (health != -294202) {
            Objects.requireNonNull(le.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
            le.setHealth(health);
        }
        return e;
    }

    public static boolean alive(Entity e) {
        return !e.isDead();
    }
}

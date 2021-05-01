package lemon.is.awesome.catacombs.items;

import static lemon.is.awesome.catacombs.Utilities.addToPath;
import static lemon.is.awesome.catacombs.Utilities.loadConfig;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CItem {
    private final Material item;
    private final String name;
    private final double damage;
    private List<String> lore = new ArrayList<>();
    public CItem(String folder, String item) {
        FileConfiguration dat = loadConfig(addToPath(folder, "items"), item);
        this.item = Material.getMaterial(Objects.requireNonNull(dat.getString("item")));
        this.name = Objects.requireNonNull(dat.getString("name")).replaceAll("&", "§");
        if (dat.getKeys(false).contains("damage")) {
            this.damage = dat.getDouble("damage");
        } else {
            this.damage = -29838493;
        }
        if (dat.getKeys(false).contains("lore")) {
            dat.getStringList("lore").forEach((String element) -> this.lore.add(element.replaceAll("&", "§")));
        }
    }

    public ItemStack get(int count) {
        ItemStack i = new ItemStack(this.item, count); ItemMeta meta = i.getItemMeta();
        if (this.damage != -29838493) {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "generic.attackDamage",
                            damage,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HAND
                    )
            );
        }
        meta.setLore(this.lore);
        meta.setDisplayName(this.name);
        i.setItemMeta(meta);
        return i;
    }
}

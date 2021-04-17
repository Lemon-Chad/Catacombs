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

import java.util.Objects;
import java.util.UUID;

public class CItem {
    private final Material item;
    private final String name;
    private final double damage;
    public CItem(String folder, String item) {
        Bukkit.getLogger().info(folder);
        FileConfiguration dat = loadConfig(addToPath(folder, "items"), item);
        this.item = Material.getMaterial(Objects.requireNonNull(dat.getString("item")));
        this.name = dat.getString("name");
        if (dat.getKeys(false).contains("damage")) {
            this.damage = dat.getDouble("damage");
        } else {
            this.damage = -29838493;
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
        meta.setDisplayName(this.name);
        i.setItemMeta(meta);
        return i;
    }
}

package lemon.is.awesome.catacombs.loottable;

import org.bukkit.inventory.ItemStack;

public class Entry {
    private final int weight; // Weight of the item
    private final ItemStack item; // The item
    private double chance; // The chance (%) of the item

    public Entry(ItemStack item, int weight) {
        this.item = item; this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return this.chance;
    }

    public ItemStack getItem() {
        return this.item;
    }
}

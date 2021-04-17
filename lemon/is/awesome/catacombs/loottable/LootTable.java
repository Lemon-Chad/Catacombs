package lemon.is.awesome.catacombs.loottable;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LootTable {

    private final ArrayList<Entry> entries; // The items in the table

    public LootTable(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public ItemStack getRandom() { // Gets a random item
        double random = Math.random(); // The chance

        for (int i = 0; i < entries.size() - 1; i++) { // Loops over all entries
            Entry entry = entries.get(i + 1);
            // I don't really remember how this works, it just does
            if (entry.getChance() > random) {
                return entries.get(i).getItem();
            }
        }

        return entries.get(entries.size() - 1).getItem(); // Return the last item if no item is chosen somehow
    }

    public static class LootTableBuilder {

        private int totalWeight = 0; // The combined weights of all the items
        private final ArrayList<Entry> entries = new ArrayList<>(); // The different items

        public void add(ItemStack item, int weight) { // Function for adding items to tables
            totalWeight += weight; // Adds the weight to the total weight
            entries.add(new Entry(item, weight)); // Adds a new entry to the entries list
        }

        public boolean isBuilt(){
            return entries.size() > 0 && totalWeight > 0;
        } // Checks if the table has at least 1 item

        public LootTable build(){
            if (!isBuilt()) // If there are no items, cry
                return null;

            double base = 0; // I also can't remember how this works lol :P
            for (Entry entry : entries) {
                double chance = getChance(base);
                entry.setChance(chance);
                base += entry.getWeight();
            }

            return new LootTable(entries); // Returns a loot table with correctly configured entries
        }

        private double getChance(double weight){
            return weight / totalWeight;
        }

    }
}

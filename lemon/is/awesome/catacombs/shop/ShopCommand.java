package lemon.is.awesome.catacombs.shop;

import lemon.is.awesome.catacombs.Catacombs;
import lemon.is.awesome.catacombs.Comb;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String s, String[] args) {
        if (cmd.getName().equals("shop") && args.length == 0) {
            Player p = (Player) sender;

            Inventory gui = genventory(p);

            p.openInventory(gui);
            return true;
        }
        return false;
    }

    public static Inventory genventory(Player p) {
        int guisize = 45; Comb comb = Catacombs.getComb(p);
        Inventory gui = Bukkit.createInventory(p, guisize, "§6§lDungeon Shop");

        for (int x = 0; x < guisize; x++) {
            gui.setItem(x, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        }

        ItemStack he = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemStack st = new ItemStack(Material.IRON_SWORD, 1);
        ItemStack sp = new ItemStack(Material.LEATHER_BOOTS, 1);
        ItemStack balance = new ItemStack(Material.GOLD_NUGGET, 1);

        ItemMeta balance_meta = balance.getItemMeta();
        balance_meta.setDisplayName(ChatColor.GOLD + String.valueOf(comb.getBalance()) + " Catacoins");
        balance_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        balance_meta.addEnchant(Enchantment.SILK_TOUCH, 32, true);
        balance.setItemMeta(balance_meta);

        ItemMeta stm = st.getItemMeta();
        stm.setDisplayName(ChatColor.RED + "Upgrade Strength");
        ArrayList<String> stml = new ArrayList<>();
        stml.add(String.format("§aCurrent Value: §6%s", comb.getDamage()));
        stml.add(String.format("§bUpgrade Cost: §6%s", comb.getDamage() * Catacombs.damageprice));
        stm.setLore(stml);
        st.setItemMeta(stm);

        ItemMeta spm = sp.getItemMeta();
        spm.setDisplayName(ChatColor.AQUA + "Upgrade Speed");
        ArrayList<String> spml = new ArrayList<>();
        Bukkit.getLogger().info(String.valueOf(comb.getSpeed()));
        spml.add(String.format("§aCurrent Value: §6%s", Math.round(comb.getSpeed() * 250)));
        spml.add(String.format("§bUpgrade Cost: §6%s", Math.round(comb.getSpeed() * Catacombs.speedprice)));
        spm.setLore(spml);
        sp.setItemMeta(spm);

        ItemMeta hem = he.getItemMeta();
        hem.setDisplayName(ChatColor.YELLOW + "Upgrade Health");
        ArrayList<String> heml = new ArrayList<>();
        heml.add(String.format("§aCurrent Value: §6%s", comb.getHealth()));
        heml.add(String.format("§bUpgrade Cost: §6%s", comb.getHealth() * Catacombs.healthprice));
        hem.setLore(heml);
        he.setItemMeta(hem);

        ItemStack[] menu_items = {sp, he, st};
        for (int x = 0; x < 3; x++) {
            gui.setItem(x * 3 + 10, menu_items[x]);
        }

        gui.setItem(31, balance);

        return gui;
    }
}

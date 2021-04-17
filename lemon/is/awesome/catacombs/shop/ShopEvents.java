package lemon.is.awesome.catacombs.shop;

import lemon.is.awesome.catacombs.Catacombs;
import lemon.is.awesome.catacombs.Comb;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

import static lemon.is.awesome.catacombs.shop.ShopCommand.genventory;

public class ShopEvents implements Listener {
    @EventHandler
    public void clickEvent(InventoryClickEvent e) {

        if (!e.getView().getTitle().equalsIgnoreCase("§6§lDungeon Shop")) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        int vol = 1; int pit = 1;
        Comb comb = Catacombs.getComb(player);

        switch ( Objects.requireNonNull(e.getCurrentItem()).getType() ) {
            case GOLDEN_APPLE:
                if (comb.getBalance() >= comb.getHealth() * Catacombs.healthprice) {
                    comb.setBalance(comb.getBalance() - comb.getHealth() * Catacombs.healthprice);
                    comb.setHealth(comb.getHealth() + 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, vol, pit); break;
                }
            case IRON_SWORD:
                if (comb.getBalance() >= comb.getDamage() * Catacombs.damageprice) {
                    comb.setBalance(comb.getBalance() - comb.getDamage() * Catacombs.damageprice);
                    comb.setDamage(comb.getDamage() + 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, vol, pit); break;
                }
            case LEATHER_BOOTS:
                if (comb.getBalance() >= Math.round(comb.getSpeed() * Catacombs.speedprice)) {
                    comb.setBalance((int) (comb.getBalance() - Math.round(comb.getSpeed() * Catacombs.speedprice)));
                    comb.setSpeed(comb.getSpeed() + 0.025);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, vol, pit); break;
                }
        }

        comb.setAttributes();

        player.closeInventory();
        player.openInventory(genventory(player));

        e.setCancelled(true);
    }
}

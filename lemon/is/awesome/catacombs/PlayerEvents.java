package lemon.is.awesome.catacombs;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.getInventory().clear(); p.updateInventory();
        Catacombs.getGroup(p).removeMember(p);
    }
}

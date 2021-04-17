package lemon.is.awesome.catacombs;

import lemon.is.awesome.catacombs.dungeon.Dungeon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignHandler implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            assert b != null;
            if (b.getType() == Material.OAK_WALL_SIGN) {
                Sign s = (Sign) b.getState();
                if (s.getLine(0).equals("play comb")) {
                    s.setLine(1, String.format("§6%s", s.getLine(1)));
                    s.setLine(0, "§a[PLAY]");
                    s.setLine(2, String.format("§e%sx%s", s.getLine(2), s.getLine(2)));
                    s.update(); return;
                }
                if (s.getLine(0).equals("§a[PLAY]")) {
                    String dungeon = s.getLine(1).replace("§6", "");
                    int size = Integer.parseInt(s.getLine(2).replace("§e", "").split("x")[0]);
                    Catacombs.startDungeon(dungeon, Catacombs.getGroup(e.getPlayer()), size, e.getPlayer().getLocation());
                    return;
                }
                for (Dungeon dungeon : Catacombs.getDungeons()) {
                    for (Sign exit : dungeon.exits) {
                        if (exit.getLocation().equals(s.getLocation())) {
                            if (s.getLine(1).equals("§6§l[NEXT FLOOR]")) { dungeon.regen(); }
                            else if (s.getLine(1).equals("§a§l[EXIT]")) { 
                                dungeon.end(); 
                                for (Player p : dungeon.getPlayers().players) {
                                    Catacombs.transferBalance(p);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }

    }
}

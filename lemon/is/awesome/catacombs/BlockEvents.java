package lemon.is.awesome.catacombs;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Objects;

public class BlockEvents implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        b.setMetadata("placer", new FixedMetadataValue(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Catacombs")), e.getPlayer().getUniqueId()));
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        List<MetadataValue> x = b.getMetadata("placer");
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || Catacombs.breaklist.contains(b.getType())) { return; }
        for (MetadataValue val : x) {
            if (Objects.equals(val.getOwningPlugin(), Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Catacombs")))) {
                if (Objects.equals(val.value(), e.getPlayer().getUniqueId())) {
                    return;
                }
            }
        } e.setCancelled(true);
    }
}

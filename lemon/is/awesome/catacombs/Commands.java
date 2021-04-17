package lemon.is.awesome.catacombs;

import lemon.is.awesome.catacombs.dungeon.Dungeon;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static lemon.is.awesome.catacombs.Utilities.*;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String s, String[] args) {
        if (cmd.getName().equals("lemonsFinalMessage") && args.length == 1) {
            for (long i : waitWhat(Long.parseLong(args[0]))) {
                sender.sendMessage(String.valueOf(i));
            } return true;
        }
        if (cmd.getName().equals("catacoins") && args.length == 0) {
            sender.sendMessage("§aYou have §6" + Catacombs.getComb((Player) sender).getBalance() + " Catacoins.");
            sender.sendMessage("§bSafely exit dungeons to earn more!");
            return true;
        }
        if (cmd.getName().equals("generate") && (args.length == 2 || args.length == 5)) {
            int size = Integer.parseInt(args[1]);
            Location loc;
            if (args.length == 5) {loc = commandLocation(sender, 2, args);}
            else {loc = getLocation(sender);}

            Dungeon dungeon = new Dungeon(args[0],
                    Catacombs.getGroup((Player) sender),
                    loc, size);
            dungeon.generate();
            sender.sendMessage("Created dungeon with id " + dungeon.getId());
            return true;
        }
        if (cmd.getName().equals("destroydungeon") && args.length == 1) {
            lemon.is.awesome.catacombs.Catacombs.removeDungeon(Integer.parseInt(args[0])); return true;
        }
        if (cmd.getName().equals("cata_refresh") && args.length == 0) {
            lemon.is.awesome.catacombs.Catacombs.refresh(); return true;
        }
        return false;
    }
}

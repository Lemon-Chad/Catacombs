package lemon.is.awesome.catacombs.groups;

import lemon.is.awesome.catacombs.Catacombs;
import lemon.is.awesome.catacombs.dungeon.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GroupCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (cmd.getName().equals("invitegroup") && args.length == 1) {
            Group g = Catacombs.getGroup((Player) sender);
            Player target = Bukkit.getPlayer(args[0]);
            if (target == sender) {
                sender.sendMessage("§cYou cannot invite yourself!"); return true;
            }
            assert target != null;
            if (target.getLocation().getWorld() != ((Player) sender).getLocation().getWorld()) {
                sender.sendMessage("§cThat player is not in the same world!"); return true;
            }
            GroupInvite invite = new GroupInvite(g, target);
            sender.sendMessage("§aInvite sent!");
            target.sendMessage(String.format("§aYou were invited to %s's group!", sender.getName()));
            target.sendMessage(String.format("§6Run the command §7§o/joingroup %s§6 to join", invite.id));
            return true;
        }
        if (cmd.getName().equals("leavegroup")) {
            Player p = (Player) sender;
            Group g = Catacombs.getGroup(p);
            g.removeMember(p);
            sender.sendMessage("§aLeft group :(");
            Dungeon game = Catacombs.inGame(p);
            if (game != null) {p.performCommand("spawn"); p.getInventory().clear(); p.updateInventory();}
            return true;
        }
        if (cmd.getName().equals("joingroup") && args.length == 1) {
            for (GroupInvite invite : Catacombs.invites) {
                if (invite.id == Integer.parseInt(args[0])) {
                    sender.sendMessage("§aJoined group!");
                    invite.accept(); return true; }
            }
            sender.sendMessage("§cThat invite does not exist or has expired!");
            return true;
        }
        return false;
    }
}

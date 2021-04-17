package lemon.is.awesome.catacombs.groups;

import lemon.is.awesome.catacombs.Catacombs;
import org.bukkit.entity.Player;

import java.util.Set;

public class Group {
    public Set<Player> players;

    public Group(Set<Player> players) {
        this.players = players;
        Catacombs.groups.add(this);
    }
    public void removeMember(Player p) {
        players.remove(p);
        if (players.size() == 0) {
            Catacombs.groups.remove(this);
        }
    }
    public void addMember(Player p) {
        for (Group x : Catacombs.groups) {
            x.removeMember(p);
        }
        players.add(p);
    }
}

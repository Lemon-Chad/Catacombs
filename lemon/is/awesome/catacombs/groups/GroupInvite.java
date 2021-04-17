package lemon.is.awesome.catacombs.groups;

import lemon.is.awesome.catacombs.Catacombs;
import org.bukkit.entity.Player;

import static lemon.is.awesome.catacombs.Utilities.randint;

public class GroupInvite {
    private final Group group;
    private final Player invitee;
    private int expiration = 60;
    public final int id;
    public GroupInvite(Group group, Player invitee) {
        this.group = group;
        this.invitee = invitee;
        this.id = randint(1, 1000);
        Catacombs.invites.add(this);
    }
    public void step() {
        expiration--;
        if (expiration <= 0) {
            Catacombs.invites.remove(this);
        }
    }
    public void accept() {
        this.group.addMember(invitee);
        this.expiration = 0; this.step();
    }
}

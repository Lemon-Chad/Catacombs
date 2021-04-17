package lemon.is.awesome.catacombs;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static lemon.is.awesome.catacombs.Utilities.getPath;
import static lemon.is.awesome.catacombs.Utilities.suid;

public class Comb {
    private int balance = 0;
    private int health = 20;
    private int damage = 1;
    private double speed = 0.1;
    public final FileConfiguration data;
    public final Player player;
    public final String uuid;
    public Comb(Player p) {
        player = p;
        uuid = suid(p);
        data = YamlConfiguration.loadConfiguration(new File(getPath(), "data.yml"));
        if (!Objects.requireNonNull(data.getConfigurationSection("Players")).getKeys(false).contains(uuid)) {
            this.dump();
        } else {
            balance = data.getInt("Players." + uuid + ".bal");
            health = data.getInt("Players." + uuid + ".hea");
            damage = data.getInt("Players." + uuid + ".dam");
            speed = data.getDouble("Players." + uuid + ".spe");
        }
        Catacombs.combs.add(this);
    }

    public void setBalance(int bal) {
        balance = bal; dump();
    }
    public void setHealth(int hea) {
        health = hea; dump();
    }
    public void setDamage(int dam) {
        damage = dam; dump();
    }
    public void setSpeed(double spe) {
        speed = spe; dump();
    }

    public int getBalance() {return balance;}
    public int getHealth() {return health;}
    public int getDamage() {return damage;}
    public double getSpeed() {return speed;}

    public void setAttributes() {
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(damage);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
    }

    public void dump() {
        data.set("Players." + uuid + ".bal", balance);
        data.set("Players." + uuid + ".hea", health);
        data.set("Players." + uuid + ".dam", damage);
        data.set("Players." + uuid + ".spe", speed);

        try {
            data.save(new File(getPath(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

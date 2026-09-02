package io.github.naysurgithub.gravestone.config;

import org.powernukkitx.utils.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Immutable snapshot of config.yml. Rebuilt on /gravestone reload.
 */
public final class GraveStoneConfig {

    public enum RecoveryMode {
        BREAK,
        INTERACT;

        static RecoveryMode parse(String raw) {
            if (raw != null && raw.trim().equalsIgnoreCase("interact")) {
                return INTERACT;
            }
            return BREAK;
        }
    }

    private final boolean allWorlds;
    private final Set<String> enabledWorlds;
    private final RecoveryMode recoveryMode;
    private final double gravestoneHardness;
    private final boolean saveXpInGrave;
    private final boolean ownerOnly;
    private final boolean directToInventory;
    private final String messageDeath;
    private final String messageRecovered;
    private final String messageNotOwner;

    public GraveStoneConfig(Config config) {
        List<String> worlds = config.getStringList("enabled-worlds");
        this.allWorlds = worlds == null || worlds.isEmpty() || worlds.contains("*");
        this.enabledWorlds = new HashSet<>();
        if (worlds != null) {
            for (String world : worlds) {
                this.enabledWorlds.add(world.toLowerCase(Locale.ROOT));
            }
        }

        this.recoveryMode = RecoveryMode.parse(config.getString("recovery-mode", "break"));
        this.gravestoneHardness = Math.max(0.0, config.getDouble("gravestone-hardness", 0.5));
        this.saveXpInGrave = config.getBoolean("save-xp-in-grave", true);
        this.ownerOnly = config.getBoolean("owner-only", true);
        this.directToInventory = config.getBoolean("direct-to-inventory", true);
        this.messageDeath = config.getString("messages.death", "");
        this.messageRecovered = config.getString("messages.recovered", "");
        this.messageNotOwner = config.getString("messages.not-owner", "");
    }

    public boolean isWorldEnabled(String worldName) {
        return allWorlds || enabledWorlds.contains(worldName.toLowerCase(Locale.ROOT));
    }

    public RecoveryMode recoveryMode() {
        return recoveryMode;
    }

    public double gravestoneHardness() {
        return gravestoneHardness;
    }

    public boolean saveXpInGrave() {
        return saveXpInGrave;
    }

    public boolean ownerOnly() {
        return ownerOnly;
    }

    public boolean directToInventory() {
        return directToInventory;
    }

    public String messageDeath() {
        return messageDeath;
    }

    public String messageRecovered() {
        return messageRecovered;
    }

    public String messageNotOwner() {
        return messageNotOwner;
    }
}

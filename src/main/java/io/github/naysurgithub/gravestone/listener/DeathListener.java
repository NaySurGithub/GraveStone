package io.github.naysurgithub.gravestone.listener;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import io.github.naysurgithub.gravestone.config.GraveStoneConfig;
import org.powernukkitx.Player;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.player.PlayerDeathEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Position;

import java.util.ArrayList;
import java.util.List;

public final class DeathListener implements Listener {

    private final GraveStonePlugin plugin;

    public DeathListener(GraveStonePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GraveStoneConfig settings = plugin.getSettings();
        Player player = event.getEntity();

        if (!settings.isWorldEnabled(player.getLevel().getName())) {
            return;
        }
        if (event.getKeepInventory()) {
            return;
        }

        List<Item> items = new ArrayList<>();
        for (Item item : event.getDrops()) {
            if (item == null || item.isNull()) {
                continue;
            }
            if (item.hasEnchantment(Enchantment.ID_VANISHING_CURSE)) {
                continue;
            }
            items.add(item);
        }

        int xpLevels = settings.saveXpInGrave() ? event.getExperience() : 0;
        if (items.isEmpty() && xpLevels <= 0) {
            return;
        }

        Position grave = plugin.getGraveManager().createGrave(player, items, xpLevels);
        if (grave == null) {
            return;
        }

        event.setDrops(Item.EMPTY_ARRAY);
        if (settings.saveXpInGrave()) {
            event.setExperience(0);
        }

        String message = settings.messageDeath();
        if (!message.isEmpty()) {
            player.sendMessage(message
                    .replace("{x}", String.valueOf(grave.getFloorX()))
                    .replace("{y}", String.valueOf(grave.getFloorY()))
                    .replace("{z}", String.valueOf(grave.getFloorZ()))
                    .replace("{world}", player.getLevel().getName())
                    .replace("{player}", player.getName()));
        }
    }
}

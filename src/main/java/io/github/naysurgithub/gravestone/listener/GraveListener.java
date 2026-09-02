package io.github.naysurgithub.gravestone.listener;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import io.github.naysurgithub.gravestone.block.BlockGravestone;
import io.github.naysurgithub.gravestone.blockentity.BlockEntityGravestone;
import io.github.naysurgithub.gravestone.config.GraveStoneConfig;
import org.powernukkitx.Player;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.block.BlockBreakEvent;
import org.powernukkitx.event.entity.EntityLevelChangeEvent;
import org.powernukkitx.event.player.PlayerInteractEvent;
import org.powernukkitx.event.player.PlayerJoinEvent;
import org.powernukkitx.event.player.PlayerQuitEvent;

public final class GraveListener implements Listener {

    private static final String BYPASS_PERMISSION = "gravestone.bypass";

    private final GraveStonePlugin plugin;

    public GraveListener(GraveStonePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!(event.getBlock() instanceof BlockGravestone block)) {
            return;
        }
        if (plugin.getSettings().recoveryMode() != GraveStoneConfig.RecoveryMode.INTERACT) {
            return;
        }

        event.setCancelled(true);

        BlockEntityGravestone grave = block.getBlockEntity();
        if (grave == null || grave.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        if (!mayLoot(player, grave)) {
            return;
        }

        plugin.getGraveManager().recover(player, grave);
        plugin.getGraveManager().removeGrave(grave);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!(event.getBlock() instanceof BlockGravestone block)) {
            return;
        }

        BlockEntityGravestone grave = block.getBlockEntity();
        if (grave == null || grave.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        if (!mayLoot(player, grave)) {
            event.setCancelled(true);
            return;
        }

        // Recovery on break works in both modes so a broken grave can never
        // destroy its content.
        plugin.getGraveManager().recover(player, grave);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BlockEntityGravestone.syncHolograms(player, player.getLevel());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            BlockEntityGravestone.syncHolograms(player, event.getTarget());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BlockEntityGravestone.forgetPlayer(event.getPlayer());
    }

    private boolean mayLoot(Player player, BlockEntityGravestone grave) {
        if (!plugin.getSettings().ownerOnly()) {
            return true;
        }
        if (grave.isOwner(player) || player.hasPermission(BYPASS_PERMISSION)) {
            return true;
        }
        String message = plugin.getSettings().messageNotOwner();
        if (!message.isEmpty()) {
            player.sendMessage(message);
        }
        return false;
    }
}

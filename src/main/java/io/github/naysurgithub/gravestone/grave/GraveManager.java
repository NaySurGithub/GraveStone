package io.github.naysurgithub.gravestone.grave;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import io.github.naysurgithub.gravestone.block.BlockGravestone;
import io.github.naysurgithub.gravestone.blockentity.BlockEntityGravestone;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;

public final class GraveManager {

    private static final int SEARCH_RANGE = 4;

    private final GraveStonePlugin plugin;

    public GraveManager(GraveStonePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Places a gravestone at (or near) the death position and stores the
     * given content in its block entity.
     *
     * @return the grave position, or null if no suitable spot was found
     *         (the caller should then let vanilla drops happen)
     */
    public Position createGrave(Player player, List<Item> items, int xpLevels) {
        Level level = player.getLevel();
        Vector3 spot = findSpot(level, player.getPosition().floor());
        if (spot == null) {
            return null;
        }

        if (!level.setBlock(spot, new BlockGravestone(), true, true)) {
            return null;
        }

        CompoundTag nbt = BlockEntity.getDefaultCompound(spot, BlockEntityGravestone.ID)
                .putBoolean("isMovable", false);
        BlockEntity blockEntity = BlockEntity.createBlockEntity(
                BlockEntityGravestone.ID,
                level.getChunk(spot.getFloorX() >> 4, spot.getFloorZ() >> 4),
                nbt);

        if (!(blockEntity instanceof BlockEntityGravestone grave)) {
            level.setBlock(spot, Block.get(BlockID.AIR), true, true);
            return null;
        }

        grave.setContents(player.getUniqueId(), player.getName(), items, xpLevels, System.currentTimeMillis());
        return Position.fromObject(spot, level);
    }

    /**
     * Gives the grave content back to the player and removes the grave block.
     */
    public void recover(Player player, BlockEntityGravestone grave) {
        List<Item> items = grave.getItems();
        int xpLevels = grave.getXpLevels();
        Vector3 dropPos = grave.add(0.5, 0.5, 0.5);

        grave.clearContents();

        if (plugin.getSettings().directToInventory()) {
            Item[] leftovers = player.getInventory().addItem(items.toArray(Item.EMPTY_ARRAY));
            for (Item leftover : leftovers) {
                player.getLevel().dropItem(dropPos, leftover);
            }
        } else {
            for (Item item : items) {
                player.getLevel().dropItem(dropPos, item);
            }
        }

        if (xpLevels > 0) {
            player.setExperience(player.getExperience(), player.getExperienceLevel() + xpLevels);
        }

        String message = plugin.getSettings().messageRecovered();
        if (!message.isEmpty()) {
            player.sendMessage(message);
        }
    }

    public void removeGrave(BlockEntityGravestone grave) {
        Level level = grave.getLevel();
        Vector3 pos = new Vector3(grave.getFloorX(), grave.getFloorY(), grave.getFloorZ());
        grave.close();
        level.setBlock(pos, Block.get(BlockID.AIR), true, true);
    }

    private Vector3 findSpot(Level level, Vector3 origin) {
        int minY = level.getMinHeight();
        int maxY = level.getMaxHeight() - 1;
        int baseY = Math.min(Math.max(origin.getFloorY(), minY), maxY);
        int x = origin.getFloorX();
        int z = origin.getFloorZ();

        for (int offset = 0; offset <= SEARCH_RANGE; offset++) {
            int up = baseY + offset;
            if (up <= maxY && isReplaceable(level, x, up, z)) {
                return new Vector3(x, up, z);
            }
            int down = baseY - offset;
            if (offset > 0 && down >= minY && isReplaceable(level, x, down, z)) {
                return new Vector3(x, down, z);
            }
        }
        return null;
    }

    private boolean isReplaceable(Level level, int x, int y, int z) {
        Block block = level.getBlock(x, y, z);
        return block.isAir() || block.canBeReplaced();
    }
}

package io.github.naysurgithub.gravestone.blockentity;

import io.github.naysurgithub.gravestone.block.BlockGravestone;
import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.ItemHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Holds the content of a grave. Everything is serialized to the chunk NBT
 * through {@link #saveNBT()} and restored by {@link #loadNBT()}, so graves
 * survive server restarts.
 */
public class BlockEntityGravestone extends BlockEntity {

    public static final String ID = "GraveStone";

    private static final String TAG_OWNER_UUID = "OwnerUUID";
    private static final String TAG_OWNER_NAME = "OwnerName";
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_XP_LEVELS = "XpLevels";
    private static final String TAG_DEATH_TIME = "DeathTime";

    private UUID ownerUuid;
    private String ownerName = "";
    private final List<Item> items = new ArrayList<>();
    private int xpLevels;
    private long deathTime;

    public BlockEntityGravestone(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.ownerUuid = parseUuid(nbt.getString(TAG_OWNER_UUID));
        this.ownerName = nbt.getString(TAG_OWNER_NAME);
        this.xpLevels = nbt.getInt(TAG_XP_LEVELS);
        this.deathTime = nbt.getLong(TAG_DEATH_TIME);
        this.items.clear();
        if (nbt.containsList(TAG_ITEMS)) {
            for (CompoundTag tag : nbt.getList(TAG_ITEMS, CompoundTag.class).getAll()) {
                Item item = ItemHelper.read(tag);
                if (item != null && !item.isNull()) {
                    this.items.add(item);
                }
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        nbt.putString(TAG_OWNER_UUID, ownerUuid != null ? ownerUuid.toString() : "");
        nbt.putString(TAG_OWNER_NAME, ownerName != null ? ownerName : "");
        nbt.putInt(TAG_XP_LEVELS, xpLevels);
        nbt.putLong(TAG_DEATH_TIME, deathTime);
        ListTag<CompoundTag> list = new ListTag<>();
        int slot = 0;
        for (Item item : items) {
            list.add(ItemHelper.write(item, slot++));
        }
        nbt.putList(TAG_ITEMS, list);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock() instanceof BlockGravestone;
    }

    public void setContents(UUID ownerUuid, String ownerName, List<Item> items, int xpLevels, long deathTime) {
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.items.clear();
        this.items.addAll(items);
        this.xpLevels = xpLevels;
        this.deathTime = deathTime;
        saveNBT();
        setDirty();
    }

    public void clearContents() {
        this.items.clear();
        this.xpLevels = 0;
        saveNBT();
        setDirty();
    }

    public boolean isOwner(Player player) {
        return ownerUuid != null && ownerUuid.equals(player.getUniqueId());
    }

    public boolean isEmpty() {
        return items.isEmpty() && xpLevels <= 0;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public int getXpLevels() {
        return xpLevels;
    }

    public long getDeathTime() {
        return deathTime;
    }

    private static UUID parseUuid(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

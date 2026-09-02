package io.github.naysurgithub.gravestone.blockentity;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import io.github.naysurgithub.gravestone.block.BlockGravestone;
import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.network.primitiveshape.PrimitiveShapes;
import org.powernukkitx.utils.ItemHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Set<BlockEntityGravestone> ACTIVE_GRAVES = ConcurrentHashMap.newKeySet();

    private UUID ownerUuid;
    private String ownerName;
    private List<Item> items;
    private int xpLevels;
    private long deathTime;
    private final Map<Player, Integer> hologramViewers = new ConcurrentHashMap<>();

    public BlockEntityGravestone(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * Shows the holograms of the given level to the player and hides the
     * ones from any other level. Call on join and on level change.
     */
    public static void syncHolograms(Player player, Level level) {
        for (BlockEntityGravestone grave : ACTIVE_GRAVES) {
            if (grave.getLevel() == level) {
                grave.showHologramTo(player);
            } else {
                grave.hideHologramFrom(player);
            }
        }
    }

    /** Drops all hologram tracking for a disconnecting player. */
    public static void forgetPlayer(Player player) {
        for (BlockEntityGravestone grave : ACTIVE_GRAVES) {
            grave.hologramViewers.remove(player);
        }
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        ACTIVE_GRAVES.add(this);
        showHologramToLevel();
    }

    @Override
    public void close() {
        ACTIVE_GRAVES.remove(this);
        hideHologramFromAll();
        super.close();
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.ownerUuid = parseUuid(nbt.getString(TAG_OWNER_UUID));
        this.ownerName = nbt.getString(TAG_OWNER_NAME);
        this.xpLevels = nbt.getInt(TAG_XP_LEVELS);
        this.deathTime = nbt.getLong(TAG_DEATH_TIME);
        this.items = new ArrayList<>();
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
        hideHologramFromAll();
        showHologramToLevel();
    }

    private String hologramText() {
        GraveStonePlugin plugin = GraveStonePlugin.getInstance();
        if (plugin == null || !plugin.getSettings().hologramEnabled()) {
            return "";
        }
        if (ownerName == null || ownerName.isEmpty()) {
            return "";
        }
        return plugin.getSettings().hologramText().replace("{player}", ownerName);
    }

    private void showHologramToLevel() {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }
        for (Player player : level.getPlayers().values()) {
            showHologramTo(player);
        }
    }

    private void showHologramTo(Player player) {
        if (hologramViewers.containsKey(player)) {
            return;
        }
        String text = hologramText();
        if (text.isEmpty()) {
            return;
        }
        int networkId = PrimitiveShapes.text(this.add(0.5, 1.3, 0.5), text)
                .maxRenderDistance(48f)
                .showTo(player);
        hologramViewers.put(player, networkId);
    }

    private void hideHologramFrom(Player player) {
        Integer networkId = hologramViewers.remove(player);
        if (networkId != null && player.isOnline()) {
            PrimitiveShapes.remove(player, networkId);
        }
    }

    private void hideHologramFromAll() {
        for (Player player : new ArrayList<>(hologramViewers.keySet())) {
            hideHologramFrom(player);
        }
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

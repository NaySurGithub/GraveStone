package io.github.naysurgithub.gravestone.block;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import io.github.naysurgithub.gravestone.blockentity.BlockEntityGravestone;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTransparent;
import org.powernukkitx.block.customblock.CustomBlock;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.Vector3f;

public class BlockGravestone extends BlockTransparent implements CustomBlock, BlockEntityHolder<BlockEntityGravestone> {

    public static final String ID = "gravestone:gravestone";
    public static final BlockProperties PROPERTIES = new BlockProperties(ID);

    private static volatile CustomBlockDefinition definition;

    public BlockGravestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGravestone(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Gravestone";
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        CustomBlockDefinition local = definition;
        if (local == null) {
            float clientBreakSeconds = (float) (getHardness() * 1.5);
            local = CustomBlockDefinition.builder(this)
                    .name("Gravestone")
                    .geometry("geometry.tombstone")
                    .texture("gravestone")
                    .destructibleByMining(clientBreakSeconds)
                    .destructibleByExplosion(false)
                    .collisionBox(new Vector3f(-8, 0, -8), new Vector3f(16, 2, 16))
                    .collisionBox(new Vector3f(-6, 2, -4), new Vector3f(12, 2, 10))
                    .collisionBox(new Vector3f(-5, 2, -7), new Vector3f(10, 13, 3))
                    .selectionBox(new Vector3f(-8, 0, -8), new Vector3f(16, 15, 16))
                    .isHiddenInCommands(true)
                    .build();
            definition = local;
        }
        return local;
    }

    @Override
    public double getHardness() {
        GraveStonePlugin plugin = GraveStonePlugin.getInstance();
        return plugin != null ? plugin.getSettings().gravestoneHardness() : 0.5;
    }

    @Override
    public double getResistance() {
        return 3_600_000;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityGravestone> getBlockEntityClass() {
        return BlockEntityGravestone.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntityGravestone.ID;
    }
}

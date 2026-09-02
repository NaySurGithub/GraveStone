package io.github.naysurgithub.gravestone;

import io.github.naysurgithub.gravestone.block.BlockGravestone;
import io.github.naysurgithub.gravestone.blockentity.BlockEntityGravestone;
import io.github.naysurgithub.gravestone.command.GraveStoneCommand;
import io.github.naysurgithub.gravestone.config.GraveStoneConfig;
import io.github.naysurgithub.gravestone.grave.GraveManager;
import io.github.naysurgithub.gravestone.listener.DeathListener;
import io.github.naysurgithub.gravestone.listener.GraveListener;
import org.powernukkitx.command.Command;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.plugin.PluginBase;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;

public final class GraveStonePlugin extends PluginBase {

    private static GraveStonePlugin instance;

    private GraveStoneConfig settings;
    private GraveManager graveManager;
    private GraveStoneCommand commandHandler;

    public static GraveStonePlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        this.settings = new GraveStoneConfig(getConfig());

        // Registered in onLoad so the block entity class is known before any
        // world chunk containing a grave is loaded.
        try {
            Registries.BLOCKENTITY.register(BlockEntityGravestone.ID, BlockEntityGravestone.class);
            Registries.BLOCK.registerCustomBlock(this, BlockGravestone.class);
        } catch (RegisterException e) {
            getLogger().error("Failed to register the gravestone block", e);
        }
    }

    @Override
    public void onEnable() {
        this.graveManager = new GraveManager(this);
        this.commandHandler = new GraveStoneCommand(this);

        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GraveListener(this), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gravestone")) {
            return commandHandler.handle(sender, args);
        }
        return false;
    }

    public void reloadSettings() {
        reloadConfig();
        this.settings = new GraveStoneConfig(getConfig());
    }

    public GraveStoneConfig getSettings() {
        return settings;
    }

    public GraveManager getGraveManager() {
        return graveManager;
    }
}

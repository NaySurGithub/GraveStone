package io.github.naysurgithub.gravestone.command;

import io.github.naysurgithub.gravestone.GraveStonePlugin;
import org.powernukkitx.command.CommandSender;

public final class GraveStoneCommand {

    private final GraveStonePlugin plugin;

    public GraveStoneCommand(GraveStonePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadSettings();
            sender.sendMessage("§aGraveStone configuration reloaded.");
            sender.sendMessage("§7Note: gravestone-hardness changes need a server restart.");
            return true;
        }
        sender.sendMessage("§7Usage: /gravestone reload");
        return true;
    }
}

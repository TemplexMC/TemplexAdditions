package net.ddns.templex.commands;

import io.github.trulyfree.va.command.commands.TabbableCommand;
import net.ddns.templex.TemplexAdditionsPlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class RefreshCommand extends TabbableCommand {

    private final BaseComponent[] SUCCESS = new ComponentBuilder("Refreshed successfully.").color(ChatColor.GREEN).create();

    private final TemplexAdditionsPlugin plugin;

    public RefreshCommand(TemplexAdditionsPlugin plugin) {
        super("refresh", "op");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        plugin.getProxy().getPluginManager().unregisterCommands(plugin);
        plugin.registerCommands();
        commandSender.sendMessage(SUCCESS);
    }
}

package net.ddns.templex.commands;

import io.github.trulyfree.va.command.commands.TabbableCommand;
import io.github.trulyfree.va.daemon.Daemon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.Collections;

public class DaemonizeCommand extends TabbableCommand {

    private final BaseComponent[] NO_COMMAND = new ComponentBuilder("Must submit a command to execute!").color(ChatColor.RED).create();

    public DaemonizeCommand() {
        super("daemonize", "op", "d");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(NO_COMMAND);
            return;
        }
        StringBuilder commandBuilder = new StringBuilder("/execute @s ~ ~ ~");
        for (String item : strings) {
            commandBuilder.append(' ');
            commandBuilder.append(item);
        }
        Daemon instance = Daemon.getInstanceNow();
        if (instance == null) {
            CommandUtil.daemonNotFound(commandSender);
            return;
        }
        instance.submitCommands(Collections.singletonList(commandBuilder.toString()));
    }
}

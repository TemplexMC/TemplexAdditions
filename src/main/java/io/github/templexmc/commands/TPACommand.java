package io.github.templexmc.commands;

import io.github.trulyfree.va.command.commands.TabbableCommand;
import io.github.trulyfree.va.daemon.Daemon;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TPACommand extends TabbableCommand {

    /**
     * The set of currently active requests. This map is threadsafe.
     */
    private final Map<ProxiedPlayer, ProxiedPlayer> requests;

    /**
     * The scheduler which manages request timeouts. Requests will timeout after 10 seconds.
     */
    private final ScheduledExecutorService requestManager;

    public TPACommand() {
        super("tpa");
        this.requests = new ConcurrentHashMap<>();
        requestManager = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void execute(final CommandSender commandSender, String[] strings) {
        if (commandSender.equals(ProxyServer.getInstance().getConsole())) {
            return;
        }
        ProxyServer proxy = ProxyServer.getInstance();
        final ProxiedPlayer enacter = proxy.getPlayer(commandSender.getName());
        if (strings.length == 0) {
            ProxiedPlayer target = null;
            for (Map.Entry<ProxiedPlayer, ProxiedPlayer> entry : requests.entrySet()) {
                if (entry.getValue().equals(enacter)) {
                    target = entry.getKey();
                    break;
                }
            }
            if (target == null) {
                commandSender.sendMessage(new ComponentBuilder("You didn't have any active requests!").color(ChatColor.RED).create());
            } else {
                requests.remove(target);
                try {
                    Daemon.getInstance().submitCommands(Collections.singletonList("/tp " + target.getName() + " " + enacter.getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (requests.containsKey(enacter)) {
            commandSender.sendMessage(new ComponentBuilder("You already have an active request.").color(ChatColor.RED).create());
        } else {
            ProxiedPlayer requested = proxy.getPlayer(strings[0]);
            if (requested == null) {
                commandSender.sendMessage(new ComponentBuilder("Didn't recognize the name " + strings[0] + ".").color(ChatColor.RED).create());
            } else {
                commandSender.sendMessage(new ComponentBuilder("Request sent to " + strings[0]).create());
                requested.sendMessage(new ComponentBuilder(commandSender.getName() + " requested a TP to you! Type /tpa to accept.").color(ChatColor.GREEN).create());
                requests.put(enacter, requested);
                requestManager.schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (requests.remove(enacter) != null) {
                            commandSender.sendMessage(new ComponentBuilder("Request timed out.").color(ChatColor.RED).create());
                        }
                    }
                }, 10, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void handleTabCompleteEvent(TabCompleteEvent event) {
        Util.pushAutocompletePlayers(event);
    }
}
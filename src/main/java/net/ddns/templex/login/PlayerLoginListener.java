package net.ddns.templex.login;

import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import net.ddns.templex.TemplexAdditionsPlugin;
import net.ddns.templex.player.config.BannedIPs;
import net.ddns.templex.player.config.OPs;
import net.ddns.templex.player.config.Specials;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;

@RequiredArgsConstructor
public class PlayerLoginListener implements Listener {

    private final TemplexAdditionsPlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        try {
            BannedIPs bannedIPs = plugin.getConfigHandler().getConfig("banned-ips.json", BannedIPs.class);
            for (BannedIPs.BannedIPsEntry entry : bannedIPs) {
                if (event.getConnection().getAddress().getAddress().equals(InetAddresses.forString(entry.getIp()))) {
                    event.setCancelled(true);
                    event.setCancelReason(entry.getReason());
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            event.setCancelled(true);
            event.setCancelReason("Could not determine IP validity.");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPostLoginEvent(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        plugin.getBackgroundExecutor().submit(new Runnable() {
            public void run() {
                plugin.getTeamHandler().assignTeam(player);
                establishOp(player);
                establishSpecial(player);
                player.setPermission("nonop", true);
            }
        });
    }

    private void establishOp(ProxiedPlayer player) {
        try {
            OPs ops = plugin.getConfigHandler().getConfig("ops.json", OPs.class);
            player.setPermission("op", false);
            for (OPs.OPsEntry entry : ops) {
                if (player.getName().equals(entry.getName())) {
                    player.setPermission("op", true);
                    plugin.getLogger().info(String.format("Marked %s as an operator.", player.getName()));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void establishSpecial(ProxiedPlayer player) {
        try {
            Specials specials = plugin.getConfigHandler().getConfig("special.json", Specials.class);
            player.setPermission("special", false);
            for (Specials.SpecialsEntry entry : specials) {
                if (player.getName().equals(entry.getName())) {
                    player.setPermission("special", true);
                    plugin.getLogger().info(String.format("Marked %s as a special player.", player.getName()));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
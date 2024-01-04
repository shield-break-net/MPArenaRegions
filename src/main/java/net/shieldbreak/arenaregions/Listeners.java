package net.shieldbreak.arenaregions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listeners implements Listener {

    private Location targetArea = new Location(Bukkit.getWorld("world"), 0, 64, 0); // Set area cnter coordinates
    private double radius = 10.0; // set radius area

    private Map<UUID, Boolean> playerInsideArea = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        Location to = event.getTo();

        boolean wasInside = playerInsideArea.getOrDefault(playerId, false);
        boolean isInsideNow = isWithinArea(to);

        if (wasInside && !isInsideNow) {
            event.getPlayer().sendMessage("§aYou left the Arena");
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("visitors");
            event.getPlayer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        } else if (!wasInside && isInsideNow) {
            event.getPlayer().sendMessage("§aYou entered the Arena");
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("arena");
            event.getPlayer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        }
        playerInsideArea.put(playerId, isInsideNow);
    }

    private boolean isWithinArea(Location location) {
        return location.getWorld().equals(targetArea.getWorld())
                && location.distanceSquared(targetArea) <= radius * radius;
    }
}
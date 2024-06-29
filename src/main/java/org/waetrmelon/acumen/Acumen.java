package org.waetrmelon.acumen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Acumen extends JavaPlugin implements Listener {

    public static HashMap<Player, Float> PreviousSliperiness = new HashMap<>();
    public static HashMap<Player, Float> CurrentSliperiness = new HashMap<>();
    public static HashMap<Player, Float> PreviousSpeed = new HashMap<>();
    public static HashMap<Player, Float> CurrentSpeed = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this,this);
        for (Player player : Bukkit.getOnlinePlayers()){
            PreviousSpeed.put(player, 0f);
            PreviousSliperiness.put(player, MovementSimulation.CalculateSlipperinessMultiplier(player));
            CurrentSpeed.put(player, 0f);
            CurrentSliperiness.put(player, MovementSimulation.CalculateSlipperinessMultiplier(player));
        }
    }

    @EventHandler
    public static void PlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PreviousSpeed.put(player, 0f);
        CurrentSpeed.put(player, 0f);
        PreviousSliperiness.put(player, MovementSimulation.CalculateSlipperinessMultiplier(player));
        CurrentSliperiness.put(player, MovementSimulation.CalculateSlipperinessMultiplier(player));
    }

    @EventHandler
    public static void  PlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().distance(event.getTo()) == 0){
            CurrentSpeed.put(player, 0f);
        }

        PreviousSpeed.put(player, CurrentSpeed.get(player));
        PreviousSliperiness.put(player, CurrentSliperiness.get(player));

        CurrentSpeed.put(player, MovementSimulation.CalculateGroundVelocity(player));
        CurrentSliperiness.put(player, MovementSimulation.CalculateSlipperinessMultiplier(player));

        MovementSimulation.SimulateMovement(player);
    }
}

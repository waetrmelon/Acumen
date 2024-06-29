package org.waetrmelon.acumen;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;

public class MovementSimulation {


//    Acceleration is added to the player's velocity.
//    The player is moved (new position = position + velocity).
//    The player's velocity is reduced to simulate drag.


    public static float GetMovementStateMultiplier(Player player) {
        if (player.isSprinting()) { return 1.3f; }
        else if (!player.isSprinting()) { return 1f; }
        else if (player.isSneaking()) { return 0.3f; }

        return 0f;
    }

    public static float CalculateMovementMultiplier (Player player) {
        float MovementStateMultiplier = GetMovementStateMultiplier(player);
        float MovementDirectionMultiplier = 0.98f;
        // TODO: Add 45 Degree Strafe and 45 Degree Sprint
        return MovementStateMultiplier * MovementDirectionMultiplier;
    }


    public static float CalculateEffectMultiplier(Player player) {
        float Speed = 0;
        float Slowness = 0;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)){
                Speed = effect.getAmplifier() + 1f;
            }
            else if (effect.getType().equals(PotionEffectType.SLOW)){
                Slowness = effect.getAmplifier() + 1f;
            }
        }
        float SpeedMultiplier = (float) (1 + (0.2 * Speed));
        float SlownessMultiplier = (float) (1 - (0.15 * Slowness));
        return SpeedMultiplier * SlownessMultiplier;
    }


    public static float CalculateSlipperinessMultiplier(Player player){
        Material block = player.getLocation().subtract(0, 1, 0).getBlock().getType();
        switch (block){
            case SLIME_BLOCK:
                return 0.8f;
            case ICE:
                return 0.98f;
            case PACKED_ICE:
                return 0.98f;
            case AIR:
                return 1f;
            default:
                return 0.6f;
        }

    }

    public static float CalculateGroundVelocity(Player player){
        float Mt = GetMovementStateMultiplier(player);
        float Et = CalculateEffectMultiplier(player);
        float St = Acumen.CurrentSliperiness.get(player);
        float pSt = Acumen.PreviousSliperiness.get(player);
        float pV = Acumen.PreviousSpeed.get(player);
        float Dt = (player.getLocation().getYaw() - 90) % 360; // Direction
        float vHt = (float) ((float) ((pV * pSt * 0.91) + (0.1 * Mt * Math.pow((0.6/St),3))) * Math.sin(Dt));
        return vHt;
    }

    public static void SimulateMovement(Player player) {
        float Mt = GetMovementStateMultiplier(player);
        float Et = CalculateEffectMultiplier(player);
        float St = CalculateSlipperinessMultiplier(player);

        //for (int i = 0; i <= 20; i++) { Bukkit.broadcastMessage(""); }
        //Bukkit.broadcastMessage("Mt: " + Mt);
        //Bukkit.broadcastMessage("Et: " + Et);
        //Bukkit.broadcastMessage("St: " + St);
        float CurrentSpeed = Acumen.CurrentSpeed.get(player);
        DecimalFormat df = new DecimalFormat("#.0000");
        String value = df.format(CurrentSpeed);
        //Bukkit.broadcastMessage("Estimated " + value);
        //Bukkit.broadcastMessage("Real: " + player.getVelocity().getX());
        float Difference = (float) Math.abs(player.getVelocity().getX() - CurrentSpeed);
        //Bukkit.broadcastMessage("% Difference: " + Difference);

        if (Difference > 0.2) {
            Bukkit.broadcastMessage("FLAG!!!!!!");
        }

    }



}

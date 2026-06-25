package com.tee_six.autoattack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Auto Attack Mod - Automatically times perfect attacks when holding down the attack button.
 *
 * When the attack key is held and the crosshair is aimed at a living entity,
 * this mod will automatically send attack packets at the optimal moment
 * (when the attack cooldown is fully charged).
 *
 * Original by Tee_Six, ported to Minecraft 26.2.
 */
public class AutoAttack implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoAttack::onTick);
    }

    private static void onTick(Minecraft client) {
        // Check if attack key is held down
        if (!client.options.keyAttack.isDown()) return;

        // Check if player exists
        LocalPlayer player = client.player;
        if (player == null) return;

        // Check if attack cooldown is fully charged (1.0 = fully charged)
        if (player.getAttackStrengthScale(0.0f) < 1.0f) return;

        // Check if player is still in the world
        if (player.isRemoved()) return;

        // Check if crosshair is targeting an entity
        HitResult hitResult = client.hitResult;
        if (hitResult == null) return;
        if (hitResult.getType() != HitResult.Type.ENTITY) return;

        // Get the targeted entity
        Entity target = ((EntityHitResult) hitResult).getEntity();

        // Only attack living entities
        if (!target.isAlive()) return;
        if (!target.isAttackable()) return;

        // Send attack packet and swing arm
        client.gameMode.attack(player, target);
        player.swing(InteractionHand.MAIN_HAND);
    }
}

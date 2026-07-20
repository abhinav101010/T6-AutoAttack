package com.tee_six.autoattack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MaceItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auto Attack mod - automatically attacks entities when holding down attack button.
 * Includes mace cooldown bypass for instant re-attacks with mace.
 */
public class AutoAttack implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("autoattack");

    /** Tick counter for mace delay */
    private static int maceTickCounter = 0;

    @Override
    public void onInitializeClient() {
        AutoAttackConfig.load();
        LOGGER.info("AutoAttack initialized (enabled={})", AutoAttackConfig.INSTANCE.enabled);
        ClientTickEvents.END_CLIENT_TICK.register(AutoAttack::onEndTick);
    }

    private static void onEndTick(MinecraftClient client) {
        // Check if mod is enabled
        if (!AutoAttackConfig.INSTANCE.enabled) return;

        // Check if attack key is held
        if (!client.options.attackKey.isPressed()) return;

        // Check if player exists
        if (client.player == null) return;

        // Check if player is not blocking (shield)
        if (client.player.isBlocking()) return;

        // Check cooldown
        boolean isMace = isHoldingMace(client.player);
        if (!isMace || !AutoAttackConfig.INSTANCE.maceContinuousSpam) {
            // Non-mace OR mace spam disabled → respect vanilla cooldown
            if (client.player.getAttackCooldownProgress(0.0f) < 1.0f) return;
            maceTickCounter = 0;
        } else {
            // Mace + spam enabled → apply tick delay
            int delay = AutoAttackConfig.INSTANCE.maceTickDelay;
            if (delay > 0) {
                maceTickCounter++;
                if (maceTickCounter < delay) return;
                maceTickCounter = 0;
            }
            // delay == 0 means spam every tick
        }

        // Check crosshair target
        if (client.crosshairTarget == null) return;
        if (client.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        // Get the entity from crosshair target
        Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();

        // Check if entity can be attacked and is alive
        if (!entity.isAttackable()) return;
        if (!entity.isAlive()) return;

        // Perform the attack
        client.interactionManager.attackEntity(client.player, entity);
        client.player.swingHand(Hand.MAIN_HAND);
    }

    private static boolean isHoldingMace(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof MaceItem
            || player.getOffHandStack().getItem() instanceof MaceItem;
    }
}

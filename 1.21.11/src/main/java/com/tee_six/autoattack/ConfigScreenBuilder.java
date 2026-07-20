package com.tee_six.autoattack;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Builds the Cloth Config screen for Auto Attack settings.
 */
public class ConfigScreenBuilder {

    /**
     * Create the config screen.
     *
     * @param parent the parent screen (mod list or game menu)
     * @return the config screen
     */
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("autoattack.config.title"))
            .setSavingRunnable(() -> {
                // Called when the user clicks "Save"
                AutoAttackConfig.save();
            });

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("autoattack.config.category.general"));
        ConfigEntryBuilder entry = builder.entryBuilder();

        // Enable/Disable toggle
        general.addEntry(entry.startBooleanToggle(
                Text.translatable("autoattack.config.enabled"),
                AutoAttackConfig.INSTANCE.enabled
            )
            .setDefaultValue(true)
            .setTooltip(Text.translatable("autoattack.config.enabled.tooltip"))
            .setSaveConsumer(newValue -> AutoAttackConfig.INSTANCE.enabled = newValue)
            .build()
        );

        // Mace continuous spam toggle
        general.addEntry(entry.startBooleanToggle(
                Text.translatable("autoattack.config.maceContinuousSpam"),
                AutoAttackConfig.INSTANCE.maceContinuousSpam
            )
            .setDefaultValue(true)
            .setTooltip(Text.translatable("autoattack.config.maceContinuousSpam.tooltip"))
            .setSaveConsumer(newValue -> AutoAttackConfig.INSTANCE.maceContinuousSpam = newValue)
            .build()
        );

        // Mace tick delay slider (only matters when spam is enabled)
        general.addEntry(entry.startIntSlider(
                Text.translatable("autoattack.config.maceTickDelay"),
                AutoAttackConfig.INSTANCE.maceTickDelay,
                0, 20
            )
            .setDefaultValue(0)
            .setTooltip(Text.translatable("autoattack.config.maceTickDelay.tooltip"))
            .setSaveConsumer(newValue -> AutoAttackConfig.INSTANCE.maceTickDelay = newValue)
            .build()
        );

        return builder.build();
    }
}

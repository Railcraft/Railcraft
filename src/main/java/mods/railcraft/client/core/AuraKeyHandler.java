/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.core;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class AuraKeyHandler {
    public static final AuraKeyHandler INSTANCE = new AuraKeyHandler();
    private static boolean anchorAuraEnabled = false;
    private static boolean tuningAuraEnabled = false;
    private static boolean surveyingAuraEnabled = false;
    private final KeyBinding anchorAura = new KeyBinding("railcraft.keybind.aura.anchor", Keyboard.KEY_F9, Railcraft.MOD_ID);
    private final KeyBinding tuningAura = new KeyBinding("railcraft.keybind.aura.tuning", Keyboard.KEY_P, Railcraft.MOD_ID);
    private final KeyBinding surveyingAura = new KeyBinding("railcraft.keybind.aura.surveying", Keyboard.KEY_O, Railcraft.MOD_ID);

    private AuraKeyHandler() {
        ClientRegistry.registerKeyBinding(anchorAura);
        ClientRegistry.registerKeyBinding(tuningAura);
    }

    public static boolean isAnchorAuraEnabled() {
        return anchorAuraEnabled;
    }

    public static boolean isTuningAuraEnabled() {
        return tuningAuraEnabled;
    }

    public static boolean isSurveyingAuraEnabled() {
        return surveyingAuraEnabled;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat)
            return;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (anchorAura.isPressed()) {
            anchorAuraEnabled = !anchorAuraEnabled;
            if (anchorAuraEnabled) {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.anchor");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.enable", "\u00A75" + aura + "\u00A77");
            } else {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.anchor");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.disable", "\u00A75" + aura + "\u00A77");
            }
        }
        if (tuningAura.isPressed()) {
            tuningAuraEnabled = !tuningAuraEnabled;
            if (tuningAuraEnabled) {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.tuning");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.enable", "\u00A75" + aura + "\u00A77");
            } else {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.tuning");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.disable", "\u00A75" + aura + "\u00A77");
            }
        }
        if (surveyingAura.isPressed()) {
            surveyingAuraEnabled = !surveyingAuraEnabled;
            if (surveyingAuraEnabled) {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.surveying");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.enable", "\u00A75" + aura + "\u00A77");
            } else {
                String aura = LocalizationPlugin.translate("railcraft.gui.goggles.aura.surveying");
                ChatPlugin.sendLocalizedChat(player, "railcraft.gui.aura.disable", "\u00A75" + aura + "\u00A77");
            }
        }
    }
}

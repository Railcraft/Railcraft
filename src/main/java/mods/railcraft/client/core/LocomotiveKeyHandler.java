/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.core;

import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.PacketKeyPress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class LocomotiveKeyHandler {

    public static final LocomotiveKeyHandler INSTANCE = new LocomotiveKeyHandler();
    private final KeyBinding reverseKey = new KeyBinding("keybind.railcraft.loco.reverse", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, Keyboard.KEY_SLASH, Railcraft.NAME);
    private final KeyBinding fasterKey = new KeyBinding("keybind.railcraft.loco.faster", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, Keyboard.KEY_PERIOD, Railcraft.NAME);
    private final KeyBinding slowerKey = new KeyBinding("keybind.railcraft.loco.slower", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, Keyboard.KEY_COMMA, Railcraft.NAME);
    private final KeyBinding modeChange = new KeyBinding("keybind.railcraft.loco.mode", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, Keyboard.KEY_M, Railcraft.NAME);
    private final KeyBinding whistle = new KeyBinding("keybind.railcraft.loco.whistle", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, Keyboard.KEY_N, Railcraft.NAME);

    private LocomotiveKeyHandler() {
        ClientRegistry.registerKeyBinding(reverseKey);
        ClientRegistry.registerKeyBinding(fasterKey);
        ClientRegistry.registerKeyBinding(slowerKey);
        ClientRegistry.registerKeyBinding(modeChange);
        ClientRegistry.registerKeyBinding(whistle);
    }

    @SubscribeEvent
    public void tick(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null)
            return;
        if (!(player.getRidingEntity() instanceof EntityMinecart))
            return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat)
            return;

        if (reverseKey.isPressed())
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.LOCOMOTIVE_REVERSE);
        if (fasterKey.isPressed())
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.LOCOMOTIVE_INCREASE_SPEED);
        if (slowerKey.isPressed())
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.LOCOMOTIVE_DECREASE_SPEED);
        if (modeChange.isPressed())
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.LOCOMOTIVE_MODE_CHANGE);
        if (whistle.isPressed())
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.LOCOMOTIVE_WHISTLE);

    }

}

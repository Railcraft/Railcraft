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
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.PacketKeyPress;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityMinecart;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class LocomotiveKeyHandler {

    public static final LocomotiveKeyHandler INSTANCE = new LocomotiveKeyHandler();
    private final KeyBinding fasterKey = new KeyBinding("railcraft.keybind.loco.faster", Keyboard.KEY_PERIOD, Railcraft.MOD_ID);
    private final KeyBinding slowerKey = new KeyBinding("railcraft.keybind.loco.slower", Keyboard.KEY_COMMA, Railcraft.MOD_ID);
    private final KeyBinding modeChange = new KeyBinding("railcraft.keybind.loco.mode", Keyboard.KEY_M, Railcraft.MOD_ID);
    private final KeyBinding whistle = new KeyBinding("railcraft.keybind.loco.whistle", Keyboard.KEY_N, Railcraft.MOD_ID);

    private LocomotiveKeyHandler() {
        ClientRegistry.registerKeyBinding(fasterKey);
        ClientRegistry.registerKeyBinding(slowerKey);
        ClientRegistry.registerKeyBinding(modeChange);
        ClientRegistry.registerKeyBinding(whistle);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return;
        if (!(player.ridingEntity instanceof EntityMinecart))
            return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat)
            return;

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

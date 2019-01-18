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
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * A key handler for sleeping on bed carts.
 */
@SideOnly(Side.CLIENT)
public final class SleepKeyHandler {

    public static final SleepKeyHandler INSTANCE = new SleepKeyHandler();

    private final KeyBinding sleepKey = new KeyBinding("keybind.railcraft.cart.bed", KeyConflictContext.IN_GAME, Keyboard.KEY_DOWN, Railcraft.NAME);

    private SleepKeyHandler() {
    }

    public void init() {
        ClientRegistry.registerKeyBinding(sleepKey);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().world == null)
            return;
        if (sleepKey.isPressed()) {
            PacketBuilder.instance().sendKeyPressPacket(PacketKeyPress.EnumKeyBinding.BED_CART_SLEEP);
        }
    }

    public String getKeyDisplayName() {
        return sleepKey.getDisplayName();
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.signals.DualLamp;
import mods.railcraft.api.signals.SignalAspect;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IDualHeadSignal {

    EnumWayObject getSignalType();

    EnumFacing getFacing();

    SignalAspect getSignalAspect(DualLamp lamp);

    @SideOnly(Side.CLIENT)
    TextureAtlasSprite getLampTexture(DualLamp lamp, SignalAspect aspect);
}

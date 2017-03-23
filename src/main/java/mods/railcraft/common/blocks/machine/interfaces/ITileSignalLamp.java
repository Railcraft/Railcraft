/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.interfaces;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.client.render.tools.RenderTools;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 6/5/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileSignalLamp {

    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getLampTexture(SignalAspect aspect) {
        return RenderTools.getMissingTexture();
    }

}

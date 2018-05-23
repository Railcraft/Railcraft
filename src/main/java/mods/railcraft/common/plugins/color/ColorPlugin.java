/*******************************************************************************
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.plugins.color;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 7/5/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ColorPlugin {
    @SidedProxy(modId = Railcraft.MOD_ID, clientSide = "mods.railcraft.common.plugins.color.ColorProxyClient", serverSide = "mods.railcraft.common.plugins.color.ColorProxy")
    public static ColorProxy instance;

    public interface IColoredItem {
        @SideOnly(Side.CLIENT)
        IItemColor colorHandler();
    }

    public interface IColoredBlock {
        @SideOnly(Side.CLIENT)
        IBlockColor colorHandler();
    }
}

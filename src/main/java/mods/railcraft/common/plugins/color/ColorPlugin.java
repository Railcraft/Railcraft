/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.color;

import mods.railcraft.common.core.Railcraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/5/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class ColorPlugin {

    public static final int NONE_MULTIPLIER = -1;

    @SidedProxy(modId = Railcraft.MOD_ID, clientSide = "mods.railcraft.common.plugins.color.ColorProxyClient", serverSide = "mods.railcraft.common.plugins.color.ColorProxy")
    public static ColorProxy instance;

    @FunctionalInterface
    public interface IColorHandlerItem {
        IColorFunctionItem colorHandler();
    }

    @FunctionalInterface
    public interface IColorHandlerBlock {
        IColorFunctionBlock colorHandler();
    }

    @FunctionalInterface
    public interface IColorFunctionItem {
        int getColor(ItemStack stack, int tintIndex);
    }

    @FunctionalInterface
    public interface IColorFunctionBlock {
        int getColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex);
    }
}

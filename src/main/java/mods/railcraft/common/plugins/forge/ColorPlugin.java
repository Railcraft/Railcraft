/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/

package mods.railcraft.common.plugins.forge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 7/5/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ColorPlugin {
    @SidedProxy(clientSide = "mods.railcraft.common.plugins.forge.ColorPlugin.ClientColorProxy", serverSide = "mods.railcraft.common.plugins.forge.ColorPlugin.ColorProxy")
    public static ColorProxy instance;

    public static class ColorProxy {
        public void register(Item item, IColoredItem colorHandler) {

        }

        public void register(Block item, IColoredBlock colorHandler) {

        }
    }

    @SuppressWarnings("unused")
    public static class ClientColorProxy extends ColorProxy {
        @Override
        public void register(Item item, IColoredItem colorHandler) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorHandler.colorHandler(), item);
        }

        @Override
        public void register(Block block, IColoredBlock colorHandler) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(colorHandler.colorHandler(), block);
        }
    }

    public interface IColoredItem {
        @SideOnly(Side.CLIENT)
        IItemColor colorHandler();
    }

    public interface IColoredBlock {
        @SideOnly(Side.CLIENT)
        IBlockColor colorHandler();
    }
}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.anvil;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRCAnvil extends BlockAnvil {

    private static final String[] anvilIconNames = new String[]{"anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2"};
    private static Block block;

    public BlockRCAnvil() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5.0F);
        setStepSound(Block.soundTypeAnvil);
        setResistance(2000.0F);
    }

    public static Block getBlock() {
        return block;
    }

    public static void registerBlock() {
        if (block == null) {
            String tag = "railcraft.anvil";
            if (RailcraftConfig.isBlockEnabled(tag)) {
                block = new BlockRCAnvil().setUnlocalizedName(tag);
                RailcraftRegistry.register(block, ItemAnvilBlock.class);

                ForestryPlugin.addBackpackItem("builder", block);

                HarvestPlugin.setBlockHarvestLevel("pickaxe", 2, block);
            }
        }
    }

    public static ItemStack getStack() {
        return new ItemStack(block);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        else {
            GuiHandler.openGui(EnumGui.ANVIL, playerIn, worldIn, pos);
            return true;
        }
    }

}

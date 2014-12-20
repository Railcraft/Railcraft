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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRCAnvil extends BlockAnvil {

    private static final String[] anvilIconNames = new String[]{"anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2"};
    private static Block block;

    public static Block getBlock() {
        return block;
    }

    public static void registerBlock() {
        if (block == null) {
            String tag = "railcraft.anvil";
            if (RailcraftConfig.isBlockEnabled(tag)) {
                block = new BlockRCAnvil().setBlockName(tag);
                RailcraftRegistry.register(block, ItemAnvilBlock.class);

                ForestryPlugin.addBackpackItem("builder", block);

                HarvestPlugin.setHarvestLevel(block, "pickaxe", 2);
            }
        }
    }

    public static ItemStack getStack() {
        return new ItemStack(block);
    }

    private IIcon[] iconArray;

    public BlockRCAnvil() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5.0F);
        setStepSound(Block.soundTypeAnvil);
        setResistance(2000.0F);
    }

    @Override
    public IIcon getIcon(int par1, int par2) {
        if (this.anvilRenderSide == 3 && par1 == 1) {
            int k = (par2 >> 2) % this.iconArray.length;
            return this.iconArray[k];
        } else
            return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("railcraft:anvil_base");
        this.iconArray = new IIcon[anvilIconNames.length];

        for (int i = 0; i < this.iconArray.length; ++i) {
            this.iconArray[i] = iconRegister.registerIcon("railcraft:" + anvilIconNames[i]);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        else {
            GuiHandler.openGui(EnumGui.ANVIL, player, world, x, y, z);
            return true;
        }
    }

}

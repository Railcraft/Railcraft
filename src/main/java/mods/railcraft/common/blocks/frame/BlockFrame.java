/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.frame;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.delta.TileWire;
import mods.railcraft.common.blocks.machine.delta.TileWire.AddonType;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFrame extends Block implements IPostConnection {

    private static BlockFrame instance;

    public static BlockFrame getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("frame")) {
                instance = new BlockFrame(Railcraft.proxy.getRenderId());
                RailcraftRegistry.register(instance, ItemBlockRailcraft.class);

//                HarvestPlugin.setStateHarvestLevel(instance, "crowbar", 0);
                HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, instance);

                ForestryPlugin.addBackpackItem("builder", instance);

                CraftingPlugin.addShapedRecipe(getItem(6),
                        "PPP",
                        "I I",
                        "III",
                        'P', RailcraftItem.plate, EnumPlate.IRON,
                        'I', RailcraftItem.rebar);
            }
    }

    public static ItemStack getItem() {
        return getItem(1);
    }

    public static ItemStack getItem(int qty) {
        if (instance == null) return null;
        return new ItemStack(instance, qty, 0);
    }

    private final int renderId;
    public static boolean flipTextures;
    public static boolean poweredTexture;

    public BlockFrame(int renderId) {
        super(Material.glass);
        this.renderId = renderId;
        setResistance(10);
        setHardness(5);
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setUnlocalizedName("railcraft.frame");
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return flipTextures || super.shouldSideBeRendered(worldIn, pos, side);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == UP;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack current = playerIn.getCurrentEquippedItem();
        if (current != null && InvTools.isItemEqualIgnoreNBT(current, EnumMachineDelta.WIRE.getItem()))
            if (WorldPlugin.setBlockState(worldIn, pos, EnumMachineDelta.WIRE.getState(), 2)) {
                TileEntity tile = WorldPlugin.getBlockTile(worldIn, pos);
                if (tile instanceof TileWire) {
                    TileWire wire = (TileWire) tile;
                    wire.setAddon(AddonType.FRAME);
                }
                if (!playerIn.capabilities.isCreativeMode)
                    playerIn.setCurrentItemOrArmor(0, InvTools.depleteItem(current));
                return true;
            }
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing face) {
        return ConnectStyle.TWO_THIN;
    }

}

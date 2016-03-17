/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class BlockDetector extends BlockContainer {

    private static BlockDetector block;

    public static void registerBlock() {
        if (block == null && RailcraftConfig.isBlockEnabled("detector")) {
            block = new BlockDetector();
            RailcraftRegistry.register(block, ItemDetector.class);

//            HarvestPlugin.setHarvestLevel(block, "pickaxe", 2);
            HarvestPlugin.setHarvestLevel(block, "crowbar", 0);

            for (EnumDetector d : EnumDetector.VALUES) {
                ItemStack stack = new ItemStack(block, 1, d.ordinal());
                RailcraftRegistry.register(stack);
            }
        }
    }

    public static BlockDetector getBlock() {
        return block;
    }

    public BlockDetector() {
        super(Material.rock);

        setBlockName("railcraft.detector");
        setResistance(4.5F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);

        GameRegistry.registerTileEntity(TileDetector.class, "RCDetectorTile");
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            return detector.getDetector().getType().getItem();
        }
        return super.getPickBlock(target, world, x, y, z, player);
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        return true;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int md, int fortune) {
        TileEntity tile = world.getTileEntity(i, j, k);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (tile instanceof TileDetector)
            items.add(((TileDetector) tile).getDetector().getType().getItem());
        return items;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector)
            ((TileDetector) tile).getDetector().onBlockRemoved();
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, x, y, z, 0, 0);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World var1, int meta) {
        return new TileDetector();
    }

    // Determine direction here
    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileDetector) {
            ((TileDetector) tile).direction = MiscTools.getSideClosestToPlayer(world, i, j, k, entityliving);
            ((TileDetector) tile).onBlockPlacedBy(entityliving, stack);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float u1, float u2, float u3) {
        if (player.isSneaking())
            return false;
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null) {
            Item item = current.getItem();
            if (item instanceof IActivationBlockingItem)
                return false;
            else if (TrackTools.isRailItem(item))
                return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector)
            return ((TileDetector) tile).blockActivated(player);
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            detector.onNeighborBlockChange(block);
        }
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector) {
            TileDetector detector = (TileDetector) tile;
            if (detector.direction == axis)
                detector.direction = axis.getOpposite();
            else
                detector.direction = axis;
            world.markBlockForUpdate(x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
        return ForgeDirection.VALID_DIRECTIONS;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        for (EnumDetector det : EnumDetector.VALUES) {
            String name = "railcraft:" + MiscTools.cleanTag(det.getTag());
            det.textures = TextureAtlasSheet.unstitchIcons(iconRegister, name, 3);
        }
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector) {
            TileDetector detectorTile = (TileDetector) tile;
            EnumDetector det = detectorTile.getDetector().getType();
            if (detectorTile.direction.ordinal() == side) {
                if (detectorTile.powerState != PowerPlugin.NO_POWER)
                    return det.textures[2];
                return det.textures[1];
            }
            return det.textures[0];
        }
        return null;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        EnumDetector det = EnumDetector.fromOrdinal(meta);
        if (side == 3)
            return det.textures[2];
        return det.textures[0];
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileDetector)
            return ((TileDetector) tile).getDetector().getHardness();
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the
     * specified side. If isBlockNormalCube returns true, standard redstone
     * propagation rules will apply instead and this will not be called. Args:
     * World, X, Y, Z, side. Note that the side is reversed - eg it is 1 (up)
     * when checking the bottom of the block.
     */
    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity t = world.getTileEntity(x, y, z);
        if (t instanceof TileDetector) {
            TileDetector tile = (TileDetector) t;
            if (tile.direction == MiscTools.getOppositeSide(side))
                return tile.powerState;
        }
        return PowerPlugin.NO_POWER;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the
     * specified side. Args: World, X, Y, Z, side. Note that the side is
     * reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return isProvidingWeakPower(world, x, y, z, side);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        super.onBlockAdded(world, i, j, k);
        world.markBlockForUpdate(i, j, k);
        if (Game.isNotHost(world))
            return;
        world.notifyBlocksOfNeighborChange(i + 1, j, k, this);
        world.notifyBlocksOfNeighborChange(i - 1, j, k, this);
        world.notifyBlocksOfNeighborChange(i, j, k + 1, this);
        world.notifyBlocksOfNeighborChange(i, j, k - 1, this);
        world.notifyBlocksOfNeighborChange(i, j - 1, k, this);
        world.notifyBlocksOfNeighborChange(i, j + 1, k, this);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        super.breakBlock(world, x, y, z, this, metadata);
        if (Game.isNotHost(world))
            return;
        world.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        world.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        world.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        world.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
        TileEntity t = world.getTileEntity(i, j, k);
        if (t instanceof TileDetector) {
            TileDetector tile = (TileDetector) t;
            if (dir == 1 && tile.direction.ordinal() == 5)
                return true;
            if (dir == 3 && tile.direction.ordinal() == 4)
                return true;
            if (dir == 2 && tile.direction.ordinal() == 3)
                return true;
            if (dir == 0 && tile.direction.ordinal() == 2)
                return true;
        }
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumDetector detector : EnumDetector.VALUES) {
            if (detector.isEnabled())
                list.add(detector.getItem());
        }
    }

}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.core.items.ITrackItem;
import mods.railcraft.api.tracks.ITrackCustomPlaced;
import mods.railcraft.api.tracks.ITrackInstance;
import mods.railcraft.api.tracks.TrackRegistry;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class ItemTrack extends ItemBlock implements ITrackItem {

    public ItemTrack(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.track");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return getIconIndex(stack);
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        TrackSpec trackSpec = getTrackSpec(stack);
        if (trackSpec == null)
            return Blocks.rail.getIcon(0, 0);
        return trackSpec.getItemIcon();
    }

    public TrackSpec getTrackSpec(ItemStack stack) {
        if (stack != null && stack.getItem() == this) {
            NBTTagCompound nbt = InvTools.getItemData(stack);
            if (nbt.hasKey("track"))
                return TrackRegistry.getTrackSpec(nbt.getString("track"));
            return TrackRegistry.getTrackSpec(-1);
        }
        return null;
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
    }

    @Override
    public int getMetadata(int i) {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + getTrackSpec(stack).getTrackTag().replace(':', '.');
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        try {
            TrackSpec spec = getTrackSpec(stack);
            List<String> tips = spec.getItemToolTip();
            if (tips != null)
                list.addAll(tips);
        } catch (Throwable error) {
            Game.logErrorAPI("Railcraft", error, TrackSpec.class);
        }
    }

    @Override
    public Block getPlacedBlock() {
        return RailcraftBlocks.getBlockTrack();
    }

    @Override
    public boolean isPlacedTileEntity(ItemStack stack, TileEntity tile) {
        if (tile instanceof TileTrack) {
            TileTrack track = (TileTrack) tile;
            if (track.getTrackInstance().getTrackSpec() == getTrackSpec(stack))
                return true;
        }
        return false;
    }

    @Override
    public boolean placeTrack(ItemStack stack, World world, int i, int j, int k) {
        return placeTrack(stack, world, i, j, k, 1);
    }

    private boolean placeTrack(ItemStack stack, World world, int i, int j, int k, int side) {
        Block blockTrack = RailcraftBlocks.getBlockTrack();
        if (blockTrack == null)
            return false;
        if (j >= world.getHeight() - 1)
            return false;
        if (stack == null || !(stack.getItem() instanceof ItemTrack))
            return false;
        TrackSpec spec = getTrackSpec(stack);
        ITrackInstance track = spec.createInstanceFromSpec();
        boolean canPlace = world.canPlaceEntityOnSide(blockTrack, i, j, k, true, side, null, stack);
        if (track instanceof ITrackCustomPlaced)
            canPlace &= ((ITrackCustomPlaced) track).canPlaceRailAt(world, i, j, k);
        else
            canPlace &= world.isSideSolid(i, j - 1, k, ForgeDirection.UP);
        if (canPlace) {
            boolean placed = world.setBlock(i, j, k, blockTrack);
            // System.out.println("Block placement attempted");
            if (placed) {
                if (world.getBlock(i, j, k) == blockTrack) {
                    TileTrack tile = TrackFactory.makeTrackTile(track);
                    world.setTileEntity(i, j, k, tile);
                    blockTrack.onPostBlockPlaced(world, i, j, k, 0);
                    world.markBlockForUpdate(i, j, k);
                }
                world.playSoundEffect((float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F, blockTrack.stepSound.getStepResourcePath(), (blockTrack.stepSound.getVolume() + 1.0F) / 2.0F, blockTrack.stepSound.getPitch() * 0.8F);
            }
            return true;
        } else
            return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
        if (stack.stackSize <= 0)
            return false;

        Block block = world.getBlock(x, y, z);
        if (block == Blocks.snow_layer)
            side = 1;
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
            if (side == 0)
                y--;
            if (side == 1)
                y++;
            if (side == 2)
                z--;
            if (side == 3)
                z++;
            if (side == 4)
                x--;
            if (side == 5)
                x++;
        }

        if (player != null && !player.canPlayerEdit(x, y, z, side, stack))
            return false;

        boolean success = placeTrack(stack, world, x, y, z, side);
        if (success) {
            Block blockTrack = RailcraftBlocks.getBlockTrack();
            if (player != null)
                blockTrack.onBlockPlacedBy(world, x, y, z, player, stack);
            stack.stackSize--;
        }
        return success;
    }

}

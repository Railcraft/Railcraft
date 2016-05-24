/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 * <p>
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.items;

import ic2.api.item.IBoxable;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.api.signals.SignalBlock;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalBlockSurveyor extends ItemRailcraft implements IBoxable, IActivationBlockingItem {

    public ItemSignalBlockSurveyor() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.signalBlockSurveyor, 1, 1, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                " C ",
                "BGB",
                " R ",
                'G', "paneGlassColorless",
                'C', Items.compass,
                'B', Blocks.stone_button,
                'R', "dustRedstone");
    }

    //TODO: Add name highlighting
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
//        System.out.println("click");
        if (Game.isHost(worldIn) && stack.hasTagCompound() && playerIn.isSneaking()) {
            WorldCoordinate signalPos = getSignalData(stack);
            if (signalPos != null) {
                TileEntity tile = DimensionManager.getWorld(signalPos.getDim()).getTileEntity(signalPos);
                if (tile instanceof ISignalBlockTile) {
                    ((ISignalBlockTile) tile).getSignalBlock().endPairing();
                }
            }
            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.abandon");
            stack.setTagCompound(null);
            return false;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null)
            if (tile instanceof ISignalBlockTile) {
//            System.out.println("target found");
                if (Game.isHost(worldIn)) {
                    ISignalBlockTile signalTile = (ISignalBlockTile) tile;
                    SignalBlock signalBlock = signalTile.getSignalBlock();
                    WorldCoordinate signalPos = getSignalData(stack);
                    SignalBlock.Status trackStatus = signalBlock.getTrackStatus();
                    if (trackStatus == SignalBlock.Status.INVALID)
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.track", signalTile.getDisplayName());
                    else if (signalPos == null) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.begin");
                        setSignalData(stack, tile);
                        signalBlock.startPairing();
                    } else if (!pos.equals(signalPos)) {
//                System.out.println("attempt pairing");
                        tile = worldIn.getTileEntity(signalPos);
                        if (tile != null && tile instanceof ISignalBlockTile) {
                            ISignalBlockTile otherTile = (ISignalBlockTile) tile;
                            SignalBlock otherSignal = otherTile.getSignalBlock();
                            if (signalBlock.createSignalBlock(otherSignal)) {
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.success");
                                stack.setTagCompound(null);
                            } else
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.invalid");
                        } else if (WorldPlugin.isBlockLoaded(worldIn, signalPos)) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.lost");
                            signalBlock.endPairing();
                            stack.setTagCompound(null);
                        } else
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.unloaded");
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.abandon");
                        signalBlock.endPairing();
                        stack.setTagCompound(null);
                    }
                }
                return true;
            } else if (Game.isHost(worldIn))
                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.wrong");
        return false;
    }

    private WorldCoordinate getSignalData(ItemStack item) {
        WorldCoordinate pos = null;
        NBTTagCompound data = item.getTagCompound();
        if (data != null) {
            int sDim = data.getInteger("signalDim");
            int sx = data.getInteger("signalX");
            int sy = data.getInteger("signalY");
            int sz = data.getInteger("signalZ");
            pos = new WorldCoordinate(sDim, sx, sy, sz);
        }
        return pos;
    }

    private void setSignalData(ItemStack item, TileEntity tile) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("signalDim", tile.getWorld().provider.getDimensionId());
        data.setInteger("signalX", tile.getPos().getX());
        data.setInteger("signalY", tile.getPos().getY());
        data.setInteger("signalZ", tile.getPos().getZ());
        item.setTagCompound(data);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import ic2.api.item.IBoxable;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.api.signals.SignalBlock;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalBlockSurveyor extends ItemRailcraft implements IBoxable, IActivationBlockingItem {

    private static Item item;

    private ItemSignalBlockSurveyor() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.surveyor";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemSignalBlockSurveyor().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item),
                        " C ",
                        "BGB",
                        " R ",
                        'G', "paneGlassColorless",
                        'C', Items.compass,
                        'B', Blocks.stone_button,
                        'R', "dustRedstone");

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, "tool.surveyor");
            }
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
//        System.out.println("click");
        if (Game.isHost(worldIn) && item.hasTagCompound() && playerIn.isSneaking()) {
            WorldCoordinate signalPos = getSignalData(item);
            if (signalPos != null) {
                TileEntity tile = DimensionManager.getWorld(signalPos.dimension).getTileEntity(signalPos.x, signalPos.y, signalPos.z);
                if (tile instanceof ISignalBlockTile) {
                    ((ISignalBlockTile) tile).getSignalBlock().endPairing();
                }
            }
            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.abandon");
            item.setTagCompound(null);
            return false;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null)
            if (tile instanceof ISignalBlockTile) {
//            System.out.println("target found");
                if (Game.isHost(worldIn)) {
                    ISignalBlockTile signalTile = (ISignalBlockTile) tile;
                    SignalBlock signalBlock = signalTile.getSignalBlock();
                    WorldCoordinate signalPos = getSignalData(item);
                    WorldCoordinate track = signalBlock.getTrackLocation();
                    if (track == null)
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.track", signalTile.getLocalizationTag());
                    else if (signalPos == null) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.begin");
                        setSignalData(item, tile);
                        signalBlock.startPairing();
                    } else if (pos.getX() != signalPos.x || pos.getY() != signalPos.y || pos.getZ() != signalPos.z) {
//                System.out.println("attempt pairing");
                        tile = worldIn.getTileEntity(signalPos.x, signalPos.y, signalPos.z);
                        if (tile != null && tile instanceof ISignalBlockTile) {
                            ISignalBlockTile otherTile = (ISignalBlockTile) tile;
                            SignalBlock otherSignal = otherTile.getSignalBlock();
                            if (signalBlock.createSignalBlock(otherSignal)) {
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.success");
                                item.setTagCompound(null);
                            } else
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.invalid");
                        } else if (worldIn.blockExists(signalPos.x, signalPos.y, signalPos.z)) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.lost");
                            signalBlock.endPairing();
                            item.setTagCompound(null);
                        } else
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.unloaded");
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.surveyor.abandon");
                        signalBlock.endPairing();
                        item.setTagCompound(null);
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

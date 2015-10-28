/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import cpw.mods.fml.common.Optional;
import ic2.api.item.IBoxable;
import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalTuner extends ItemRailcraft implements IBoxable, IActivationBlockingItem {
    private static Item item;

    private ItemSignalTuner() {
        super();
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.tool.signal.tuner";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemSignalTuner().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item),
                        " T ",
                        "BRB",
                        "   ",
                        'B', Blocks.stone_button,
                        'R', RailcraftItem.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                        'T', Blocks.redstone_torch);

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, "tool.signal.tuner");
            }
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int i, int j, int k, int side, float par8, float par9, float par10) {
        if (Game.isHost(world) && item.hasTagCompound() && player.isSneaking()) {
            WorldCoordinate cPos = getControllerData(item);
            if (cPos != null) {
                TileEntity tile = DimensionManager.getWorld(cPos.dimension).getTileEntity(cPos.x, cPos.y, cPos.z);
                if (tile instanceof IControllerTile) {
                    ((IControllerTile) tile).getController().endPairing();
                }
            }
            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.abandon.player");
            item.setTagCompound(null);
            return false;
        }
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile != null) {
            WorldCoordinate cPos = getControllerData(item);
            if (tile instanceof IReceiverTile && cPos != null) {
                if (Game.isHost(world)) {
                    SignalReceiver receiver = ((IReceiverTile) tile).getReceiver();
                    if (i != cPos.x || j != cPos.y || k != cPos.z) {
                        tile = world.getTileEntity(cPos.x, cPos.y, cPos.z);
                        if (tile != null && tile instanceof IControllerTile) {
                            SignalController controller = ((IControllerTile) tile).getController();
                            if (receiver.getTile() != controller.getTile()) {
                                controller.registerReceiver(receiver);
                                controller.endPairing();
                                ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.success", controller.getLocalizationTag(), receiver.getLocalizationTag());
                                item.setTagCompound(null);
                                return true;
                            }
                        } else if (world.blockExists(cPos.x, cPos.y, cPos.z)) {
                            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.abandon.gone");
                            item.setTagCompound(null);
                        } else {
                            ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.abandon.chunk");
                            item.setTagCompound(null);
                        }
                    }
                }
            } else if (tile instanceof IControllerTile) {
                if (Game.isHost(world)) {
                    SignalController controller = ((IControllerTile) tile).getController();
                    if (cPos == null || (i != cPos.x || j != cPos.y || k != cPos.z)) {
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.start", controller.getLocalizationTag());
                        setControllerData(item, tile);
                        controller.startPairing();
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(player, "railcraft.gui.tuner.stop", controller.getLocalizationTag());
                        controller.endPairing();
                        item.setTagCompound(null);
                    }
                }
            } else
                return false;
            return true;
        }
        return false;
    }

    private WorldCoordinate getControllerData(ItemStack item) {
        WorldCoordinate cPos = null;
        NBTTagCompound data = item.getTagCompound();
        if (data != null) {
            int cDim = data.getInteger("controllerDim");
            int cx = data.getInteger("controllerX");
            int cy = data.getInteger("controllerY");
            int cz = data.getInteger("controllerZ");
            cPos = new WorldCoordinate(cDim, cx, cy, cz);
        }
        return cPos;
    }

    private void setControllerData(ItemStack item, TileEntity tile) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("controllerDim", tile.getWorldObj().provider.dimensionId);
        data.setInteger("controllerX", tile.xCoord);
        data.setInteger("controllerY", tile.yCoord);
        data.setInteger("controllerZ", tile.zCoord);
        item.setTagCompound(data);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

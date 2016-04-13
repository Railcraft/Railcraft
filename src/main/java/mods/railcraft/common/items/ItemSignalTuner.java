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
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.SignalController;
import mods.railcraft.api.signals.SignalReceiver;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "ic2.api.item.IBoxable", modid = "IC2")
public class ItemSignalTuner extends ItemRailcraft implements IBoxable, IActivationBlockingItem {

    public ItemSignalTuner() {
        super();
        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);

        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.signalTuner, 1, 1, LootPlugin.Type.WORKSHOP);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                " T ",
                "BRB",
                "   ",
                'B', Blocks.stone_button,
                'R', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                'T', Blocks.redstone_torch);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (Game.isHost(worldIn) && stack.hasTagCompound() && playerIn.isSneaking()) {
            WorldCoordinate cPos = getControllerData(stack);
            if (cPos != null) {
                TileEntity tile = DimensionManager.getWorld(cPos.getDim()).getTileEntity(cPos);
                if (tile instanceof IControllerTile) {
                    ((IControllerTile) tile).getController().endPairing();
                }
            }
            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.abandon.player");
            stack.setTagCompound(null);
            return false;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null) {
            WorldCoordinate cPos = getControllerData(stack);
            if (tile instanceof IReceiverTile && cPos != null) {
                if (Game.isHost(worldIn)) {
                    SignalReceiver receiver = ((IReceiverTile) tile).getReceiver();
                    if (!pos.equals(cPos)) {
                        tile = worldIn.getTileEntity(cPos);
                        if (tile != null && tile instanceof IControllerTile) {
                            SignalController controller = ((IControllerTile) tile).getController();
                            if (receiver.getTile() != controller.getTile()) {
                                controller.registerReceiver(receiver);
                                controller.endPairing();
                                ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.success", controller.getLocalizationTag(), receiver.getLocalizationTag());
                                stack.setTagCompound(null);
                                return true;
                            }
                        } else if (WorldPlugin.isBlockLoaded(worldIn, cPos)) {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.abandon.gone");
                            stack.setTagCompound(null);
                        } else {
                            ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.abandon.chunk");
                            stack.setTagCompound(null);
                        }
                    }
                }
            } else if (tile instanceof IControllerTile) {
                if (Game.isHost(worldIn)) {
                    SignalController controller = ((IControllerTile) tile).getController();
                    if (cPos == null || !pos.equals(cPos)) {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.start", controller.getLocalizationTag());
                        setControllerData(stack, tile);
                        controller.startPairing();
                    } else {
                        ChatPlugin.sendLocalizedChatFromServer(playerIn, "railcraft.gui.tuner.stop", controller.getLocalizationTag());
                        controller.endPairing();
                        stack.setTagCompound(null);
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
        data.setInteger("controllerDim", tile.getWorld().provider.getDimensionId());
        data.setInteger("controllerX", tile.getPos().getX());
        data.setInteger("controllerY", tile.getPos().getY());
        data.setInteger("controllerZ", tile.getPos().getZ());
        item.setTagCompound(data);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.blocks.TileSmartItemTicking;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.emblems.EmblemToolsServer;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@net.minecraftforge.fml.common.Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public class TileEngravingBench extends TileSmartItemTicking implements ISidedInventory, IHasWork, IGuiReturnHandler, ITileRotate {
    //TODO: Finish conversion to RCUs
    public enum GuiPacketType {

        START_CRAFTING, NORMAL_RETURN, OPEN_UNLOCK, OPEN_NORMAL, UNLOCK_EMBLEM
    }

    private static final int PROCESS_TIME = 100;
    private static final int ACTIVATION_POWER = 50;
    private static final int MAX_RECEIVE = 1000;
    private static final int MAX_ENERGY = ACTIVATION_POWER * (PROCESS_TIME + (PROCESS_TIME / 2));
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_RESULT = 1;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private final InventoryMapper invResult = new InventoryMapper(this, SLOT_RESULT, 1).ignoreItemChecks();
    //    public final FEEnergyIndicator rfIndicator;
    private int progress;
    public boolean paused, startCrafting, isCrafting, flippedAxis;
    public String currentEmblem = "";
    private final Set<Object> actions = new HashSet<>();

    public TileEngravingBench() {
        super(2);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("flippedAxis", flippedAxis);
        data.setBoolean("isCrafting", isCrafting);
        data.setInteger("progress", progress);
        data.setString("currentEmblem", currentEmblem);

        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        flippedAxis = data.getBoolean("flippedAxis");
        isCrafting = data.getBoolean("isCrafting");
        progress = data.getInteger("progress");
        currentEmblem = data.getString("currentEmblem");

    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(flippedAxis);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        flippedAxis = data.readBoolean();
        markBlockForUpdate();
    }

    @Override
    public void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException {
        GuiPacketType type = GuiPacketType.values()[data.readByte()];
        switch (type) {
            case START_CRAFTING:
                startCrafting = true;
            case NORMAL_RETURN:
                currentEmblem = data.readUTF();
                break;
            case OPEN_UNLOCK:
                GuiHandler.openGui(EnumGui.ENGRAVING_BENCH_UNLOCK, sender, world, getPos());
                break;
            case OPEN_NORMAL:
                GuiHandler.openGui(EnumGui.ENGRAVING_BENCH, sender, world, getPos());
                break;
            case UNLOCK_EMBLEM:
                if (EmblemToolsServer.manager != null) {
                    int windowId = data.readByte();
                    String code = data.readUTF();
                    EmblemToolsServer.manager.unlockEmblem((EntityPlayerMP) sender, code, windowId);
                }
                break;
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        BlockPos offsetPos = getPos().add(0.5, 0.5, 0.5);
        if (player.getDistanceSq(offsetPos) > 64D)
            return false;
        GuiHandler.openGui(EnumGui.ENGRAVING_BENCH, player, world, getPos());
        return true;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressScaled(int i) {
        return (progress * i) / PROCESS_TIME;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world))
            return;

        if (clock % 16 == 0)
            processActions();

        if (paused)
            return;

        if (startCrafting) {
            startCrafting = false;
            isCrafting = true;
        }

        if (!getStackInSlot(SLOT_RESULT).isEmpty())
            isCrafting = false;

        if (!isCrafting) {
            progress = 0;
            return;
        }

        ItemStack emblem = makeEmblem();
        if (InvTools.isEmpty(emblem))
            return;

        if (!isItemValidForSlot(SLOT_INPUT, getStackInSlot(SLOT_INPUT))) {
            progress = 0;
            return;
        }

        // TODO charge usage

        if (progress >= PROCESS_TIME) {
            isCrafting = false;
            if (invResult.canFit(emblem)) {
                decrStackSize(SLOT_INPUT, 1);
                invResult.addStack(emblem);
                progress = 0;
            }
        } else
            progress++;
    }

    private @Nullable ItemStack makeEmblem() {
        if (currentEmblem == null || currentEmblem.isEmpty() || EmblemToolsServer.manager == null)
            return null;
        return EmblemToolsServer.manager.getEmblemItemStack(currentEmblem);
    }

    @Override
    public boolean hasWork() {
        return isCrafting;
    }

    public void setPaused(boolean p) {
        paused = p;
    }

    private void processActions() {
        paused = actions.stream().anyMatch(a -> a == Actions.PAUSE);
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        actions.add(action);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_RESULT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
        if (slot == SLOT_RESULT)
            return false;
        if (InvTools.isEmpty(stack))
            return false;
        if (OreDictPlugin.isOreType("ingotSteel", stack))
            return true;
        if (OreDictPlugin.isOreType("ingotBronze", stack))
            return true;
        if (OreDictPlugin.isOreType("ingotBrass", stack))
            return true;
        return Items.GOLD_INGOT == stack.getItem();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase entityLiving, ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        if (entityLiving != null) {
            EnumFacing facing = entityLiving.getHorizontalFacing();
            if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
                flippedAxis = true;
        }
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        flippedAxis = !flippedAxis;
        sendUpdateToClient();
        return true;
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.ENGRAVING_BENCH;
    }

    @Override
    public EnumFacing getFacing() {
        return flippedAxis ? EnumFacing.WEST : EnumFacing.NORTH;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        flippedAxis = facing.getAxis() == EnumFacing.Axis.X;
    }
}

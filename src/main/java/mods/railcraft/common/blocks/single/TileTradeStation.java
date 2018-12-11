/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileSmartItemTicking;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.chest.TradeStationLogic;
import mods.railcraft.common.util.entity.ai.EntityAISearchForBlock;
import mods.railcraft.common.util.entity.ai.EntityAIWatchBlock;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileTradeStation extends TileSmartItemTicking implements TradeStationLogic.IContainer, IGuiReturnHandler, ISidedInventory, ITileRotate {

    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);
    private final TradeStationLogic logic;
//    private static final int AREA = 6;
//    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);
//
//    private VillagerRegistry.VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
//    private final InventoryAdvanced recipeSlots = new InventoryAdvanced(9).callbackInv(this).phantom();
//    private final InventoryMapper invInput;
//    private final InventoryMapper invOutput;
//    protected EnumFacing direction = EnumFacing.NORTH;

    public TileTradeStation() {
        super(16);
        logic = new TradeStationLogic(getWorld(), this) {
            @Override
            public boolean openGui(EntityPlayer player) {
                GuiHandler.openGui(EnumGui.TRADE_STATION, player, getWorld(), getPos());
                return true;
            }

            @Override
            public void onLogicChanged() {
                TileTradeStation.this.sendUpdateToClient();
            }

            @Override
            public void sendUpdateToClient() {
                TileTradeStation.this.sendUpdateToClient();
            }

            @Override
            public double getX() {
                return getPos().getX() + 0.5;
            }

            @Override
            public double getY() {
                return getPos().getY() + 0.5;
            }

            @Override
            public double getZ() {
                return getPos().getZ() + 0.5;
            }

            @Override
            public BlockPos getPos() {
                return TileTradeStation.this.getPos();
            }

            @Override
            public String getName() {
                return TileTradeStation.this.getName();
            }

            @Override
            protected void modifyNearbyAI() {
                for (EntityVillager villager : findNearbyVillagers(20)) {
                    AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 4, 0.08F));
                    AIPlugin.addAITask(villager, 9, new EntityAISearchForBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 16, 0.002F));
                }
            }
        };
    }

    @Override
    public TradeStationLogic getLogic() {
        return logic;
    }

    public IInventory getRecipeSlots() {
        return logic.getRecipeSlots();
    }

    public VillagerRegistry.VillagerProfession getProfession() {
        return logic.getProfession();
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(world))
            return;

        if (clock % 256 == 0)
            logic.updateNearbyAI();

        logic.update();
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        logic.direction = placer == null ? EnumFacing.NORTH : MiscTools.getHorizontalSideFacingPlayer(placer);
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (logic.direction == axis)
            logic.direction = axis.getOpposite();
        else
            logic.direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        logic.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        logic.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        logic.readPacketData(data);
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        logic.readGuiData(data, sender);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing face) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
        return slot >= 10;
    }

    @Override
    public @Nullable EnumGui getGui() {
        return EnumGui.TRADE_STATION;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(BlockTradeStation.FACING, logic.direction);
    }

    @Override
    public EnumFacing getFacing() {
        return logic.direction;
    }

    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
        logic.setWorld(worldIn);
    }
}

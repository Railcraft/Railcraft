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
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.entity.ai.EntityAIMoveToBlock;
import mods.railcraft.common.util.entity.ai.EntityAIWatchBlock;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@SuppressWarnings("PointlessArithmeticExpression")
public class TileTradeStation extends TileSmartItemTicking implements IGuiReturnHandler, ISidedInventory, ITileRotate {
    public enum GuiPacketType {

        NEXT_TRADE, SET_PROFESSION
    }

    private static final int AREA = 6;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);

    private VillagerRegistry.VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
    private final InventoryAdvanced recipeSlots = new InventoryAdvanced(9).callbackInv(this).phantom();
    private final InventoryMapper invInput;
    private final InventoryMapper invOutput;
    protected EnumFacing direction = EnumFacing.NORTH;

    public TileTradeStation() {
        super(16);
        invInput = InventoryMapper.make(this, 0, 10);
        invOutput = new InventoryMapper(this, 10, 6).ignoreItemChecks();
    }

    public IInventory getRecipeSlots() {
        return recipeSlots;
    }

    public VillagerRegistry.VillagerProfession getProfession() {
        return profession;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.TRADE_STATION, player, world, getPos().getX(), getPos().getY(), getPos().getZ());
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (clock % 256 == 0)
            modifyNearbyAI();

        List<EntityVillager> villagers = findNearbyVillagers(AREA);
        attemptTrade(villagers, 0);
        attemptTrade(villagers, 1);
        attemptTrade(villagers, 2);
    }

    private void modifyNearbyAI() {
        for (EntityVillager villager : findNearbyVillagers(20)) {
            AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 4, 0.08F));
            AIPlugin.addAITask(villager, 9, new EntityAIMoveToBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 16, 0.002F));
        }
    }

    private List<EntityVillager> findNearbyVillagers(int range) {
        float x = getPos().getX();
        float y = getPos().getY();
        float z = getPos().getZ();
        AxisAlignedBB area = AABBFactory.start().setBounds(x, y - 1, z, x + 1, y + 3, z + 1).expandHorizontally(range).build();
        return EntitySearcher.find(EntityVillager.class).around(area).in(world);
    }

    private void attemptTrade(List<EntityVillager> villagers, int tradeSet) {
        ItemStack buy1 = recipeSlots.getStackInSlot(tradeSet * 3 + 0);
        ItemStack buy2 = recipeSlots.getStackInSlot(tradeSet * 3 + 1);
        ItemStack sell = recipeSlots.getStackInSlot(tradeSet * 3 + 2);
        for (EntityVillager villager : villagers) {
            MerchantRecipeList recipes = villager.getRecipes(null);
            for (MerchantRecipe recipe : recipes) {
                if (recipe.isRecipeDisabled())
                    continue;
                // TODO: There must be clearer way to write this!
                if (!InvTools.isEmpty(recipe.getItemToBuy()) && !InvTools.isItemLessThanOrEqualTo(recipe.getItemToBuy(), buy1))
                    continue;
                if (!InvTools.isEmpty(recipe.getSecondItemToBuy()) && !InvTools.isItemLessThanOrEqualTo(recipe.getSecondItemToBuy(), buy2))
                    continue;
                if (!InvTools.isItemGreaterOrEqualThan(recipe.getItemToSell(), sell))
                    continue;
//                System.out.printf("Buying: %d %s Found: %d%n", recipe.getItemToBuy().stackSize, recipe.getItemToBuy().getDisplayName(), InvTools.countItems(invInput, recipe.getItemToBuy()));
                if (canDoTrade(recipe)) {
//                    System.out.println("Can do trade");
                    doTrade(villager, recipe);
                    return;
                }
            }
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean canDoTrade(MerchantRecipe recipe) {
        if (!InvTools.isEmpty(recipe.getItemToBuy()) && invInput.countItems(recipe.getItemToBuy()) < sizeOf(recipe.getItemToBuy()))
            return false;
        if (!InvTools.isEmpty(recipe.getSecondItemToBuy()) && invInput.countItems(recipe.getSecondItemToBuy()) < sizeOf(recipe.getSecondItemToBuy()))
            return false;
        return invOutput.canFit(recipe.getItemToSell());
    }

    private void doTrade(IMerchant merchant, MerchantRecipe recipe) {
        merchant.useRecipe(recipe);
        if (!InvTools.isEmpty(recipe.getItemToBuy()))
            invInput.removeItems(sizeOf(recipe.getItemToBuy()), recipe.getItemToBuy());
        if (!InvTools.isEmpty(recipe.getSecondItemToBuy()))
            invInput.removeItems(sizeOf(recipe.getSecondItemToBuy()), recipe.getSecondItemToBuy());
        invOutput.addStack(recipe.getItemToSell().copy());
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        direction = placer == null ? EnumFacing.NORTH : MiscTools.getHorizontalSideFacingPlayer(placer);
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        recipeSlots.writeToNBT("recipe", data);

        data.setString("ProfessionName", profession.getRegistryName().toString());
        data.setByte("direction", (byte) direction.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        recipeSlots.readFromNBT("recipe", data);

        if (data.hasKey("ProfessionName")) {
            VillagerRegistry.VillagerProfession p =
                    ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.getString("ProfessionName")));
            if (p == null)
                p = VillagerRegistry.FARMER;
            profession = p;
        }
        direction = EnumFacing.byIndex(data.getByte("direction"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(profession.getRegistryName().toString());
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.readUTF()));
        EnumFacing f = EnumFacing.byIndex(data.readByte());
        if (direction != f) {
            direction = f;
            markBlockForUpdate();
        }
    }

    @Override
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        GuiPacketType type = GuiPacketType.values()[data.readByte()];
        switch (type) {
            case NEXT_TRADE:
                nextTrade(data.readByte());
                break;
            case SET_PROFESSION:
                profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.readUTF()));
                sendUpdateToClient();
                break;
        }
    }

    public void nextTrade(int tradeSet) {
        EntityVillager villager = new EntityVillager(world);
        villager.setProfession(profession);
        MerchantRecipeList recipes = villager.getRecipes(null);
        assert recipes != null;
        MerchantRecipe recipe = recipes.get(MiscTools.RANDOM.nextInt(recipes.size()));
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 0, recipe.getItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 1, recipe.getSecondItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 2, recipe.getItemToSell());
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
        return base.withProperty(BlockTradeStation.FACING, direction);
    }

    @Override
    public EnumFacing getFacing() {
        return direction;
    }

}

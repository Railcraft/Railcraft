/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.chest;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 *
 */
public abstract class TradeStationLogic extends InventoryLogic implements IEntityLogic.ILocatable, IEntityLogic.ISaveable, INetworkedObject<RailcraftInputStream, RailcraftOutputStream>, IGuiReturnHandler {
    public enum GuiPacketType {
        NEXT_TRADE, SET_PROFESSION
    }

    private static final int AREA = 6;

    private VillagerRegistry.VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:farmer"));
    private final InventoryAdvanced recipeSlots = new InventoryAdvanced(9).callbackInv(inventory).phantom();
    private final InventoryMapper invInput;
    private final InventoryMapper invOutput;
    public EnumFacing direction = EnumFacing.NORTH;

    protected TradeStationLogic(World world, IInventory inventory) {
        super(world, inventory);
        invInput = InventoryMapper.make(inventory, 0, 10);
        invOutput = new InventoryMapper(inventory, 10, 6).ignoreItemChecks();
    }

    public IInventory getRecipeSlots() {
        return recipeSlots;
    }

    public VillagerRegistry.VillagerProfession getProfession() {
        return profession;
    }

    public abstract boolean openGui(EntityPlayer player);

    public void updateNearbyAI() {
        modifyNearbyAI();
    }

    @Override
    public void update() {
        List<EntityVillager> villagers = findNearbyVillagers(AREA);
        attemptTrade(villagers, 0);
        attemptTrade(villagers, 1);
        attemptTrade(villagers, 2);
    }

    protected abstract void modifyNearbyAI();

    protected List<EntityVillager> findNearbyVillagers(int range) {
        double x = getX();
        double y = getY();
        double z = getZ();
        AABBFactory area = AABBFactory.start().setBounds(x, y - 1, z, x + 1, y + 3, z + 1).expandHorizontally(range);
        return EntitySearcher.find(EntityVillager.class).around(area).in(world);
    }

    private void attemptTrade(List<EntityVillager> villagers, int tradeSet) {
        ItemStack buy1 = recipeSlots.getStackInSlot(tradeSet * 3 + 0);
        ItemStack buy2 = recipeSlots.getStackInSlot(tradeSet * 3 + 1);
        ItemStack sell = recipeSlots.getStackInSlot(tradeSet * 3 + 2);
        for (EntityVillager villager : villagers) {
            MerchantRecipeList recipes = villager.getRecipes(RailcraftFakePlayer.get((WorldServer) world, getX(), getY(), getZ()));
            for (MerchantRecipe recipe : recipes) {
                if (recipe.isRecipeDisabled())
                    continue;
                // TODO: There must be clearer way to write this!
                ItemStack firstItem = recipe.getItemToBuy();
                ItemStack secondItem = recipe.getSecondItemToBuy();
                if (!InvTools.isEmpty(firstItem) && !InvTools.isItemLessThanOrEqualTo(firstItem, buy1))
                    continue;
                if (!InvTools.isEmpty(secondItem) && !InvTools.isItemLessThanOrEqualTo(secondItem, buy2))
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
        ItemStack firstItem = recipe.getItemToBuy();
        ItemStack secondItem = recipe.getSecondItemToBuy();

        if (InvTools.isItemEqual(firstItem, secondItem)) {
            if (invInput.countItems(firstItem) < sizeOf(firstItem) + sizeOf(secondItem))
                return false;
        } else {
            if (!InvTools.isEmpty(firstItem) && invInput.countItems(firstItem) < sizeOf(firstItem))
                return false;
            if (!InvTools.isEmpty(secondItem) && invInput.countItems(secondItem) < sizeOf(secondItem))
                return false;
        }
        return invOutput.canFit(recipe.getItemToSell());
    }

    private void doTrade(IMerchant merchant, MerchantRecipe recipe) {
        merchant.useRecipe(recipe);
        ItemStack firstItem = recipe.getItemToBuy();
        ItemStack secondItem = recipe.getSecondItemToBuy();
        if (!InvTools.isEmpty(firstItem))
            if (!invInput.removeItems(sizeOf(firstItem), firstItem))
                Game.log(Level.WARN, "Cannot remove first input item!");
        if (!InvTools.isEmpty(secondItem))
            if (!invInput.removeItems(sizeOf(secondItem), secondItem))
                Game.log(Level.WARN, "Cannot remove second input item!");
        invOutput.addStack(InvTools.copy(recipe.getItemToSell()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        recipeSlots.writeToNBT("recipe", data);

        data.setString("ProfessionName", profession.getRegistryName().toString());
        data.setByte("direction", (byte) direction.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
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
        data.writeUTF(profession.getRegistryName().toString());
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(data.readUTF()));
        EnumFacing f = EnumFacing.byIndex(data.readByte());
        if (direction != f) {
            direction = f;
            onLogicChanged();
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
        MerchantRecipeList recipes = villager.getRecipes(RailcraftFakePlayer.get((WorldServer) world, getX(), getY(), getZ()));
        assert recipes != null;
        MerchantRecipe recipe = recipes.get(MiscTools.RANDOM.nextInt(recipes.size()));
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 0, recipe.getItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 1, recipe.getSecondItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 2, recipe.getItemToSell());
    }

    /**
     * Called when the fields of the logic change.
     */
    public abstract void onLogicChanged();

    public abstract String getName();

    @Override
    public @Nullable World theWorld() {
        return getWorld();
    }

    public interface IContainer extends InventoryLogic.IContainer, ILocatable.IContainer, ISaveable.IContainer {
        @Override
        TradeStationLogic getLogic();
    }
}

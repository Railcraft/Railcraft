/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.VillagerPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

/**
 *
 */
public abstract class TradeStationLogic extends InventoryLogic {
    public enum GuiPacketType {
        NEXT_TRADE, SET_PROFESSION, SET_CAREER
    }

    private static final int AREA = 6;

    private VillagerRegistry.VillagerProfession profession = VillagerRegistry.FARMER;
    private VillagerRegistry.VillagerCareer career = profession.getCareer(0);
    private final InventoryAdvanced recipeSlots = new InventoryAdvanced(9).callbackInv(inventory).phantom();
    private final InventoryMapper invInput = InventoryMapper.make(inventory, 0, 10);
    private final InventoryMapper invOutput = InventoryMapper.make(inventory, 10, 6).ignoreItemChecks();
    private int xpCollected;

    protected TradeStationLogic(Adapter adapter) {
        super(adapter, 16);
        xpCollected = 0;
    }

    public IInventory getRecipeSlots() {
        return recipeSlots;
    }

    public VillagerRegistry.VillagerProfession getProfession() {
        return profession;
    }

    public VillagerRegistry.VillagerCareer getCareer() {
        return career;
    }

    public void setProfession(VillagerRegistry.VillagerProfession profession) {
        if (!Objects.equals(this.profession, profession)) {
            this.profession = profession;
            this.career = profession.getCareer(0);
            sendUpdateToClient();
        }
    }

    public void setCareer(VillagerRegistry.VillagerCareer career) {
        if (!Objects.equals(this.career, career)) {
            this.career = career;
            sendUpdateToClient();
        }
    }

    @Override
    public boolean interact(EntityPlayer player, EnumHand hand) {
        if (super.interact(player, hand))
            return true;
        player.addExperience(getXpCollected());
        clearXp();
        return true;
    }

    @Override
    public void updateServer() {
        List<EntityVillager> villagers = findNearbyVillagers(AREA);
        attemptTrade(villagers, 0);
        attemptTrade(villagers, 1);
        attemptTrade(villagers, 2);

        if (clock(256)) {
            modifyNearbyAI();
            absorbExperience();
        }
    }

    protected abstract void modifyNearbyAI();

    private void absorbExperience() {
        double x = getX();
        double y = getY();
        double z = getZ();
        AABBFactory area = AABBFactory.start().setBounds(x, y - 3, z, x + 1, y + 3, z + 1).expandHorizontally(10);
        List<EntityXPOrb> orbs = EntitySearcher.find(EntityXPOrb.class).around(area).in(theWorldAsserted());
        xpCollected += orbs.stream().mapToInt(EntityXPOrb::getXpValue).sum();
        for (EntityXPOrb orb : orbs) {
            SoundHelper.playSoundForEntity(orb, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            orb.setDead();
        }
    }

    protected List<EntityVillager> findNearbyVillagers(int range) {
        double x = getX();
        double y = getY();
        double z = getZ();
        AABBFactory area = AABBFactory.start().setBounds(x, y - 1, z, x + 1, y + 3, z + 1).expandHorizontally(range);
        return EntitySearcher.find(EntityVillager.class).and(v -> v.getProfessionForge() == getProfession() && VillagerPlugin.getCareer(v).equals(getCareer())).around(area).in(theWorldAsserted());
    }

    private void attemptTrade(List<EntityVillager> villagers, int tradeSet) {
        ItemStack buy1 = recipeSlots.getStackInSlot(tradeSet * 3);
        ItemStack buy2 = recipeSlots.getStackInSlot(tradeSet * 3 + 1);
        ItemStack sell = recipeSlots.getStackInSlot(tradeSet * 3 + 2);
        for (EntityVillager villager : villagers) {
            MerchantRecipeList recipes = villager.getRecipes(RailcraftFakePlayer.get((WorldServer) theWorldAsserted(), getX(), getY(), getZ()));
            if (recipes != null) {
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
    }

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
        EntityPlayer originalCustomer = merchant.getCustomer();
        merchant.setCustomer(getOwnerEntityOrFake());
        merchant.useRecipe(recipe);
        merchant.setCustomer(originalCustomer);
        ItemStack firstItem = recipe.getItemToBuy();
        ItemStack secondItem = recipe.getSecondItemToBuy();
        if (!InvTools.isEmpty(firstItem))
            if (!invInput.removeItems(sizeOf(firstItem), firstItem))
                Game.log().msg(Level.WARN, "Cannot remove first input item!");
        if (!InvTools.isEmpty(secondItem))
            if (!invInput.removeItems(sizeOf(secondItem), secondItem))
                Game.log().msg(Level.WARN, "Cannot remove second input item!");
        invOutput.addStack(InvTools.copy(recipe.getItemToSell()));
    }

    protected abstract EntityPlayer getOwnerEntityOrFake();

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        recipeSlots.writeToNBT("recipe", data);

        data.setString("ProfessionName", Objects.requireNonNull(profession.getRegistryName()).toString());
        data.setInteger("career", VillagerPlugin.getCareerId(career));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        recipeSlots.readFromNBT("recipe", data);

        if (data.hasKey("ProfessionName", Constants.NBT.TAG_STRING)) {
            setProfession(findProfession(data.getString("ProfessionName")));
        }

        if (data.hasKey("career", Constants.NBT.TAG_INT)) {
            setCareer(getProfession().getCareer(data.getInteger("career")));
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeUTF(Objects.requireNonNull(profession.getRegistryName()).toString());
        data.writeInt(VillagerPlugin.getCareerId(career));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        setProfession(findProfession(data.readUTF()));
        setCareer(getProfession().getCareer(data.readInt()));
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readGuiData(RailcraftInputStream data, @Nullable EntityPlayer sender) throws IOException {
        super.readGuiData(data, sender);
        GuiPacketType type = GuiPacketType.values()[data.readByte()];
        switch (type) {
            case NEXT_TRADE:
                nextTrade(data.readByte());
                break;
            case SET_PROFESSION:
                setProfession(findProfession(data.readUTF()));
                recipeSlots.clear();
                sendUpdateToClient();
                break;
            case SET_CAREER:
                setCareer(getProfession().getCareer(data.readInt()));
                recipeSlots.clear();
                sendUpdateToClient();
                break;
        }
    }

    private void nextTrade(int tradeSet) {
        List<EntityVillager> villagers = findNearbyVillagers(AREA);
        Collections.shuffle(villagers);
        EntityVillager villager;
        if (villagers.isEmpty()) {
            villager = new EntityVillager(theWorldAsserted());
        } else {
            villager = villagers.iterator().next();
        }
        MerchantRecipeList recipes = villager.getRecipes(RailcraftFakePlayer.get((WorldServer) theWorldAsserted(), getX(), getY(), getZ()));
        assert recipes != null;
        MerchantRecipe recipe = recipes.get(MiscTools.RANDOM.nextInt(recipes.size()));
        recipeSlots.setInventorySlotContents(tradeSet * 3, recipe.getItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 1, recipe.getSecondItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 2, recipe.getItemToSell());
    }

    private static VillagerRegistry.VillagerProfession findProfession(String location) {
        VillagerRegistry.VillagerProfession p = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(location));
        if (p == null)
            p = VillagerRegistry.FARMER;
        return p;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }

    @Override
    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return new InvWrapper(this) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 10) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    public int getXpCollected() {
        return xpCollected;
    }

    public void clearXp() {
        this.xpCollected = 0;
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return EnumGui.TRADE_STATION;
    }
}

/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneRefined extends ItemFirestone {

    public static final int CHARGES = 5000;
    private static final int HEAT = 250;

    protected int heat = HEAT;

    @Nullable
    public static ItemStack getItemCharged() {
        return RailcraftItems.FIRESTONE_REFINED.getStack();
    }

    @Nullable
    public static ItemStack getItemEmpty() {
        return RailcraftItems.FIRESTONE_REFINED.getStack(1, CHARGES - 1);
    }

    public ItemFirestoneRefined() {
        setMaxStackSize(1);
        setMaxDamage(CHARGES);
    }

    @Override
    public void defineRecipes() {
        ItemStack ore = EnumOreMagic.FIRESTONE.getStack();
        if (ore != null) {
            ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(ore, true, false);
            recipe.addOutput(RailcraftItems.FIRESTONE_RAW.getStack(), 1F);
        }

        CraftingPlugin.addRecipe(RailcraftItems.FIRESTONE_CUT.getStack(),
                " P ",
                "PFP",
                " P ",
                'P', Items.DIAMOND_PICKAXE,
                'F', RailcraftItems.FIRESTONE_RAW);

        for (ItemStack stack : FluidTools.getContainersFilledWith(Fluids.LAVA.get(FluidTools.BUCKET_VOLUME))) {
            CraftingPlugin.addRecipe(ItemFirestoneRefined.getItemEmpty(),
                    "LRL",
                    "RFR",
                    "LRL",
                    'R', "blockRedstone",
                    'L', stack,
                    'F', RailcraftItems.FIRESTONE_CUT);
            CraftingPlugin.addRecipe(ItemFirestoneRefined.getItemEmpty(),
                    "LOL",
                    "RFR",
                    "LRL",
                    'R', "blockRedstone",
                    'L', stack,
                    'O', RailcraftItems.FIRESTONE_RAW,
                    'F', RailcraftItems.FIRESTONE_CRACKED.getWildcard());
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, getMaxDamage()));
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack newStack;
        double damageLevel = (double) stack.getItemDamage() / (double) stack.getMaxDamage();
        if (MiscTools.RANDOM.nextDouble() < damageLevel * 0.0001) {
            newStack = ItemFirestoneCracked.getItemEmpty();
            if (stack.hasDisplayName())
                newStack.setStackDisplayName(stack.getDisplayName());
        } else
            newStack = stack.copy();
        newStack.stackSize = 1;
        newStack = InvTools.damageItem(newStack, 1);
        return newStack;
    }

    @Override
    public final int getHeatValue(ItemStack stack) {
        if (stack.getItemDamage() < getMaxDamage())
            return heat;
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        String tipTag = getUnlocalizedName() + ".tips.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getUnlocalizedName() + ".tips.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.canPlayerEdit(pos, side, stack)) {
            Block block = WorldPlugin.getBlock(world, pos);
            if (block != Blocks.STONE) {
                List<ItemStack> drops = block.getDrops(world, pos, WorldPlugin.getBlockState(world, pos), 0);
                if (drops.size() == 1 && drops.get(0) != null && drops.get(0).getItem() instanceof ItemBlock) {
                    ItemStack cooked = FurnaceRecipes.instance().getSmeltingResult(drops.get(0));
                    if (cooked != null && cooked.getItem() instanceof ItemBlock) {
                        IBlockState newState = InvTools.getBlockStateFromStack(cooked, world, pos);
                        if (newState != null) {
                            WorldPlugin.setBlockState(world, pos, newState);
                            SoundHelper.playSound(world, null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                            stack.damageItem(1, player);
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        pos = pos.offset(side);

        if (player.canPlayerEdit(pos, side, stack) && world.isAirBlock(pos)) {
            SoundHelper.playSound(world, null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            stack.damageItem(1, player);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (!target.isImmuneToFire()) {
            target.setFire(5);
            stack.damageItem(1, playerIn);
            SoundHelper.playSound(playerIn.worldObj, null, target.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            playerIn.swingArm(hand);
            BlockPos pos = new BlockPos(target);
            playerIn.worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState());
            return true;
        }
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world    The world object
     * @param location The EntityItem object, useful for getting the position of
     *                 the entity
     * @param stack    The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemFirestone entity = (EntityItemFirestone) super.createEntity(world, location, stack);
        entity.setRefined(true);
        return entity;
    }

    @Override
    public void finalizeDefinition() {
    }

}

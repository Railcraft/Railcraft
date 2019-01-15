/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.blocks.ore.EnumOreMagic;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneRefined extends ItemFirestone {

    public static final int CHARGES = 5000;
    public static final int HEAT = 250;

    protected int heat = HEAT;

    public static ItemStack getItemCharged() {
        return RailcraftItems.FIRESTONE_REFINED.getStack();
    }

    public static ItemStack getItemEmpty() {
        return RailcraftItems.FIRESTONE_REFINED.getStack(1, CHARGES - 1);
    }

    @SuppressWarnings("unused")
    // Seriously Intellij! It's used in the same place _all_ the other item constructors are!
    public ItemFirestoneRefined() {
        setMaxStackSize(1);
        setMaxDamage(CHARGES);
    }

    @Override
    public void defineRecipes() {
        ItemStack ore = EnumOreMagic.FIRESTONE.getStack();
        if (!ore.isEmpty()) {
            Crafters.rockCrusher().makeRecipe(ore).name("railcraft:firestone_ore")
                    .addOutput(RailcraftItems.FIRESTONE_RAW.getStack())
                    .register();
        }

        CraftingPlugin.addShapedRecipe(RailcraftItems.FIRESTONE_CUT.getStack(),
                " P ",
                "PFP",
                " P ",
                'P', Items.DIAMOND_PICKAXE,
                'F', RailcraftItems.FIRESTONE_RAW);

        FluidStack fluidStack = Fluids.LAVA.get(FluidTools.BUCKET_VOLUME);
        CraftingPlugin.addShapedRecipe(ItemFirestoneRefined.getItemEmpty(),
                "LRL",
                "RFR",
                "LRL",
                'R', "blockRedstone",
                'L', fluidStack,
                'F', RailcraftItems.FIRESTONE_CUT);

        CraftingPlugin.addShapedRecipe(ItemFirestoneRefined.getItemEmpty(),
                "LOL",
                "RFR",
                "LRL",
                'R', "blockRedstone",
                'L', fluidStack,
                'O', RailcraftItems.FIRESTONE_RAW,
                'F', Ingredients.consumingContainer(RailcraftItems.FIRESTONE_CRACKED.getWildcard()));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (!isInCreativeTab(tab))
            return;
        ItemStack noDamage = new ItemStack(this, 1, 0);
        list.add(noDamage);
        list.add(new ItemStack(this, 1, noDamage.getMaxDamage() - 1));
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
        setSize(newStack, 1);
        newStack = InvTools.damageItem(newStack, 1);
        return newStack;
    }

    @Override
    public final int getItemBurnTime(ItemStack stack) {
        if (stack.getItemDamage() < stack.getMaxDamage() || InvTools.isWildcard(stack))
            return heat;
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        String tipTag = getTranslationKey() + ".tips.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getTranslationKey() + ".tips.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        info.addAll(tip.convertToStrings());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.canPlayerEdit(pos, side, stack)) {
            Block block = WorldPlugin.getBlock(world, pos);
            if (block != Blocks.STONE) {
                NonNullList<ItemStack> drops = NonNullList.create();
                block.getDrops(drops, world, pos, WorldPlugin.getBlockState(world, pos), 0);
                if (drops.size() == 1 && !InvTools.isEmpty(drops.get(0)) && drops.get(0).getItem() instanceof ItemBlock) {
                    ItemStack cooked = FurnaceRecipes.instance().getSmeltingResult(drops.get(0));
                    if (cooked.getItem() instanceof ItemBlock) {
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
            SoundHelper.playSound(playerIn.world, null, target.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            playerIn.swingArm(hand);
            BlockPos pos = new BlockPos(target);
            playerIn.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
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
    public EntityItemFirestone createEntity(World world, Entity location, ItemStack stack) {
        EntityItemFirestone entity = super.createEntity(world, location, stack);
        Objects.requireNonNull(entity).setRefined(true);
        return entity;
    }
}

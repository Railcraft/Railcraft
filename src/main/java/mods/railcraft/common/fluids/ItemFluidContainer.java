/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.setSize;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemFluidContainer extends ItemRailcraft {

    protected final Fluids fluid;
    protected final Item empty;

    public ItemFluidContainer(Fluids fluid, Item empty) {
        this.fluid = fluid;
        this.empty = empty;
        CustomContainerHandler.INSTANCE.addContainer(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        RayTraceResult trace = rayTrace(world, player, false);

        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = trace.getBlockPos();

            if (!world.isBlockModifiable(player, pos))
                return new ActionResult<>(EnumActionResult.FAIL, stack);

            pos = pos.offset(trace.sideHit);

            if (!player.canPlayerEdit(pos, trace.sideHit, stack))
                return new ActionResult<>(EnumActionResult.FAIL, stack);

            if (tryPlaceContainedLiquid(world, pos) && !player.capabilities.isCreativeMode) {
                ItemStack empty = getContainerItem(stack);
                if (InvTools.isEmpty(empty)) {
                    empty = stack.copy();
                    setSize(empty, 0);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, empty);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private boolean tryPlaceContainedLiquid(World world, BlockPos pos) {
        Fluid fl = fluid.get();
        if (fl == null)
            return false;
        Block fluidBlock = fl.getBlock();
        if (fluidBlock == null)
            return false;

        Material material = WorldPlugin.getBlockMaterial(world, pos);

        if (!world.isAirBlock(pos) && material.isSolid())
            return false;

        if (!world.isRemote && !material.isSolid() && !material.isLiquid())
            world.destroyBlock(pos, true);

        world.setBlockState(pos, fluidBlock.getDefaultState().withProperty(BlockFluidBase.LEVEL, fluidBlock instanceof BlockFluidFinite ? 15 : 0));
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidContainerCapabilityDispatcher(this, stack);
    }

    @Nullable
    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(empty);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }
}

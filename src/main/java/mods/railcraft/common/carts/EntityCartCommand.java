/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Credits to CovertJaguar.
 *
 * @author liach
 */
public class EntityCartCommand extends EntityMinecartCommandBlock {

    public EntityCartCommand(World world) {
        super(world);
    }

    public EntityCartCommand(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        // Command block logic overrode this and the custom name is
        // somewhat set to the command block's name ("@")
        if (!getCustomNameTag().isEmpty()) {
            compound.setString("CustomName", getCustomNameTag());
        }
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        ItemStack stack = new ItemStack(Items.COMMAND_BLOCK_MINECART);
        if (hasCustomName())
            stack.setStackDisplayName(getName());
        return stack;
    }

    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (RailcraftConfig.doCartsBreakOnDrop()) {
            items.add(new ItemStack(Items.MINECART));
            items.add(new ItemStack(Blocks.COMMAND_BLOCK));
        } else
            items.add(getCartItem());
        return items;
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        setDead();
        List<ItemStack> drops = getItemsDropped();
        if (hasCustomName())
            drops.get(0).setStackDisplayName(getCustomNameTag());
        for (ItemStack item : drops) {
            entityDropItem(item, 0.0F);
        }
    }
}

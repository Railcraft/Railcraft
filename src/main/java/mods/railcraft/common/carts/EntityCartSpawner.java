/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

/**
 *
 */
public class EntityCartSpawner extends EntityMinecartMobSpawner implements IRailcraftCart {

    private static final DataParameter<NBTTagCompound> SPAWN_DATA = DataManagerPlugin.create(DataSerializers.COMPOUND_TAG);

    public EntityCartSpawner(World worldIn) {
        super(worldIn);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.SPAWNER;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        cartInit();

        dataManager.register(SPAWN_DATA, new NBTTagCompound());
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        saveToNBT(compound);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        loadFromNBT(compound);
    }

    /**
     * Returns true if it can be exploited for OP access.
     */
    @Override
    public boolean ignoreItemEntityData() {
        return true;
    }

    /**
     * Loads mob spawner info from the item.
     */
    @Override
    public void initEntityFromItem(ItemStack stack) {
        // Cannot load here as we do not know the owner and placer.
    }

    /**
     * Loads mob info from spawn egg in hand.
     */
    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() instanceof ItemMonsterPlacer) {
            player.swingArm(hand);
            if (Game.isClient(world)) {
                return EnumActionResult.SUCCESS;
            }

            mobSpawnerLogic.setEntityId(ItemMonsterPlacer.getNamedIdFrom(stack));
            sendToClient();
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    void sendToClient() {
        NBTTagCompound tag = mobSpawnerLogic.writeToNBT(new NBTTagCompound());
        tag.removeTag("SpawnPotentials");
        dataManager.set(SPAWN_DATA, tag);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (Game.isHost(world))
            return;
        if (Objects.equals(key, SPAWN_DATA)) {
            mobSpawnerLogic.readFromNBT(dataManager.get(SPAWN_DATA));
        }
    }

    public MobSpawnerBaseLogic getLogic() {
        return mobSpawnerLogic;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return CartTools.isInRangeToRenderDist(this, distance);
    }

    @Override
    public ItemStack getCartItem() {
        return createCartItem(this);
    }

    @Override
    public ItemStack createCartItem(EntityMinecart cart) {
        ItemStack stack = RailcraftCarts.SPAWNER.getStack();
        if (!InvTools.isEmpty(stack) && cart.hasCustomName())
            stack.setStackDisplayName(cart.getCustomNameTag());
        NBTTagCompound spawner = mobSpawnerLogic.writeToNBT(new NBTTagCompound());
        stack.getOrCreateSubCompound("Spawner").merge(spawner);
        return stack;
    }

    @Override
    public void killMinecart(DamageSource par1DamageSource) {
        killAndDrop(this);
    }

    @Override
    public ItemStack[] getItemsDropped(EntityMinecart cart) {
        ItemStack stack = new ItemStack(Items.MINECART);
        if (!InvTools.isEmpty(stack) && cart.hasCustomName())
            stack.setStackDisplayName(cart.getCustomNameTag());
        return new ItemStack[]{stack};
    }
}

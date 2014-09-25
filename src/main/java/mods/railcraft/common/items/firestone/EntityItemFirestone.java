/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import cpw.mods.fml.common.registry.EntityRegistry;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.EntityItemFireproof;
import mods.railcraft.common.util.misc.EntityIDs;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityItemFirestone extends EntityItemFireproof {

    public static void register() {
        EntityRegistry.registerModEntity(EntityItemFirestone.class, "ItemFirestone", EntityIDs.ENTITY_ITEM_FIRESTONE, Railcraft.getMod(), 64, 20, true);
    }

    public EntityItemFirestone(World world) {
        super(world);
    }

    public EntityItemFirestone(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemFirestone(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    @Override
    protected void setOnFireFromLava() {
        if (isDead)
            return;
        if (worldObj.isRemote)
            return;
        int xHit = MathHelper.floor_double(posX);
        int yHit = MathHelper.floor_double(posY);
        int zHit = MathHelper.floor_double(posZ);
        if (worldObj.getBlock(xHit, yHit, zHit).getMaterial() == Material.lava || worldObj.getBlock(xHit, yHit + 1, zHit).getMaterial() == Material.lava)
            for (int y = yHit + 1; y <= yHit + 10; y++) {
                if (worldObj.isAirBlock(xHit, y, zHit) && worldObj.getBlock(xHit, y - 1, zHit).getMaterial() == Material.lava) {
                    int meta = (getEntityItem().getItem() instanceof ItemFirestoneCracked) ? 1 : 0;
                    worldObj.setBlock(xHit, y, zHit, BlockFirestoneRecharge.getBlock(), meta, 3);
                    TileEntity tile = worldObj.getTileEntity(xHit, y, zHit);
                    if (tile instanceof TileFirestoneRecharge) {
                        TileFirestoneRecharge fireTile = (TileFirestoneRecharge) tile;
                        ItemStack firestone = getEntityItem();
                        fireTile.charge = firestone.getMaxDamage() - firestone.getItemDamage();
                        if (firestone.hasDisplayName())
                            fireTile.setItemName(firestone.getDisplayName());
                        setDead();
                        return;
                    }
                }
            }
    }

}

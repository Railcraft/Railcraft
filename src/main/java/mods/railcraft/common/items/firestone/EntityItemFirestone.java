/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items.firestone;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.EntityItemFireproof;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntityIDs;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityItemFirestone extends EntityItemFireproof {

    private int clock = MiscTools.RANDOM.nextInt(100);
    private boolean refined;

    public EntityItemFirestone(World world) {
        super(world);
    }

    public EntityItemFirestone(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemFirestone(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    public static void register() {
        EntityRegistry.registerModEntity(RailcraftConstantsAPI.locationOf("firestone"), EntityItemFirestone.class, "ItemFirestone", EntityIDs.ENTITY_ITEM_FIRESTONE, Railcraft.getMod(), 64, 20, true);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isHost(world)) {
            clock++;
            if (clock % 4 != 0)
                return;
            ItemStack stack = getItem();
            FirestoneTools.trySpawnFire(world, getPosition(), stack);
        }
    }

    @Override
    protected void setOnFireFromLava() {
        if (!refined || isDead || world.isRemote)
            return;
        IBlockState firestoneBlock = RailcraftBlocks.RITUAL.getDefaultState();
        if (firestoneBlock.getBlock() == Blocks.AIR)
            return;
        BlockPos surface = new BlockPos(posX, posY, posZ);
        if (WorldPlugin.getBlockMaterial(world, surface) == Material.LAVA || WorldPlugin.getBlockMaterial(world, surface.up()) == Material.LAVA)
            for (int i = 0; i < 10; i++) {
                surface = surface.up();
                if (WorldPlugin.isBlockAir(world, surface) && WorldPlugin.getBlockMaterial(world, surface.down()) == Material.LAVA) {
                    boolean cracked = getItem().getItem() instanceof ItemFirestoneCracked;
                    WorldPlugin.setBlockState(world, surface, firestoneBlock.withProperty(BlockRitual.CRACKED, cracked));
                    TileEntity tile = WorldPlugin.getBlockTile(world, surface);
                    if (tile instanceof TileRitual) {
                        TileRitual fireTile = (TileRitual) tile;
                        ItemStack firestone = getItem();
                        fireTile.charge = firestone.getMaxDamage() - firestone.getItemDamage();
                        if (firestone.hasDisplayName())
                            fireTile.setItemName(firestone.getDisplayName());
                        setDead();
                        return;
                    }
                }
            }
    }

    public boolean isRefined() {
        return refined;
    }

    public void setRefined(boolean refined) {
        this.refined = refined;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("refined", refined);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        refined = compound.getBoolean("refined");
        super.readFromNBT(compound);
    }
}

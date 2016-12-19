/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartAnchorWorld extends EntityCartAnchor {

    public EntityCartAnchorWorld(World world) {
        super(world);
    }

    public EntityCartAnchorWorld(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean doesCartMatchFilter(ItemStack stack, EntityMinecart cart) {
        return RailcraftCarts.getCartType(stack) == RailcraftCarts.ANCHOR_WORLD;
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), world, ForgeChunkManager.Type.ENTITY);
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.anchorFuelWorld;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return EnumMachineAlpha.ANCHOR_WORLD.getDefaultState().withProperty(TileAnchorWorld.DISABLED, !hasTicketFlag());
    }

}

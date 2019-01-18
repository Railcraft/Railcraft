/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWorldspikeStandard extends EntityCartWorldspike {

    public EntityCartWorldspikeStandard(World world) {
        super(world);
    }

    public EntityCartWorldspikeStandard(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.WORLDSPIKE_STANDARD;
    }

    @Override
    protected @Nullable ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestTicket(Railcraft.getMod(), world, ForgeChunkManager.Type.ENTITY);
    }

    @Override
    public Map<Ingredient, Float> getFuelMap() {
        return RailcraftConfig.worldspikeFuelStandard;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return WorldspikeVariant.STANDARD.getDefaultState().withProperty(BlockWorldspike.ENABLED, hasTicketFlag());
    }

}

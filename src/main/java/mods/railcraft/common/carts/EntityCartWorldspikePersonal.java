/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.machine.worldspike.BlockWorldspike;
import mods.railcraft.common.blocks.machine.worldspike.WorldspikeVariant;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.ItemMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWorldspikePersonal extends EntityCartWorldspike {

    private static final int MINUTES_BEFORE_DISABLE = 5;
    private long ticksSincePlayerLogged;

    public EntityCartWorldspikePersonal(World world) {
        super(world);
    }

    public EntityCartWorldspikePersonal(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.WORLDSPIKE_PERSONAL;
    }

    @Override
    public void onUpdate() {
        if (ticket != null) {
            if (PlayerPlugin.isPlayerConnected(CartToolsAPI.getCartOwner(this)))
                ticksSincePlayerLogged = 0;
            else
                ticksSincePlayerLogged++;
            if (ticksSincePlayerLogged > RailcraftConstants.TICKS_PER_MIN * MINUTES_BEFORE_DISABLE)
                releaseTicket();
        }
        super.onUpdate();
    }

    @Override
    protected ForgeChunkManager.Ticket getTicketFromForge() {
        return ForgeChunkManager.requestPlayerTicket(Railcraft.getMod(), CartToolsAPI.getCartOwner(this).getName(), world, ForgeChunkManager.Type.ENTITY);
    }

    @Override
    public ItemMap<Float> getFuelMap() {
        return RailcraftConfig.worldspikeFuelPersonal;
    }

    @Override
    protected boolean meetsTicketRequirements() {
        return PlayerPlugin.isPlayerConnected(CartToolsAPI.getCartOwner(this)) && super.meetsTicketRequirements();
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return WorldspikeVariant.PERSONAL.getDefaultState().withProperty(BlockWorldspike.ENABLED, hasTicketFlag());
    }
}

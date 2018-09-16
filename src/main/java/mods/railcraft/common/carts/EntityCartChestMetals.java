package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.entity.ChestLogic;
import mods.railcraft.common.util.entity.MetalsChestLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityCartChestMetals extends EntityCartChestRailcraft {

    public EntityCartChestMetals(World world) {
        super(world);
    }

    @Override
    protected ChestLogic createLogic() {
        return new MetalsChestLogic(world, this);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_METALS;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_METALS.getDefaultState();
    }

    @Override
    protected int getTickInterval() {
        return 16;
    }
}

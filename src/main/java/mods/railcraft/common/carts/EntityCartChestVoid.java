package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.util.entity.ChestLogic;
import mods.railcraft.common.util.entity.VoidChestLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class EntityCartChestVoid extends EntityCartChestRailcraft {

    public EntityCartChestVoid(World world) {
        super(world);
    }

    @Override
    protected ChestLogic createLogic() {
        return new VoidChestLogic(world, this);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CHEST_VOID;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.CHEST_VOID.getDefaultState();
    }

    @Override
    protected int getTickInterval() {
        return 8;
    }
}

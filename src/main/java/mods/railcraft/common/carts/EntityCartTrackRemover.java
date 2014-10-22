package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityCartTrackRemover extends CartMaintanceBase {

    public EntityCartTrackRemover(World world) {
        super(world);
    }

    public EntityCartTrackRemover(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isNotHost(worldObj))
            return;

        updateTravelDirection();
        if (travelDirection != ForgeDirection.UNKNOWN && isHalfWay()) {
            ForgeDirection opposite = travelDirection.getOpposite();
            int x = MathHelper.floor_double(this.posX) + opposite.offsetX;
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ) + opposite.offsetZ;
            if (TrackTools.isRailBlockAt(worldObj, x, y, z)) {
                Block block = worldObj.getBlock(x, y, z);
                removeOldTrack(x, y, z, block);
                blink();
            }
        }
    }

    private boolean isHalfWay() {
        double x = Math.abs(this.posX % 1);
        double z = Math.abs(this.posZ % 1);
        switch (travelDirection) {
            case NORTH:
                return z <= 0.5D;
            case SOUTH:
                return z >= 0.5D;
            case WEST:
                return x <= 0.5D;
            case EAST:
                return x >= 0.5D;
            default:
                return false;
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[0];
    }
}

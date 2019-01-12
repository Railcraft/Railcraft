/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.blocks.interfaces.ITileLit;
import mods.railcraft.common.blocks.logic.CrafterLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.Random;

import static net.minecraft.util.EnumParticleTypes.FLAME;

/**
 * Created by CovertJaguar on 12/27/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "buildcraftapi_statements")
public abstract class TileCrafter extends TileLogic implements IHasWork, ITileLit, INeedsFuel {
    private boolean wasBurning;

    @Override
    public void update() {
        super.update();
        if (Game.isClient(world) && hasFlames() && clock(4)) {
            updateLighting();
        }
    }

    private void updateLighting() {
        boolean b = hasFlames();
        if (wasBurning != b) {
            wasBurning = b;
            theWorldAsserted().checkLightFor(EnumSkyBlock.BLOCK, getPos());
            markBlockForUpdate();
        }
    }

    @Override
    public void randomDisplayTick(Random random) {
        updateLighting();
        if (hasFlames() && random.nextInt(100) < 20) {
            float x = getPos().getX() + 0.5F;
            float y = getPos().getY() + 0.4375F + (random.nextFloat() * 3F / 16F);
            float z = getPos().getZ() + 0.5F;
            float offset = 0.52F;
            float randVal = random.nextFloat() * 0.6F - 0.3F;
            World world = theWorldAsserted();
            world.spawnParticle(FLAME, x - offset, y, z + randVal, 0.0D, 0.0D, 0.0D);
            world.spawnParticle(FLAME, x + offset, y, z + randVal, 0.0D, 0.0D, 0.0D);
            world.spawnParticle(FLAME, x + randVal, y, z - offset, 0.0D, 0.0D, 0.0D);
            world.spawnParticle(FLAME, x + randVal, y, z + offset, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getLightValue() {
        return hasFlames() ? 13 : 0;
    }

    public boolean hasFlames() {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(true)
                && getLogic(CrafterLogic.class).map(CrafterLogic::isProcessing).orElse(false);
    }

    @Override
    public boolean needsFuel() {
        return getLogic(INeedsFuel.class).map(INeedsFuel::needsFuel).orElse(false);
    }

    @Override
    public boolean hasWork() {
        return getLogic(CrafterLogic.class).map(CrafterLogic::hasWork).orElse(false);
    }
}

/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.ore;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockWorldLogic extends Block {

    private static BlockWorldLogic instance;

    public static BlockWorldLogic getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null && RailcraftConfig.isBlockEnabled("worldlogic")) {
            instance = new BlockWorldLogic();
            RailcraftRegistry.register(instance);
        }
    }

    public BlockWorldLogic() {
        super(Material.rock);
        setBlockName("railcraft.worldlogic");
        setResistance(6000000.0F);
        setBlockUnbreakable();
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        disableStats();

        setTickRandomly(true);
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.bedrock.getIcon(side, meta);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
        if (MiscTools.getRand().nextInt(32) != 0)
            return;
        BlockOre blockOre = BlockOre.getBlock();
        if (blockOre == null || !EnumOre.SALTPETER.isEnabled() || !RailcraftConfig.isWorldGenEnabled("saltpeter"))
            return;
        int surfaceY = world.getTopSolidOrLiquidBlock(x, z) - 2;

        if (surfaceY < 50 || surfaceY > 100)
            return;

        Block block = WorldPlugin.getBlock(world, x, surfaceY, z);
        if (block != Blocks.sand)
            return;

        Block above = WorldPlugin.getBlock(world, x, surfaceY + 1, z);
        if (above != Blocks.sand)
            return;

        Block below = WorldPlugin.getBlock(world, x, surfaceY - 1, z);
        if (below != Blocks.sand && below != Blocks.sandstone)
            return;

        int airCount = 0;
        Block ore = BlockOre.getBlock();
        for (ForgeDirection side : EnumSet.of(ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST)) {
            boolean isAir = world.isAirBlock(MiscTools.getXOnSide(x, side), MiscTools.getYOnSide(surfaceY, side), MiscTools.getZOnSide(z, side));
            if (isAir)
                airCount++;

            if (airCount > 1)
                return;

            if (isAir)
                continue;

            block = WorldPlugin.getBlockOnSide(world, x, surfaceY, z, side);
            if (block != Blocks.sand && block != Blocks.sandstone && block != ore)
                return;
        }

        world.setBlock(x, surfaceY, z, ore, EnumOre.SALTPETER.ordinal(), 3);
//        System.out.println("saltpeter spawned");
    }

    @Override
    public int tickRate(World world) {
        return 6000;
    }

}

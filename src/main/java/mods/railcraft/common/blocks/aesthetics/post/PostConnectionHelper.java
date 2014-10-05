/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import java.util.HashSet;
import java.util.Set;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.core.IPostConnection.ConnectStyle;
import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import mods.railcraft.common.blocks.signals.ISignalTile;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class PostConnectionHelper {

    public static final Set<Block> canConnect = new HashSet<Block>();
    public static final Set<Block> noConnect = new HashSet<Block>();

    static {
        canConnect.add(Blocks.glowstone);
        canConnect.add(Blocks.redstone_lamp);
        canConnect.add(Blocks.lit_redstone_lamp);
        canConnect.add(Blocks.glass);
        canConnect.add(Blocks.mob_spawner);
        canConnect.add(Blocks.piston);

        noConnect.add(Blocks.dirt);
        noConnect.add(Blocks.stone);
        noConnect.add(Blocks.gravel);
        noConnect.add(Blocks.grass);
        noConnect.add(Blocks.sand);
        noConnect.add(Blocks.bedrock);
        noConnect.add(Blocks.coal_ore);
        noConnect.add(Blocks.diamond_ore);
        noConnect.add(Blocks.emerald_ore);
        noConnect.add(Blocks.gold_ore);
        noConnect.add(Blocks.iron_ore);
        noConnect.add(Blocks.lapis_ore);
        noConnect.add(Blocks.redstone_ore);
        noConnect.add(Blocks.lit_redstone_ore);
        noConnect.add(Blocks.clay);
        noConnect.add(Blocks.snow);
        noConnect.add(Blocks.snow_layer);
        noConnect.add(Blocks.melon_block);
        noConnect.add(Blocks.pumpkin);
        noConnect.add(Blocks.tnt);
        noConnect.add(Blocks.soul_sand);
        noConnect.add(Blocks.netherrack);
        noConnect.add(Blocks.sponge);
    }

    public static ConnectStyle connect(IBlockAccess world, int x1, int y1, int z1, ForgeDirection side) {
        Block block = WorldPlugin.getBlock(world, x1, y1, z1);
        if (block instanceof IPostConnection && ((IPostConnection) block).connectsToPost(world, x1, y1, z1, side) == ConnectStyle.NONE)
            return ConnectStyle.NONE;

        int x2 = MiscTools.getXOnSide(x1, side);
        int y2 = MiscTools.getYOnSide(y1, side);
        int z2 = MiscTools.getZOnSide(z1, side);

        if (world.isAirBlock(x2, y2, z2))
            return ConnectStyle.NONE;

        Block otherBlock = WorldPlugin.getBlock(world, x2, y2, z2);
        ForgeDirection oppositeSide = side.getOpposite();

        try {
            if (otherBlock instanceof IPostConnection)
                return ((IPostConnection) otherBlock).connectsToPost(world, x2, y2, z2, oppositeSide);
        } catch (Error error) {
            Game.logErrorAPI("Railcraft", error, IPostConnection.class);
        }

        if (otherBlock instanceof BlockPostBase)
            return ConnectStyle.TWO_THIN;

        if (noConnect.contains(otherBlock))
            return ConnectStyle.NONE;

        if (canConnect.contains(otherBlock))
            return ConnectStyle.TWO_THIN;

        if (otherBlock instanceof BlockSign) {
            int meta = world.getBlockMetadata(x2, y2, z2);
            return meta == side.ordinal() ? ConnectStyle.SINGLE_THICK : ConnectStyle.NONE;
        }

        if (otherBlock instanceof BlockLantern)
            return ConnectStyle.SINGLE_THICK;

        TileEntity otherTile = world.getTileEntity(x2, y2, z2);
        if (otherTile instanceof ISignalTile)
            return ConnectStyle.TWO_THIN;

        if (world.isSideSolid(x2, y2, z2, oppositeSide, false))
            return ConnectStyle.TWO_THIN;

        // RedPower 2 compat
//        if (Blocks.blocksList[id] != null && Blocks.blocksList[id].getClass().getSimpleName().equals("BlockShapedLamp")) {
//            return true;
//        }

        return ConnectStyle.NONE;
    }

}

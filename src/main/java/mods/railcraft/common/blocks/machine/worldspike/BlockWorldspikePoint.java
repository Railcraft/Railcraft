/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.worldspike;

import mods.railcraft.api.core.WorldCoordinate;
import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockWorldspikePoint extends BlockRailcraft {

    public BlockWorldspikePoint() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setResistance(50);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, pos)) {
                WorldCoordinate ourCoord = new WorldCoordinate(world.provider.getDimension(), pos);
                WorldCoordinate target = TileWorldspike.getTarget(player);
                if (target == null) {
                    TileWorldspike.setTarget(ourCoord, player, getLocalizedName());
                } else {
                    if (world.provider.getDimension() != target.getDim()) {
                        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.dimension", getLocalizedName());
                    } else if (ourCoord.equals(target)) {
                        ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.cancel", getLocalizedName());
                    } else if (TileWorldspike.isTargetLoaded(player, target, getLocalizedName())) {
                        TileEntity tile = WorldPlugin.getBlockTile(world, target.getPos());
                        if (tile instanceof TileWorldspike) {
                            ((TileWorldspike) tile).setPoint(player, ourCoord);
                        } else {
                            ChatPlugin.sendLocalizedChatFromServer(player, "gui.railcraft.worldspike.pair.fail.invalid", getLocalizedName());
                        }
                    }
                    TileWorldspike.removeTarget(player);
                }
                crowbar.onWhack(player, hand, heldItem, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return 8;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
//        return 60f * 3f / 5f;
        return 90f;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void defineRecipes() {
        if (RailcraftConfig.canCraftStandardWorldspikes()) {
            CraftingPlugin.addShapedRecipe(getStack(),
                    " p ",
                    " o ",
                    "ogo",
                    'g', "ingotGold",
                    'p', Items.ENDER_PEARL,
                    'o', new ItemStack(Blocks.OBSIDIAN));
        }
    }
}

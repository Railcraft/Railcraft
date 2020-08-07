/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.worldspike;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(WorldspikeVariant.class)
public class BlockWorldspike extends BlockMachine<WorldspikeVariant> {
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");

    public BlockWorldspike() {
        super(Material.ROCK);
        setDefaultState(getDefaultState()
                .withProperty(ENABLED, true)
        );
        setSoundType(SoundType.STONE);
        setResistance(50);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @Nullable StateMapperBase getStateMapper() {
        return new StateMap.Builder().withName(getVariantEnumProperty()).build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public final boolean isOpaqueCube(IBlockState state) {
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), ENABLED);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        Optional<TileWorldspike> tile = WorldPlugin.getTileEntity(worldIn, pos, TileWorldspike.class);
        state = state.withProperty(ENABLED, tile.map(TileWorldspike::hasActiveTicket).orElse(false));
        return state;
    }

    @Override
    public void defineRecipes() {
        WorldspikeVariant worldspike = WorldspikeVariant.STANDARD;
        if (worldspike.isAvailable() && RailcraftConfig.canCraftStandardWorldspikes()) {
            CraftingPlugin.addShapedRecipe(worldspike.getStack(),
                    "gog",
                    "dpd",
                    "gog",
                    'd', "gemDiamond",
                    'g', "ingotGold",
                    'p', Items.ENDER_PEARL,
                    'o', new ItemStack(Blocks.OBSIDIAN));
        }

        worldspike = WorldspikeVariant.PERSONAL;
        if (worldspike.isAvailable() && RailcraftConfig.canCraftPersonalWorldspikes()) {
            CraftingPlugin.addShapedRecipe(worldspike.getStack(),
                    "gog",
                    "dpd",
                    "gog",
                    'd', "gemEmerald",
                    'g', "ingotGold",
                    'p', Items.ENDER_PEARL,
                    'o', new ItemStack(Blocks.OBSIDIAN));
        }

        worldspike = WorldspikeVariant.PASSIVE;
        if (worldspike.isAvailable() && RailcraftConfig.canCraftPassiveWorldspikes()) {
            CraftingPlugin.addShapedRecipe(worldspike.getStack(),
                    "gog",
                    "dpd",
                    "gog",
                    'd', new ItemStack(Blocks.PRISMARINE, 1, 1),
                    'g', "ingotGold",
                    'p', Items.ENDER_PEARL,
                    'o', new ItemStack(Blocks.OBSIDIAN));
        }
//        }
    }
}

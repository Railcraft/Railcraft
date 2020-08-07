/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.anvil;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockRCAnvil extends BlockAnvil implements IRailcraftBlock {

    public BlockRCAnvil() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHardness(5.0F);
        setSoundType(SoundType.ANVIL);
        setResistance(2000.0F);
    }

    @Override
    public Block getObject() {
        return this;
    }

    @Override
    public @Nullable Class<? extends IVariantEnum> getVariantEnumClass() {
        return DamageState.class;
    }

    @Override
    public @Nullable IVariantEnum[] getVariants() {
        return DamageState.VALUES;
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(DAMAGE, variant.ordinal());
        }
        return state;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "BBB",
                " I ",
                "III",
                'B', "blockSteel",
                'I', "ingotSteel");
    }

    @Override
    public void initializeDefinition() {
        ForestryPlugin.addBackpackItem("forestry.builder", this);

        HarvestPlugin.setBlockHarvestLevel("pickaxe", 2, this);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        else {
            GuiHandler.openGui(EnumGui.ANVIL, playerIn, worldIn, pos);
            return true;
        }
    }

    public enum DamageState implements IVariantEnum {
        UNDAMAGED,
        SLIGHTLY_DAMAGED,
        VARY_DAMAGED;
        public static DamageState[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}

package mods.railcraft.common.blocks.ore;

import mods.railcraft.api.core.IRailcraftRecipeIngredient;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
import mods.railcraft.common.blocks.IVariantEnumBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.modules.RailcraftModuleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by GeneralCamo on 4/4/2017.
 *
 * @author GeneralCamo
 *         Created for Railcraft <http://www.railcraft.info>
 */
public enum EnumOreExtra implements IVariantEnumBlock {

    NICKEL("nickel"),
    POOR_NICKEL("poor_nickel");
    public static final EnumOreExtra[] VALUES = values();
    private final String tag;

    EnumOreExtra(String tag) {
        this.tag = tag;
    }

    @Override
    public IRailcraftBlockContainer getContainer() {
        return RailcraftBlocks.ORE_EXTRA;
    }

    @Nullable
    @Override
    public IBlockState getDefaultState() {
        BlockOreExtra block = (BlockOreExtra) block();
        if (block == null)
            return null;
        return block.getDefaultState().withProperty(block.getVariantProperty(), this);
    }

    public String getTag() {
        return "tile.railcraft.ore_extra_" + tag;
    }

    @Nullable
    public ItemStack getItem() {
        return getItem(1);
    }

    @Nullable
    public ItemStack getItem(int qty) {
        Block block = block();
        if (block == null)
            return null;
        return new ItemStack(block, qty, ordinal());
    }

    @Override
    public boolean isEnabled() {
        return RailcraftModuleManager.isModuleEnabled(ModuleWorld.class) && block() != null && RailcraftConfig.isSubBlockEnabled(getTag());
    }

    public static EnumOreExtra fromOrdinal(int meta) {
        if (meta < 0 || meta >= values().length)
            return NICKEL;
        return values()[meta];
    }

    @Override
    public String getName() {
        return tag;
    }

    @Nullable
    @Override
    public Object getAlternate(IRailcraftRecipeIngredient container) {
        return null;
    }
}

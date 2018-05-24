package mods.railcraft.common.blocks.multi;

import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.BlockEntityDelegate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 */
public abstract class BlockMultiBlock extends BlockEntityDelegate {

    protected BlockMultiBlock(Material materialIn) {
        super(materialIn);
    }

    protected BlockMultiBlock(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileMultiBlock<?> createTileEntity(World world, IBlockState state);

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(TextureMap textureMap) {
        TextureAtlasSheet.unstitchIcons(textureMap, getBlockTexture(), getTextureDimensions());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public abstract Tuple<Integer, Integer> getTextureDimensions();

}

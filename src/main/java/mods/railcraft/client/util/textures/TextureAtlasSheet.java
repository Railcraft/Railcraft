/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.util.textures;

import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TextureAtlasSheet extends TextureAtlasSprite {

    private final int index;
    private final int rows;
    private final int columns;

    public static ResourceLocation[] unstitchIcons(TextureMap textureMap, ResourceLocation textureResource, Tuple<Integer, Integer> textureDimensions) {
        return unstitchIcons(textureMap, textureResource, textureDimensions, "blocks/");
    }

    public static ResourceLocation[] unstitchIcons(TextureMap textureMap, ResourceLocation textureResource, Tuple<Integer, Integer> textureDimensions, String textureFolder) {
        int columns = textureDimensions.getFirst();
        int rows = textureDimensions.getSecond();
        if (columns <= 1 && rows <= 1)
            return new ResourceLocation[]{new ResourceLocation(textureResource.getResourceDomain(), textureFolder + textureResource.getResourcePath())};

        if (Game.DEVELOPMENT_ENVIRONMENT)
            Game.log(Level.INFO, "Unstitching texture sheet: {0} {1}x{2}", textureResource, columns, rows);

        int numIcons = rows * columns;
        ResourceLocation[] locations = new ResourceLocation[numIcons];
        String domain = textureResource.getResourceDomain();
        String name = textureResource.getResourcePath();

        Map<String, TextureAtlasSprite> mapRegisteredSprites = ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, textureMap, 5);  //TODO 1.11

        for (int i = 0; i < numIcons; i++) {
            String texName = domain + ":" + textureFolder + name;
            TextureAtlasSheet texture = new TextureAtlasSheet(texName, i, rows, columns);
            mapRegisteredSprites.put(texture.getIconName(), texture);
            locations[i] = new ResourceLocation(texture.getIconName());
        }
        return locations;
    }

    private TextureAtlasSheet(String name, int index, int rows, int columns) {
        super(name + "_" + index);
        this.index = index;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }



    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> resourceGetter) {
        // Remove the index from the resource path so we can find the original texture.
        location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath().replace("_" + index, ""));

        BufferedImage image;
        IResource resource = null;
        try {
            resource = manager.getResource(location);
            image = ImageIO.read(resource.getInputStream());
        } catch (IOException ex) {
            Game.log(Level.WARN, "Failed to load sub-texture from {0}: {1}", location.getResourcePath(), ex.getLocalizedMessage());
            return true;
        } finally {
            if (resource != null)
                try {
                    resource.getInputStream().close();
                } catch (IOException ignored) {
                }
        }

        int mipmapLevels = Minecraft.getMinecraft().gameSettings.mipmapLevels;

        int size = image.getHeight() / rows;
        int x = index % columns;
        int y = index / columns;

        BufferedImage subImage;
        try {
            subImage = image.getSubimage(x * size, y * size, size, size);
        } catch (RasterFormatException ex) {
            Game.log(Level.WARN, "Failed to load sub-texture from {0} - {1}x{2}: {3}", location.getResourcePath(), image.getWidth(), image.getHeight(), ex.getLocalizedMessage());
            return true;
        }
        this.height = subImage.getHeight();
        this.width = subImage.getWidth();
        int[] rgbaData = new int[height * width];
        subImage.getRGB(0, 0, width, height, rgbaData, 0, width);
        int[][] imageData = new int[1 + mipmapLevels][];
        imageData[0] = rgbaData;
        framesTextureData.add(imageData);
        return false;
    }

    @Override
    public String toString() {
        return super.toString().replace("TextureAtlasSprite", "TextureAtlasSheet");
    }
}

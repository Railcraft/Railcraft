/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.util.textures;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import javax.imageio.ImageIO;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TextureAtlasSheet extends TextureAtlasSprite {

    private final int index;
    private final int rows;
    private final int columns;

    public static IIcon[] unstitchIcons(IIconRegister iconRegister, String name, int numIcons) {
        return unstitchIcons(iconRegister, name, numIcons, 1);
    }

    public static IIcon[] unstitchIcons(IIconRegister iconRegister, String name, int columns, int rows) {
        TextureMap textureMap = (TextureMap) iconRegister;
        int numIcons = rows * columns;
        IIcon[] icons = new IIcon[numIcons];
        for (int i = 0; i < numIcons; i++) {
            String texName = name + "." + i;
            TextureAtlasSheet texture = new TextureAtlasSheet(texName, i, rows, columns);
            textureMap.setTextureEntry(texName, texture);
            icons[i] = texture;
        }
        return icons;
    }

    private TextureAtlasSheet(String name, int index, int rows, int columns) {
        super(name);
        this.index = index;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath().replace("." + index, ""));
        int split = location.getResourcePath().indexOf(':');
        if (split != -1)
            location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath().substring(0, split));
        location = new ResourceLocation(location.getResourceDomain(), "textures/blocks/" + location.getResourcePath() + ".png");

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
                } catch (IOException e) {
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
        int[] rgbaData = new int[this.height * this.width];
        subImage.getRGB(0, 0, this.width, this.height, rgbaData, 0, this.width);
        int[][] imageData = new int[1 + mipmapLevels][];
        imageData[0] = rgbaData;
        framesTextureData.add(imageData);
        return false;
    }

}

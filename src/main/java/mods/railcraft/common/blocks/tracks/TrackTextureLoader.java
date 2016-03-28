/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import mods.railcraft.api.core.ITextureLoader;
import mods.railcraft.api.tracks.ITrackItemIconProvider;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.client.util.textures.TextureAtlasSheet;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTextureLoader implements ITrackItemIconProvider, ITextureLoader {

    public static final TrackTextureLoader INSTANCE = new TrackTextureLoader();
    public final Map<TrackSpec, TextureAtlasSprite[]> sprites = new HashMap<TrackSpec, TextureAtlasSprite[]>();
    public final Map<TrackSpec, TextureAtlasSprite> itemSprite = new HashMap<TrackSpec, TextureAtlasSprite>();

    public void registerSprites(TextureMap textureMap) {
        for (EnumTrack track : EnumTrack.VALUES) {
            if (track.getNumIcons() == 0)
                continue;
            TextureAtlasSprite[] unstiched = TextureAtlasSheet.unstitchIcons(textureMap, track.getTextureTag(), track.getNumIcons());
            sprites.put(track.getTrackSpec(), unstiched);
            itemSprite.put(track.getTrackSpec(), unstiched[track.getItemIconIndex()]);
        }
    }

    @Override
    public TextureAtlasSprite getTrackItemSprite(TrackSpec spec) {
        return itemSprite.get(spec);
    }

    public TextureAtlasSprite[] getTrackIcons(TrackSpec spec) {
        return sprites.get(spec);
    }

}

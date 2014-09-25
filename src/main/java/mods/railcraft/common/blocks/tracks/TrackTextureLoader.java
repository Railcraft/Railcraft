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
import mods.railcraft.api.core.ITextureLoader;
import net.minecraft.util.IIcon;
import mods.railcraft.api.tracks.ITrackItemIconProvider;
import mods.railcraft.api.tracks.TrackSpec;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TrackTextureLoader implements ITextureLoader, ITrackItemIconProvider {

    public static final TrackTextureLoader INSTANCE = new TrackTextureLoader();
    public final Map<TrackSpec, IIcon[]> textures = new HashMap<TrackSpec, IIcon[]>();
    public final Map<TrackSpec, IIcon> itemIcon = new HashMap<TrackSpec, IIcon>();

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (EnumTrack track : EnumTrack.VALUES) {
            if (track.getNumIcons() == 0)
                continue;
            IIcon[] icons = TextureAtlasSheet.unstitchIcons(iconRegister, track.getTextureTag(), track.getNumIcons());
            textures.put(track.getTrackSpec(), icons);
            itemIcon.put(track.getTrackSpec(), icons[track.getItemIconIndex()]);
        }
    }

    @Override
    public IIcon getTrackItemIcon(TrackSpec spec) {
        IIcon icon = itemIcon.get(spec);
        if (icon == null)
            icon = Blocks.rail.getIcon(0, 0);
        return icon;
    }

    public IIcon[] getTrackIcons(TrackSpec spec) {
        return textures.get(spec);
    }

}

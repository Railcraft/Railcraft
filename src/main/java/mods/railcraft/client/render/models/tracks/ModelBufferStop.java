/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render.models.tracks;

import mods.railcraft.client.render.models.ModelSimple;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModelBufferStop extends ModelSimple {

    public ModelBufferStop() {
        super("buffer");
        renderer.setTextureSize(64, 32);
        setTextureOffset("buffer.bumper2", 31, 14);
        setTextureOffset("buffer.bumper1", 31, 14);
        setTextureOffset("buffer.baseSmall", 27, 1);
        setTextureOffset("buffer.baseBig", 1, 1);
        setTextureOffset("buffer.boardR1", 1, 16);
        setTextureOffset("buffer.boardR2", 1, 24);
        setTextureOffset("buffer.boardW", 15, 16);

        renderer.setRotationPoint(8, 8, 8);
        renderer.addBox("bumper2", 3F, -1F, -5F, 2, 2, 1);
        renderer.addBox("bumper1", -5F, -1F, -5F, 2, 2, 1);
        renderer.addBox("baseSmall", -3F, -8F, 1F, 6, 7, 4);
        renderer.addBox("baseBig", -4F, -8F, -3F, 8, 9, 4);
        renderer.addBox("boardR1", -6F, -2F, -4F, 4, 4, 2);
        renderer.addBox("boardR2", 2F, -2F, -4F, 4, 4, 2);
        renderer.addBox("boardW", -2F, -2F, -4F, 4, 4, 2);
    }
}

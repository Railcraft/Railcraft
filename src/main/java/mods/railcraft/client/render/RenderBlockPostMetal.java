/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.client.render.RenderBlockPost.RenderPost;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockPostMetal extends BlockRenderer {

    public static BlockRenderer make(BlockPostMetal block) {
        BlockRenderer renderer = new RenderBlockPostMetal(block);
        renderer.setDefaultRenderer(new RenderPost());
        return renderer;
    }

    private RenderBlockPostMetal(Block block) {
        super(block);
    }

}

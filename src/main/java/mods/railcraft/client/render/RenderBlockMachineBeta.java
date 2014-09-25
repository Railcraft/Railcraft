/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.beta.TileChestMetals;
import mods.railcraft.common.blocks.machine.beta.TileChestVoid;
import mods.railcraft.common.core.RailcraftConstants;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBlockMachineBeta extends BlockRenderer {

    public RenderBlockMachineBeta() {
        super(RailcraftBlocks.getBlockMachineBeta());

        addBlockRenderer(EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal(), new RenderBoilerTank());
        addBlockRenderer(EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal(), new RenderBoilerTank());

        addBlockRenderer(EnumMachineBeta.ENGINE_STEAM_HOBBY.ordinal(), new DoNothingRenderer());
        addItemRenderer(EnumMachineBeta.ENGINE_STEAM_HOBBY.ordinal(), RenderPneumaticEngine.renderHobby);

        addBlockRenderer(EnumMachineBeta.ENGINE_STEAM_LOW.ordinal(), new DoNothingRenderer());
        addItemRenderer(EnumMachineBeta.ENGINE_STEAM_LOW.ordinal(), RenderPneumaticEngine.renderLow);

        addBlockRenderer(EnumMachineBeta.ENGINE_STEAM_HIGH.ordinal(), new DoNothingRenderer());
        addItemRenderer(EnumMachineBeta.ENGINE_STEAM_HIGH.ordinal(), RenderPneumaticEngine.renderHigh);

        addCombinedRenderer(EnumMachineBeta.SENTINEL.ordinal(), new RenderSentinel());

        addBlockRenderer(EnumMachineBeta.VOID_CHEST.ordinal(), new DoNothingRenderer());
        addItemRenderer(EnumMachineBeta.VOID_CHEST.ordinal(), new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_void.png", new TileChestVoid()));

        addBlockRenderer(EnumMachineBeta.METALS_CHEST.ordinal(), new DoNothingRenderer());
        addItemRenderer(EnumMachineBeta.METALS_CHEST.ordinal(), new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_metals.png", new TileChestMetals()));
    }
}

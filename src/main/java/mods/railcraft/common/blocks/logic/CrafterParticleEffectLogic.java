/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

/**
 * Created by CovertJaguar on 8/4/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class CrafterParticleEffectLogic extends Logic {
    private final Runnable spawnParticles;
    private boolean wasProcessing;

    public CrafterParticleEffectLogic(Adapter adapter, Runnable spawnParticles) {
        super(adapter);
        this.spawnParticles = spawnParticles;
    }

    @Override
    protected void updateClient() {
        super.updateClient();
        boolean isProcessing = getLogic(CrafterLogic.class).map(CrafterLogic::isProcessing).orElse(false);
        if (wasProcessing != isProcessing && !isProcessing) {
            spawnParticles.run();
        }
        wasProcessing = isProcessing;
    }
}

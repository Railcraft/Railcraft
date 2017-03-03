package atomicstryker.dynamiclights.client;

import net.minecraft.entity.Entity;

public interface IDynamicLightSource {
    Entity getAttachmentEntity();
    int getLightLevel();
}

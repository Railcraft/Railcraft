/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.api.signals.SignalAspect;

import java.util.*;

/**
 * Created by CovertJaguar on 4/23/2015.
 */
public class TokenBlock {
    public static final Map<UUID, TokenBlock> tokenBlocks = new HashMap<UUID, TokenBlock>();
    private final UUID uuid;
    private UUID entityWithToken;

    public TokenBlock(UUID uuid) {
        this.uuid = uuid;
    }

    public static TokenBlock getOrCreateTokenBlock(UUID uuid) {
        TokenBlock tokenBlock = tokenBlocks.get(uuid);
        if (tokenBlock == null) {
            tokenBlock = new TokenBlock(uuid);
            tokenBlocks.put(uuid, tokenBlock);
        }
        return tokenBlock;
    }

    public void setOrClearEntityWithToken(UUID entityId) {
        if (this.entityWithToken == entityId)
            this.entityWithToken = null;
        else
            this.entityWithToken = entityId;
    }

    public SignalAspect getAspect() {
        if (entityWithToken != null)
            return SignalAspect.RED;
        return SignalAspect.GREEN;
    }
}

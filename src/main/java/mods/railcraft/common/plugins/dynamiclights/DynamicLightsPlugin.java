/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.dynamiclights;

import atomicstryker.dynamiclights.client.IDynamicLightSource;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;

/**
 * A plugin for compatibility with
 * <url https://github.com/AtomicStryker/atomicstrykers-minecraft-mods/blob/1.10/DynamicLights/>
 * Dynamic Lights</url>
 */
public final class DynamicLightsPlugin {
    public static final String MOD_ID = "DynamicLights";
    private static final DynamicLightsPlugin INSTANCE = new DynamicLightsPlugin();

    private IHandle handle;

    public static DynamicLightsPlugin getInstance() {
        return INSTANCE;
    }

    public void registerEntityLightSource(Class<? extends Entity> type, ToIntFunction<Entity> lightCalculator) {
        handle.registerEntityLightSource(type, lightCalculator);
    }

    private DynamicLightsPlugin() {
        if (FMLCommonHandler.instance().getSide().isClient()) { //only enable on client
            try {
                Class<?> dynamicLightsClass = Class.forName("atomicstryker.dynamiclights.client.DynamicLights");
                Class<?> iDynamicLightSourceClass = IDynamicLightSource.class;
                Method addLightSource = dynamicLightsClass.getMethod("addLightSource", iDynamicLightSourceClass);
                handle = new PresentHandle(addLightSource);
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
                handle = new AbsentHandle();
            }
        } else {
            handle = new AbsentHandle();
        }
    }

    private interface IHandle {
        void registerEntityLightSource(Class<? extends Entity> type, ToIntFunction<Entity> lightCalculator);
    }

    private static final class AbsentHandle implements IHandle {
        @Override
        public void registerEntityLightSource(Class<? extends Entity> type, ToIntFunction<Entity> lightCalculator) {

        }
    }

    private static final class PresentHandle implements IHandle {
        private Method addLightSource;
        private Map<Class<? extends Entity>, ToIntFunction<Entity>> lightCalculatorMap;

        PresentHandle(Method addLightSource) {
            this.addLightSource = addLightSource;
            lightCalculatorMap = new ConcurrentHashMap<>();
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        public void registerEntityLightSource(Class<? extends Entity> type, ToIntFunction<Entity> lightCalculator) {
            lightCalculatorMap.put(type, lightCalculator);
        }

        @SubscribeEvent
        public void onEntityJoin(EntityJoinWorldEvent event) {
            if (!event.getWorld().isRemote) {
                return; //only process on the client
            }
            Entity entity = event.getEntity();
            ToIntFunction<Entity> function = lightCalculatorMap.get(entity.getClass());
            if (function != null) {
                addLightSource(entity, function);
            }
        }

        private void addLightSource(Entity source, ToIntFunction<Entity> lightCalculator) {
            try {
                addLightSource.invoke(null, new IDynamicLightSource() {
                    @Override
                    public Entity getAttachmentEntity() {
                        return source;
                    }

                    @Override
                    public int getLightLevel() {
                        return lightCalculator.applyAsInt(source);
                    }
                });
            } catch (IllegalAccessException | InvocationTargetException e) {
                Game.log().throwable(Level.ERROR, 10, e, "Dynamic Light plugin encountered a problem, please report to Railcraft");
            }
        }
    }
}

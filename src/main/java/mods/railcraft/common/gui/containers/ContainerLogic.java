/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.gui.containers;

import mods.railcraft.common.blocks.logic.ILogicContainer;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.SyncToGui;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by CovertJaguar on 1/11/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ContainerLogic extends RailcraftContainer {
    protected final Logic root;
    private final List<SyncField> fields = new ArrayList<>();

    public ContainerLogic(ILogicContainer logicContainer) {
        super(player -> Logic.Adapter.from(logicContainer).isUsableByPlayer(player));
        this.root = Logic.get(Logic.class, logicContainer);

        root.logics().forEach(logic ->
                Arrays.stream(FieldUtils.getAllFields(logic.getClass()))
                        .filter(field -> field.isAnnotationPresent(SyncToGui.class))
                        .sorted(Comparator.comparing(Field::getName))
                        .forEach(field -> fields.add(new SyncField(logic, field))));
    }

    private class SyncField {
        final Logic logic;
        final Field field;
        int lastValue;

        private SyncField(Logic logic, Field field) {
            this.logic = logic;
            this.field = field;

            field.setAccessible(true);
        }

        void send(IContainerListener listener, int id) {
            int newValue = 0;
            try {
                Object value = field.get(logic);
                if (value instanceof Integer)
                    newValue = (Integer) value;
                else if (value instanceof Double)
                    newValue = MathHelper.ceil((Double) value);
            } catch (IllegalAccessException ignored) {}

            if (lastValue != newValue) {
                listener.sendWindowProperty(ContainerLogic.this, id, newValue);
                lastValue = newValue;
            }
        }

        void update(int value) {
            try {
                field.setInt(logic, value);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    private void update(IContainerListener listener) {
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).send(listener, i);
        }
    }

    @Override
    public void sendUpdateToClient() {
        super.sendUpdateToClient();

        for (IContainerListener listener : listeners) {
            update(listener);
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        update(listener);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id < fields.size())
            fields.get(id).update(value);
    }
}

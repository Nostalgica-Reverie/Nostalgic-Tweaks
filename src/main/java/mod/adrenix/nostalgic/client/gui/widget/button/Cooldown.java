package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicPriority;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;

import java.util.List;

class Cooldown {
    static class Disable<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>>
            implements DynamicFunction<Builder, Button> {
        @Override
        public void apply(Button button, Builder builder) {
            if (!builder.cooldownFlag.get())
                button.setActive();
        }

        @Override
        public boolean isReapplyNeeded(Button button, Builder builder, WidgetCache cache) {
            return true;
        }

        @Override
        public List<DynamicField> getManaging(Builder builder) {
            return List.of(DynamicField.ACTIVE);
        }
    }

    static class Enable<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>>
            implements DynamicFunction<Builder, Button> {
        @Override
        public void apply(Button button, Builder builder) {
            if (builder.cooldownFlag.get())
                button.setInactive();
        }

        @Override
        public boolean isReapplyNeeded(Button button, Builder builder, WidgetCache cache) {
            return true;
        }

        @Override
        public List<DynamicField> getManaging(Builder builder) {
            return List.of(DynamicField.ACTIVE);
        }

        @Override
        public DynamicPriority priority() {
            return DynamicPriority.HIGH;
        }
    }
}

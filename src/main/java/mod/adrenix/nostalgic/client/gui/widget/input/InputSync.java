package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class InputSync<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>>
        implements DynamicFunction<Builder, Input> {
    @Nullable
    private final CacheValue<String> input;

    InputSync(Input widget) {
        if (widget.getBuilder().sync == null)
            this.input = null;
        else
            this.input = CacheValue.create(() -> widget.getBuilder().sync.apply(widget));
    }

    @Override
    public void apply(Input widget, Builder builder) {
        if (this.input == null)
            return;

        widget.setInput(this.input.getAndUpdate());
    }

    @Override
    public boolean isReapplyNeeded(Input widget, Builder builder, WidgetCache cache) {
        if (this.input == null)
            return false;

        return !this.input.next().equals(widget.getInput());
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of();
    }
}

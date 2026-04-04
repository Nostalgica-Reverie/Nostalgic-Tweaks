package mod.adrenix.nostalgic.client.gui.overlay;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;

import java.util.Collection;
import java.util.List;

public class OverlayWidgets {
    public final UniqueArrayList<DynamicWidget<?, ?>> all;
    public final UniqueArrayList<DynamicWidget<?, ?>> internal;
    public final UniqueArrayList<DynamicWidget<?, ?>> external;
    public final UniqueArrayList<DynamicWidget<?, ?>> listeners;
    public final UniqueArrayList<DynamicWidget<?, ?>> relatives;
    public final UniqueArrayList<DynamicWidget<?, ?>> projected;
    public final UniqueArrayList<DynamicWidget<?, ?>> scissored;
    public final UniqueArrayList<DynamicWidget<?, ?>> scrollbars;

    OverlayWidgets() {
        this.all = new UniqueArrayList<>();
        this.external = new UniqueArrayList<>();
        this.internal = new UniqueArrayList<>();
        this.listeners = new UniqueArrayList<>();
        this.relatives = new UniqueArrayList<>();
        this.projected = new UniqueArrayList<>();
        this.scissored = new UniqueArrayList<>();
        this.scrollbars = new UniqueArrayList<>();
    }

    void addScissoredExternal(DynamicWidget<?, ?> widget) {
        this.all.add(widget);
        this.external.add(widget);
        this.scissored.add(widget);
        this.projected.remove(widget);
    }

    void addProjectedExternal(DynamicWidget<?, ?> widget) {
        this.all.add(widget);
        this.external.add(widget);
        this.projected.add(widget);
        this.scissored.remove(widget);
    }

    void removeAll(Collection<DynamicWidget<?, ?>> widgets) {
        for (DynamicWidget<?, ?> widget : widgets) {
            this.all.remove(widget);
            this.external.remove(widget);
            this.listeners.remove(widget);
            this.relatives.remove(widget);
            this.projected.remove(widget);
            this.scissored.remove(widget);
        }
    }

    void removeAll(DynamicWidget<?, ?>... widgets) {
        this.removeAll(List.of(widgets));
    }
}

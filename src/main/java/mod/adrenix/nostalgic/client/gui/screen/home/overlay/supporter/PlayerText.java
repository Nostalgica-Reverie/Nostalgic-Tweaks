package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;

record PlayerText(Holder<DynamicWidget<?, ?>> name, NullableHolder<DynamicWidget<?, ?>> first,
                  NullableHolder<DynamicWidget<?, ?>> last) {
}

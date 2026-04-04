package mod.adrenix.nostalgic.client.gui.overlay.types.state;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextBuilder;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SwitchGroup {
    /* Fields */

    private final WidgetHolder parent;
    private final Supplier<Component> header;
    private final Supplier<Component> description;
    private final ButtonWidget toggle;
    @Nullable
    private Group group;

    /* Constructor */

    private SwitchGroup(WidgetHolder parent, Supplier<Component> header, Supplier<Component> description, BooleanSupplier getter, BooleanConsumer setter) {
        this.parent = parent;
        this.description = description;
        this.header = header;
        this.toggle = ButtonTemplate.toggle(getter, setter).build();
    }

    /* Static */

    /**
     * Create a new switch group using the given suppliers and consumer.
     *
     * @param parent      A {@link WidgetHolder} that will accept the built {@link Group}.
     * @param header      A {@link Supplier} that provides a header {@link Component}.
     * @param description A {@link Supplier} that provides a description {@link Component}.
     * @param getter      A {@link BooleanSupplier} that gets the switch's current state.
     * @param setter      A {@link BooleanConsumer} that accepts a new switch state.
     * @return A new {@link SwitchGroup} instance.
     */
    public static SwitchGroup create(WidgetHolder parent, Supplier<Component> header, Supplier<Component> description, BooleanSupplier getter, BooleanConsumer setter) {
        return new SwitchGroup(parent, header, description, getter, setter);
    }

    /**
     * Create a new switch group using only component instances for the header and description.
     *
     * @param parent      A {@link WidgetHolder} that will accept the built {@link Group}.
     * @param header      A {@link Component} for the header.
     * @param description A {@link Component} for the description.
     * @param getter      A {@link BooleanSupplier} that gets the switch's current state.
     * @param setter      A {@link BooleanConsumer} that accepts a new switch state.
     * @return A new {@link SwitchGroup} instance.
     */
    public static SwitchGroup create(WidgetHolder parent, Component header, Component description, BooleanSupplier getter, BooleanConsumer setter) {
        return new SwitchGroup(parent, () -> header, () -> description, getter, setter);
    }

    /**
     * Create a new switch group using lang keys to provide a header and description.
     *
     * @param parent      A {@link WidgetHolder} that will accept the built {@link Group}.
     * @param header      A {@link Translation} instance for the header.
     * @param description A {@link Translation} instance for the description.
     * @param getter      A {@link BooleanSupplier} that gets the switch's current state.
     * @param setter      A {@link BooleanConsumer} that accepts a new switch state.
     * @return A new {@link SwitchGroup} instance.
     */
    public static SwitchGroup create(WidgetHolder parent, Translation header, Translation description, BooleanSupplier getter, BooleanConsumer setter) {
        return new SwitchGroup(parent, header::get, description::get, getter, setter);
    }

    /* Widgets */

    /**
     * @return A new {@link Widgets} instance.
     */
    public Widgets getWidgets() {
        TextWidget header = this.getHeader().build();
        TextWidget description = this.getDescription().below(header, 4).build();

        return new Widgets(this.toggle, header, description);
    }

    /* Methods */

    /**
     * @return The {@link ButtonWidget} toggle widget for this group.
     */
    public ButtonWidget getToggle() {
        return this.toggle;
    }

    /**
     * @return A new {@link TextBuilder} instance for the state toggle header.
     */
    public TextBuilder getHeader() {
        return TextWidget.create(this.header).rightOf(this.toggle, 4);
    }

    /**
     * @return A new {@link TextBuilder} instance for the state toggle description.
     */
    public TextBuilder getDescription() {
        return TextWidget.create(this.description).rightOf(this.toggle, 4);
    }

    /**
     * @return Getter for the {@link Group} instance created by this utility.
     */
    public Group getInstance() {
        if (this.group != null)
            return this.group;

        TextWidget header = this.getHeader().extendWidthToScreenEnd(0).build();
        TextWidget description = this.getDescription().below(header, 4).extendWidthToScreenEnd(0).build();

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
                .below(description, 4)
                .height(1)
                .extendWidthToScreenEnd(6)
                .build();

        this.group = Group.create(this.parent)
                .extendWidthToScreenEnd(0)
                .addWidget(this.toggle)
                .addWidget(header)
                .addWidget(description)
                .addWidget(separator)
                .build();

        return this.group;
    }

    /**
     * A record of widgets that are a part of a switch group.
     *
     * @param toggle      The toggle {@link ButtonWidget}.
     * @param header      The header {@link TextWidget}.
     * @param description The description {@link TextWidget}.
     */
    public record Widgets(ButtonWidget toggle, TextWidget header, TextWidget description) {
        /**
         * Subscribe all the widgets to the given consumer.
         *
         * @param parent A {@link WidgetHolder} instance.
         */
        @PublicAPI
        public void subscribeTo(WidgetHolder parent) {
            parent.addWidgets(this.toggle, this.header, this.description);
        }
    }
}

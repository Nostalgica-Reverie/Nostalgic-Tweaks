package mod.adrenix.nostalgic.client.gui.widget.group;

import mod.adrenix.nostalgic.client.gui.PaddingManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class GroupBuilder extends DynamicBuilder<GroupBuilder, Group>
        implements LayoutBuilder<GroupBuilder, Group>, ActiveBuilder<GroupBuilder, Group>,
        VisibleBuilder<GroupBuilder, Group>, PaddingManager<GroupBuilder> {
    /* Fields */

    final WidgetHolder parent;
    final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    Supplier<Component> groupTitle = null;
    TextureIcon icon = TextureIcon.EMPTY;
    Color outlineColor = Color.TRANSPARENT;
    Color backgroundColor = Color.TRANSPARENT;
    Color borderColor = Color.WHITE;
    boolean outline = false;
    boolean border = false;
    int outlineThickness = 2;
    int bottomOffset = 0;
    int paddingTop = 0;
    int paddingBottom = 0;
    int paddingLeft = 0;
    int paddingRight = 0;

    /* Constructor */

    protected GroupBuilder(WidgetHolder parent) {
        this.parent = parent;
        this.widgets = new UniqueArrayList<>();
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public GroupBuilder self() {
        return this;
    }

    @Override
    public GroupBuilder paddingLeft(int padding) {
        this.paddingLeft = padding;

        return this;
    }

    @Override
    public GroupBuilder paddingTop(int padding) {
        this.paddingTop = padding;

        return this;
    }

    @Override
    public GroupBuilder paddingRight(int padding) {
        this.paddingRight = padding;

        return this;
    }

    @Override
    public GroupBuilder paddingBottom(int padding) {
        this.paddingBottom = padding;

        return this;
    }

    /**
     * Define a widget to be added to the overlay when it is built.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public GroupBuilder addWidget(DynamicWidget<?, ?> widget) {
        if (this.widget.isEmpty())
            this.widgets.add(widget);
        else
            this.widget.getOrThrow().addWidget(widget);

        return this;
    }

    /**
     * Offset the bottom padding by the given amount. For example, an offset of {@code -1} will shrink the bottom
     * padding by one unit.
     *
     * @param offset The amount of offset.
     */
    @PublicAPI
    public GroupBuilder bottomOffset(int offset) {
        this.bottomOffset = offset;

        return this;
    }

    /**
     * Set an outline around this group. There is a difference between the group's outline and the group's border. The
     * border embedded titles, but the outline does not. The outline allows for custom thickness, but the border does
     * not. This will <b color=red>disable</b> any previous border options.
     *
     * @param color     A {@link Color} instance.
     * @param thickness A thickness for the outline. The default value is {@code 2}.
     */
    @PublicAPI
    public GroupBuilder outline(Color color, int thickness) {
        this.border = false;
        this.outline = true;
        this.outlineColor = color;
        this.outlineThickness = thickness;

        return this;
    }

    /**
     * Set an outline around this group. There is a difference between the group's outline and the group's border. The
     * border can embed titles, but the outline cannot. The outline allows for custom thickness, but the border does
     * not. This method uses a default thickness of {@code 2}. This will <b color=red>disable</b> any previous border
     * options.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public GroupBuilder outline(Color color) {
        return this.outline(color, 2);
    }

    /**
     * Set a border around this group. There is a difference between the group's outline and the group's border. The
     * border can embed titles, but the outline cannot. The outline allows for custom thickness, but the border does
     * not. This will <b color=red>disable</b> any previous outline options.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public GroupBuilder border(Color color) {
        this.outline = false;
        this.border = true;
        this.borderColor = color;

        return this;
    }

    /**
     * Set a background color for this group.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public GroupBuilder background(Color color) {
        this.backgroundColor = color;

        return this;
    }

    /**
     * Set an icon that will be displayed to the left of the group's text. This icon will only appear if this group has
     * a title set.
     *
     * @param icon A {@link TextureIcon} instance.
     */
    @PublicAPI
    public GroupBuilder icon(TextureIcon icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Set a title supplier for this group. This will be rendered at the top-left of the group window.
     *
     * @param supplier A component title supplier.
     * @see #title(Translation)
     * @see #title(Component)
     * @see #title(String)
     */
    @PublicAPI
    public GroupBuilder title(Supplier<Component> supplier) {
        this.groupTitle = supplier;
        return this;
    }

    /**
     * Set the title for this group using a {@link Translation} instance.
     *
     * @param langKey A {@link Translation}.
     * @see #title(Supplier)
     * @see #title(Component)
     * @see #title(String)
     */
    @PublicAPI
    public GroupBuilder title(Translation langKey) {
        return this.title(langKey::get);
    }

    /**
     * Set a title for this group. This will be rendered at the top-left of the group window.
     *
     * @param title A component title.
     * @see #title(Supplier)
     * @see #title(Translation)
     * @see #title(String)
     */
    @PublicAPI
    public GroupBuilder title(Component title) {
        return this.title(() -> title);
    }

    /**
     * Set a title for this group. This will be rendered at the top-left of the group window.
     *
     * @param title A string title.
     * @see #title(Supplier)
     * @see #title(Translation)
     * @see #title(Component)
     */
    @PublicAPI
    public GroupBuilder title(String title) {
        return this.title(Component.literal(title));
    }

    /**
     * @return Whether the group height is being controlled by a supplier.
     */
    protected boolean isHeightOverridden() {
        return this.height != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Group construct() {
        return new Group(this);
    }
}

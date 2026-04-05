package mod.adrenix.nostalgic.client.gui.screen.vanilla.world.select;

import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonRenderer;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.RowData;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.mixin.access.WorldListEntryAccess;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

class WorldRow extends AbstractRow<WorldRowMaker, WorldRow> {
    /* Fields */

    private final Function<LevelSummary, Supplier<String>> getLevelSize;
    private final WorldSelectionList.WorldListEntry entry;
    private final NullableHolder<WorldRow> selected;
    private final FaviconTexture favicon;
    private final BlankWidget icon;
    private final LevelSummary summary;
    private long lastClickTime;

    /* Constructors */

    WorldRow(WorldRowMaker builder) {
        super(builder);

        this.favicon = ((WorldListEntryAccess) (Object) builder.entry).nt$getIcon();
        this.selected = builder.selected;
        this.summary = builder.summary;
        this.entry = builder.entry;
        this.getLevelSize = builder.getLevelSize;

        boolean isIcon = true;//CandyTweak.ADD_WORLD_THUMBNAIL.get(); //TODO
        this.icon = BlankWidget.create()
                .pos(isIcon ? 3 : 4, 3)
                .size(isIcon ? 32 : 0)
                .renderer(this::renderIcon)
                .build(this::addWidget);

        this.init();
        this.getBuilder().preRenderer(this::renderBox);
    }

    /* Methods */

    /**
     * Initializes row widgets.
     */
    private void init() {
        boolean isMetadata = true;//CandyTweak.ADD_WORLD_METADATA.get(); //TODO
        boolean isIcon = true;//CandyTweak.ADD_WORLD_THUMBNAIL.get(); //TODO

        String name = this.summary.getLevelName();

        if (name.isEmpty())
            name = "World";

        TextWidget header = TextWidget.create(name)
                .rightOf(this.icon, isIcon ? 3 : 0)
                .extendWidthToEnd(this, 4)
                .build(this::addWidget);

        final Supplier<String> levelSize = this.getLevelSize.apply(this.summary);
        long lastPlayed = this.summary.getLastPlayed();
        ZonedDateTime lastPlayedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastPlayed), ZoneId.systemDefault());
        final String date = WorldSelectionList.DATE_FORMAT.format(lastPlayedTime);
        final String unknown = Lang.Worlds.BETA_UNKNOWN_LAST_PLAYED.getString();
        final String time = String.format("%s (%s, ", this.summary.getLevelId(), lastPlayed != -1L ? date : unknown);

        TextWidget data = TextWidget.create(() -> Component.literal(time + levelSize.get() + ")"))
                .color(Color.GRAY)
                .rightOf(this.icon, isIcon ? 3 : 0)
                .below(header, 2)
                .extendWidthToEnd(this, 4)
                .build(this::addWidget);

        if (Color.RED.matches(this.summary.getInfo().getStyle()) || isMetadata) {
            TextWidget.create(this.summary.getInfo())
                    .color(isMetadata ? Color.GRAY : Color.RED)
                    .rightOf(this.icon, isIcon ? 3 : 0)
                    .below(data, 2)
                    .extendWidthToEnd(this, 4)
                    .build(this::addWidget);
        }

        ButtonWidget.create()
                .noClickSound()
                .attach(RowData.WIDGET_SYNCED_WITH_HEIGHT)
                .backgroundRenderer(ButtonRenderer.EMPTY)
                .whenFocused(() -> this.selected.set(this))
                .onPress(this::onPress)
                .hoverCursor(null)
                .pos(this::getX, this::getY)
                .size(this::getWidth, this::getHeight)
                .build(this::addWidget);
    }

    /**
     * @return The {@link WorldSelectionList.WorldListEntry} instance associated with this row.
     */
    public WorldSelectionList.WorldListEntry getEntry() {
        return this.entry;
    }

    /**
     * @return Whether the primary action of the selected world is active.
     */
    public boolean isPrimaryActionActive() {
        return this.summary.primaryActionActive();
    }

    /**
     * @return Whether the selected world can be edited.
     */
    public boolean canEdit() {
        return this.summary.canEdit();
    }

    /**
     * @return Whether the selected world can be deleted.
     */
    public boolean canDelete() {
        return this.summary.canDelete();
    }

    /**
     * Instructions to perform when the main row button is pressed.
     */
    private void onPress() {
        this.selected.set(this);

        if (Util.getMillis() - this.lastClickTime >= 250L)
            this.lastClickTime = Util.getMillis();
        else
            this.entry.joinWorld();
    }

    /**
     * Handler method for rendering the world's icon using a blank widget.
     *
     * @param widget      A {@link BlankWidget} instance.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderIcon(BlankWidget widget, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (true/*CandyTweak.ADD_WORLD_THUMBNAIL.get()*/) //TODO
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.favicon.textureLocation(), widget.getX(), widget.getY(), 0.0F, 0.0F, 32, 32, 32, 32);
    }

    /**
     * Handler method for rendering the selection box for a world row.
     *
     * @param row         A {@link WorldRow} instance.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderBox(WorldRow row, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!row.equals(this.selected.get()))
            return;

        Color background = Color.BLACK;
        Color outline = Color.GRAY;

        graphics.fill(this.getX(), this.getY(), this.getEndX(), this.getEndY(), background.get());
        graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), outline.get());
    }
}

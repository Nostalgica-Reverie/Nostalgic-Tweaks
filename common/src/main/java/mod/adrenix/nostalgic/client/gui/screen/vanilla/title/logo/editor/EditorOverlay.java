package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.embed.Embed;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class EditorOverlay
{
    /* Static */

    /**
     * The padding that should be used between text widgets that are used as paragraphs.
     */
    private static final int PARAGRAPH_SPACING = GuiUtil.textHeight() + 2;

    /**
     * A static instance of this overlay is used so that when the overlay is opened again, it can pick up where the user
     * last left off.
     */
    private static final Overlay HELP_OVERLAY = Overlay.create(Lang.Logo.Help.TITLE::get)
        .icon(Icons.SMALL_INFO)
        .backgroundColor(new Color(0x191B34, 220))
        .resizeHeightUsingPercentage(0.65D)
        .resizeWidthUsingPercentage(0.75D)
        .build();

    /**
     * A static instance of an embed for all help categories, which helps with the logical flow for functional building
     * of the help overlay.
     */
    private static final Embed HELP_CATEGORIES = Embed.create()
        .widthOfScreen(0.3F)
        .extendHeightToScreenEnd(0)
        .borderThickness(0)
        .build(HELP_OVERLAY::addWidget);

    /**
     * This separates the help section buttons from the help section paragraph text.
     */
    private static final SeparatorWidget HELP_SEPARATOR = SeparatorWidget.create(Color.AZURE_WHITE)
        .width(1)
        .extendHeightToScreenEnd(0)
        .rightOf(HELP_CATEGORIES, 0)
        .build(HELP_OVERLAY::addWidget);

    /* Enumerations */

    private enum HelpCategory
    {
        TERMINOLOGY(Icons.BOOK_OPEN, Lang.Logo.Help.TERMINOLOGY, Lang.Logo.Help.TERMINOLOGY_INFO),
        TIPS_AND_HINTS(Icons.YELLOW_LIGHT, Lang.Logo.Help.TIPS_AND_HINTS, Lang.Logo.Help.TIPS_AND_HINTS_INFO),
        KEY_SHORTCUTS(Icons.LIGHTNING, Lang.Logo.Help.KEY_SHORTCUTS, Lang.Logo.Help.KEY_SHORTCUTS_INFO),
        CANVAS_TOOLS(Icons.PAINTBRUSH, Lang.Logo.Help.CANVAS_TOOLS, Lang.Logo.Help.CANVAS_TOOLS_INFO),
        PIXEL_SETTINGS(Icons.WRENCH, Lang.Logo.Help.PIXEL_SETTINGS, Lang.Logo.Help.PIXEL_SETTINGS_INFO),
        BATCH_EDITING(Icons.FILTER, Lang.Logo.Help.BATCH_EDITING, Lang.Logo.Help.BATCH_EDITING_INFO),
        FILE_OPTIONS(Icons.FOLDER, Lang.Logo.Help.FILE_OPTIONS, Lang.Logo.Help.FILE_OPTIONS_INFO),
        ACTION_HISTORY(Icons.CLIPBOARD, Lang.Logo.Help.ACTION_HISTORY, Lang.Logo.Help.ACTION_HISTORY_INFO),
        MOVE_TOOLBAR(Icons.UP_ARROW, Lang.Logo.Help.MOVE_TOOLBAR, Lang.Logo.Help.MOVE_TOOLBAR_INFO);

        /* Fields */

        final TextureIcon icon;
        final Translation title;
        final FlagHolder selector;

        final Supplier<ArrayList<Translation>> paragraphs;
        final ButtonWidget button;
        final Embed embed;

        /* Constructor */

        HelpCategory(TextureIcon icon, Translation title, Supplier<ArrayList<Translation>> paragraphs)
        {
            this.icon = icon;
            this.title = title;
            this.paragraphs = paragraphs;
            this.selector = FlagHolder.off();

            this.button = ButtonWidget.create(this.title::get)
                .alignLeft(3)
                .attach(this)
                .icon(this.icon)
                .extendWidthToEnd(HELP_CATEGORIES, 1)
                .backgroundRenderer(EditorOverlay::renderLeftTransparent)
                .onPress(HelpCategory::select)
                .build(HELP_CATEGORIES::addWidget);

            this.embed = Embed.create()
                .attach(this)
                .rightOf(HELP_SEPARATOR, 0)
                .extendWidthToScreenEnd(0)
                .extendHeightToScreenEnd(0)
                .borderThickness(0)
                .padding(4, 4, 2, 4)
                .build(HELP_OVERLAY::addWidget);

            this.makeInfoWidgets();

            this.embed.setInvisible();
        }

        /* Methods */

        ButtonWidget getButton()
        {
            return this.button;
        }

        Embed getEmbed()
        {
            return this.embed;
        }

        FlagHolder getSelector()
        {
            return this.selector;
        }

        void makeInfoWidgets()
        {
            this.embed.removeAllWidgets();

            ArrayList<TextWidget> textWidgets = new ArrayList<>();

            for (Translation paragraph : this.paragraphs.get())
            {
                textWidgets.add(TextWidget.create(paragraph)
                    .extendWidthToEnd(this.embed, 0)
                    .build(this.embed::addWidget));
            }

            ForEachWithPrevious.create(textWidgets)
                .forEach((prev, next) -> next.getBuilder().below(prev, PARAGRAPH_SPACING))
                .run();
        }

        boolean isFocused()
        {
            return this.selector.get();
        }

        /* Helpers */

        /**
         * @return A {@link Stream} of {@link HelpCategory} values.
         */
        static Stream<HelpCategory> stream()
        {
            return Arrays.stream(HelpCategory.values());
        }

        static Optional<HelpCategory> getFocused()
        {
            return HelpCategory.stream().filter(HelpCategory::isFocused).findFirst();
        }

        /**
         * On press instructions for a help section's button.
         *
         * @param button The help section {@link ButtonWidget}.
         */
        static void select(ButtonWidget button)
        {
            HelpCategory.stream().map(HelpCategory::getSelector).forEach(FlagHolder::disable);
            HelpCategory.stream().map(HelpCategory::getButton).forEach(ButtonWidget::setUnfocused);
            HelpCategory.stream().map(HelpCategory::getEmbed).forEach(Embed::setInvisible);

            CollectionUtil.fromCast(button.getAttachments(), HelpCategory.class)
                .findFirst()
                .ifPresent(HelpCategory::focus);
        }

        /**
         * Focus the given section.
         *
         * @param section The {@link HelpCategory} to focus.
         */
        static void focus(HelpCategory section)
        {
            section.getSelector().enable();
            section.getButton().setFocused();
            section.getEmbed().setVisible();
        }

        /**
         * Align all help sections in the order in which this enumeration has defined its values.
         */
        static void align()
        {
            ForEachWithPrevious.create(HelpCategory.stream().map(HelpCategory::getButton))
                .forEach((prev, next) -> next.getBuilder().below(prev, 0))
                .run();
        }
    }

    /**
     * Button background rendering for the section chooser buttons.
     */
    private static void renderLeftTransparent(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isReading = button.maybeHas(HelpCategory.getFocused().orElse(null)).isPresent();

        Color line = button.isHoveredOrFocused() || isReading ? Color.LIGHT_BLUE : Color.TRANSPARENT;
        Color fill = button.isHoveredOrFocused() || isReading ? Color.AZURE_WHITE.fromAlpha(0.2D) : Color.TRANSPARENT;

        if (isReading)
        {
            fill = button.isMouseOver(mouseX, mouseY) ? fill.brighten(0.2D) : fill.brighten(0.1D);
            line = button.isMouseOver(mouseX, mouseY) ? line.brighten(0.2D) : line.brighten(0.1D);
        }

        RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill);
        RenderUtil.fill(graphics, button.getX(), button.getY(), button.getX() + 1.5F, button.getEndY(), line);
    }

    /* Methods */

    /**
     * Open an overlay that displays instructions on how to use this falling block logo maker.
     */
    public static void howToUse()
    {
        HelpCategory.align();
        HelpCategory.stream().forEach(HelpCategory::makeInfoWidgets);

        if (HelpCategory.stream().map(HelpCategory::getSelector).noneMatch(FlagHolder::get))
            HelpCategory.stream().findFirst().ifPresent(HelpCategory::focus);
        else
            HelpCategory.getFocused().ifPresent(HelpCategory::focus);

        HELP_OVERLAY.getWidgets().forEach(DynamicWidget::setUnfocused);
        HELP_OVERLAY.open();
    }

    /**
     * Open an overlay that confirms or denies exiting of the editor screen if there are unsaved changes.
     *
     * @param screen The {@link FallingBlockEditorScreen} instance.
     */
    public static void areYouSure(FallingBlockEditorScreen screen)
    {
        int padding = 2;

        final Overlay overlay = Overlay.create(Lang.Affirm.QUIT_TITLE.plainCopy())
            .icon(Icons.SMALL_WARNING)
            .gradientBackground(Gradient.vertical(new Color(0x77500D, 220), new Color(0x2D1F05, 220)))
            .resizeUsingPercentage(0.5D)
            .resizeHeightForWidgets()
            .padding(padding)
            .build();

        TextWidget message = TextWidget.create(Lang.Affirm.QUIT_BODY)
            .extendWidthToScreenEnd(0)
            .centerAligned()
            .build(overlay::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.AZURE_WHITE)
            .height(1)
            .extendWidthToScreenEnd(0)
            .below(message, padding)
            .build(overlay::addWidget);

        Grid grid = Grid.create(overlay, 2)
            .below(separator, padding)
            .extendWidthToScreenEnd(0)
            .build(overlay::addWidget);

        ButtonWidget.create(Lang.Logo.QUIT_DISCARD).icon(Icons.RED_TRASH_CAN).onPress(() -> {
            overlay.close();
            Minecraft.getInstance().setScreen(screen.getParentScreen());
        }).build(grid::addCell);

        ButtonWidget.create(Lang.Affirm.QUIT_CANCEL)
            .icon(Icons.RIGHT_ARROW)
            .onPress(overlay::close)
            .build(grid::addCell)
            .setFocused();

        overlay.open();
    }

    /**
     * An error overlay that opens when the mod is not able to successfully read a falling block logo config file.
     *
     * @param onClose    A {@link Runnable} to run when the overlay is closed.
     * @param resetOffer If {@code true}, offers a checkbox that will reset the falling block logo config file when the
     *                   overlay is closed.
     */
    public static void couldNotReadConfig(final Runnable onClose, boolean resetOffer)
    {
        int padding = 2;

        final FlagHolder resetFlag = FlagHolder.off();

        final Overlay overlay = Overlay.create(Lang.Logo.READ_ERROR)
            .icon(Icons.SMALL_RED_WARNING)
            .gradientBackground(Gradient.vertical(new Color(0x632B2B, 220), new Color(0x281111, 220)))
            .resizeUsingPercentage(0.5D)
            .resizeHeightForWidgets()
            .padding(padding)
            .onClose(() -> {
                if (!resetFlag.get())
                    return;

                FallingBlockConfig.reset(resetFlag.get(), "corrupted");
                onClose.run();
            })
            .build();

        TextWidget message = TextWidget.create(Lang.Logo.READ_ERROR_INFO)
            .extendWidthToScreenEnd(0)
            .centerAligned()
            .build(overlay::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.AZURE_WHITE)
            .height(1)
            .extendWidthToScreenEnd(0)
            .below(message, padding)
            .build(overlay::addWidget);

        if (resetOffer)
        {
            ButtonWidget checkbox = ButtonTemplate.checkbox(Lang.EMPTY, resetFlag::get)
                .skipFocusOnClick()
                .below(separator, padding * 2)
                .onPress(resetFlag::toggle)
                .build(overlay::addWidget);

            TextWidget reset = TextWidget.create(Lang.Logo.RESET_CONFIG)
                .extendWidthToScreenEnd(0)
                .rightOf(checkbox, 0)
                .build(overlay::addWidget);

            separator = SeparatorWidget.create(Color.AZURE_WHITE)
                .height(1)
                .extendWidthToScreenEnd(0)
                .below(reset, padding * 2)
                .build(overlay::addWidget);
        }

        Grid grid = Grid.create(overlay, 2)
            .below(separator, padding)
            .extendWidthToScreenEnd(0)
            .build(overlay::addWidget);

        ButtonTemplate.openFolder(PathUtil.getLogoPath()).build(grid::addCell);

        ButtonWidget.create(Lang.Button.OKAY)
            .icon(Icons.GREEN_CHECK)
            .onPress(overlay::close)
            .build(grid::addCell)
            .setFocused();

        overlay.open();
    }

    /**
     * An error overlay that opens when the mod is not able to successfully read a falling block logo config file.
     */
    public static void couldNotReadConfig()
    {
        couldNotReadConfig(() -> { }, false);
    }
}

package mod.adrenix.nostalgic.client.gui.overlay.types.color;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.array.ArrayUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class PickerPalette
{
    /* Static */

    static final int SIZE = 7;
    static final ArrayList<Color> RECENT = new ArrayList<>(SIZE);
    static final ArrayList<Color> RANDOM = new ArrayList<>(SIZE);

    /* Fields */

    private final ColorPicker picker;

    /* Constructor */

    PickerPalette(ColorPicker picker)
    {
        this.picker = picker;

        if (RANDOM.isEmpty())
        {
            for (int i = 0; i < SIZE; i++)
                RANDOM.add(Color.getRandomColor());
        }
        else
        {
            for (int i = 0; i < SIZE; i++)
                RANDOM.get(i).set(Color.getRandomColor());
        }
    }

    /* Methods */

    /**
     * Create standard and random color palettes.
     */
    void build()
    {
        this.createStandardPalette();
        this.createRandomPalette();
        this.createRecentPalette();
    }

    /**
     * A palette of all Minecraft colors.
     */
    private void createStandardPalette()
    {
        int padding = ColorPicker.PADDING - 1;

        // @formatter:off

        ButtonWidget darkRed = this.getPaletteColorFactory(ChatFormatting.DARK_RED).build();
        ButtonWidget red = this.getPaletteColorFactory(ChatFormatting.RED).rightOf(darkRed, padding).build();
        ButtonWidget gold = this.getPaletteColorFactory(ChatFormatting.GOLD).rightOf(red, padding).build();
        ButtonWidget yellow = this.getPaletteColorFactory(ChatFormatting.YELLOW).rightOf(gold, padding).build();
        ButtonWidget darkGreen = this.getPaletteColorFactory(ChatFormatting.DARK_GREEN).rightOf(yellow, padding).build();
        ButtonWidget green = this.getPaletteColorFactory(ChatFormatting.GREEN).rightOf(darkGreen, padding).build();
        ButtonWidget aqua = this.getPaletteColorFactory(ChatFormatting.AQUA).rightOf(green, padding).build();
        ButtonWidget darkAqua = this.getPaletteColorFactory(ChatFormatting.DARK_AQUA).rightOf(aqua, padding).build();
        ButtonWidget darkBlue = this.getPaletteColorFactory(ChatFormatting.DARK_BLUE).below(darkRed, padding).build();
        ButtonWidget blue = this.getPaletteColorFactory(ChatFormatting.BLUE).rightOf(darkBlue, padding).build();
        ButtonWidget lightPurple = this.getPaletteColorFactory(ChatFormatting.LIGHT_PURPLE).rightOf(blue, padding).build();
        ButtonWidget darkPurple = this.getPaletteColorFactory(ChatFormatting.DARK_PURPLE).rightOf(lightPurple, padding).build();
        ButtonWidget white = this.getPaletteColorFactory(ChatFormatting.WHITE).rightOf(darkPurple, padding).build();
        ButtonWidget gray = this.getPaletteColorFactory(ChatFormatting.GRAY).rightOf(white, padding).build();
        ButtonWidget darkGray = this.getPaletteColorFactory(ChatFormatting.DARK_GRAY).rightOf(gray, padding).build();
        ButtonWidget black = this.getPaletteColorFactory(ChatFormatting.BLACK).rightOf(darkGray, padding).build();

        // @formatter:on

        this.picker.group.palette.addWidget(darkRed);
        this.picker.group.palette.addWidget(red);
        this.picker.group.palette.addWidget(gold);
        this.picker.group.palette.addWidget(yellow);
        this.picker.group.palette.addWidget(darkGreen);
        this.picker.group.palette.addWidget(green);
        this.picker.group.palette.addWidget(aqua);
        this.picker.group.palette.addWidget(darkAqua);
        this.picker.group.palette.addWidget(darkBlue);
        this.picker.group.palette.addWidget(blue);
        this.picker.group.palette.addWidget(lightPurple);
        this.picker.group.palette.addWidget(darkPurple);
        this.picker.group.palette.addWidget(white);
        this.picker.group.palette.addWidget(gray);
        this.picker.group.palette.addWidget(darkGray);
        this.picker.group.palette.addWidget(black);
    }

    /**
     * Align two buttons in the random color palette.
     *
     * @param prev A {@link ButtonWidget} instance.
     * @param next A {@link ButtonWidget} instance.
     */
    private void alignRandom(ButtonWidget prev, ButtonWidget next)
    {
        next.getBuilder().rightOf(prev, ColorPicker.PADDING - 1);
    }

    /**
     * Align two buttons in the recent color palette.
     *
     * @param prev A {@link ButtonWidget} instance.
     * @param next A {@link ButtonWidget} instance.
     */
    private void alignRecent(ButtonWidget prev, ButtonWidget next)
    {
        next.getBuilder().rightOf(prev, ColorPicker.PADDING - 1);
    }

    /**
     * Create a random color palette.
     */
    private void createRandomPalette()
    {
        ArrayList<ButtonWidget> random = new ArrayList<>(SIZE);

        for (int i = 0; i < SIZE; i++)
            random.add(this.getColorSquareFactory(RANDOM.get(i)).build(this.picker.group.random::addWidget));

        ForEachWithPrevious.create(random).forEach(this::alignRandom).run();

        IconTemplate.close()
            .onPress(() -> {
                for (int i = 0; i < SIZE; i++)
                    RANDOM.get(i).set(Color.getRandomColor());
            })
            .tooltip(Lang.Colorize.RANDOMIZE, 500L, TimeUnit.MILLISECONDS)
            .hideTooltipAfter(3L, TimeUnit.SECONDS)
            .alignVerticalTo(CollectionUtil.last(random).orElse(null), 1)
            .rightOf(CollectionUtil.last(random).orElse(null), ColorPicker.PADDING)
            .build(this.picker.group.random::addWidget);
    }

    /**
     * Create a recent color palette.
     */
    private void createRecentPalette()
    {
        ArrayList<ButtonWidget> recent = new ArrayList<>(SIZE);

        for (int i = 0; i < SIZE; i++)
        {
            recent.add(this.getRecentColorFactory(ArrayUtil.get(RECENT, i).orElse(Color.TRANSPARENT))
                .build(this.picker.group.recent::addWidget));
        }

        ForEachWithPrevious.create(recent).forEach(this::alignRecent).run();

        IconTemplate.close()
            .onPress(() -> {
                for (int i = 0; i < SIZE; i++)
                    RECENT.forEach(color -> color.set(Color.TRANSPARENT));
            })
            .tooltip(Lang.Colorize.CLEAR, 500L, TimeUnit.MILLISECONDS)
            .hideTooltipAfter(4L, TimeUnit.SECONDS)
            .alignVerticalTo(CollectionUtil.last(recent).orElse(null), 1)
            .rightOf(CollectionUtil.last(recent).orElse(null), ColorPicker.PADDING)
            .build(this.picker.group.recent::addWidget);
    }

    /**
     * The default button factory for color squares.
     *
     * @param color A {@link Color} instance.
     * @return A button factory builder instance.
     */
    private ButtonBuilder getColorSquareFactory(Color color)
    {
        int size = 11;

        return ButtonWidget.create()
            .size(size)
            .onPress(() -> this.picker.color.set(color))
            .renderer((button, graphics, mouseX, mouseY, partialTick) -> {
                graphics.pose().pushPose();
                graphics.pose().translate(button.getX(), button.getY(), 0.0D);

                boolean isHighlighted = button.isFocused() || this.picker.color.equals(color);
                int border = isHighlighted ? Color.LIGHT_BLUE.get() : Color.GRAY.get();

                if (button.isMouseOver(mouseX, mouseY))
                    border = Color.WHITE.get();

                RenderUtil.beginBatching();
                RenderUtil.outline(graphics, 0.0F, 0.0F, size, size, border);

                if (color.isTransparent())
                {
                    int width = size - 2;
                    int height = size - 2;

                    for (int row = 1; row <= height; row++)
                    {
                        Color primary = MathUtil.isOdd(row) ? Color.GRAY : Color.WHITE;
                        Color secondary = MathUtil.isOdd(row) ? Color.WHITE : Color.GRAY;

                        for (int i = 1; i <= width; i++)
                            RenderUtil.fill(graphics, i, row, i + 1, row + 1, MathUtil.isEven(i) ? primary : secondary);
                    }
                }

                RenderUtil.fill(graphics, 1.0F, 1.0F, size - 1, size - 1, color.get());
                RenderUtil.endBatching();

                graphics.pose().popPose();
            });
    }

    /**
     * Creates a new palette button factory based on the given Minecraft chat formatting enumeration.
     *
     * @param formatting A vanilla chat formatting enumeration.
     * @return A button factory that can be chained onto for further customization.
     */
    private ButtonBuilder getPaletteColorFactory(ChatFormatting formatting)
    {
        return this.getColorSquareFactory(Color.fromFormatting(formatting))
            .tooltip(Component.literal(TextUtil.toTitleCase(formatting.getName())), 750L, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a new recent color button factory based on the given starting {@link Color} instance.
     *
     * @param color A {@link Color} instance.
     * @return A button factory that can be chained onto for further customization.
     */
    private ButtonBuilder getRecentColorFactory(Color color)
    {
        return this.getColorSquareFactory(color).onPress(() -> {
            if (color.isPresent())
                this.picker.color.set(color);
        });
    }
}

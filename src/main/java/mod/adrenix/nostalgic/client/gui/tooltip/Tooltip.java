package mod.adrenix.nostalgic.client.gui.tooltip;

import com.google.common.collect.Lists;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Tooltip {
    private static int mouseX = -1;
    private static int mouseY = -1;
    private static boolean visible = true;

    @Nullable
    private static LayoutElement focused = null;
    @Nullable
    private static Component message = null;
    @Nullable
    private static List<Component> info = null;
    @Nullable
    private static List<Component> multiline = null;

    /**
     * Change whether the tooltip can be rendered.
     *
     * @param state A boolean flag.
     */
    public static void setVisible(boolean state) {
        Tooltip.visible = state;
    }

    /**
     * Manually define where the tooltip should be rendered at.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     */
    public static void setMousePosition(int mouseX, int mouseY) {
        Tooltip.mouseX = mouseX;
        Tooltip.mouseY = mouseY;
    }

    /**
     * Set a tooltip to render at the end of the gui rendering pass.
     *
     * @param tooltip The tooltip component to render.
     */
    public static void setTooltip(Component tooltip) {
        Tooltip.multiline = null;
        Tooltip.message = tooltip;
    }

    /**
     * Set a multiline tooltip to render at the end of the gui rendering pass.
     *
     * @param tooltip The multiline tooltip component to render.
     */
    public static void setListTooltip(List<Component> tooltip) {
        Tooltip.multiline = tooltip;
        Tooltip.message = null;
    }

    /**
     * Set an information tooltip to render when the shift key is held during the rendering cycle. This will be appended
     * to the current tooltip box being rendered to the screen.
     *
     * @param info A {@link Component} to apply to the current tooltip box when the shift key is held.
     * @see #setInfo(List)
     */
    public static void setInfo(Component info) {
        Tooltip.info = List.of(info);
    }

    /**
     * Set an information tooltip to render when the shift key is held during the rendering cycle. This will be appended
     * to the current tooltip box being rendered to the screen.
     *
     * @param info A {@link List} of {@link Component}s to apply to the current tooltip box when the shift key is held.
     * @see #setInfo(Component)
     */
    public static void setInfo(List<Component> info) {
        Tooltip.info = info;
    }

    /**
     * Set the tooltip relative to the given widget's position.
     *
     * @param widget The {@link DynamicWidget} to set the tooltip relative to.
     */
    public static void setRelativeToIfFocused(DynamicWidget<?, ?> widget) {
        Font font = widget.getScreen().font;
        Tooltip.focused = widget;

        if (widget.isFocused()) {
            Tooltip.mouseX = widget.getX() - 9;
            Tooltip.mouseY = widget.getY() - getHeight(font) - 1;
        }
    }

    /**
     * @param font The {@link Font} to get the height of the text.
     * @return Get the height of the tooltip box that will be rendered.
     */
    public static int getHeight(Font font) {
        List<Component> tooltip = get();

        List<ClientTooltipComponent> clientTooltips = Lists.transform(tooltip, Component::getVisualOrderText)
                .stream()
                .map(ClientTooltipComponent::create)
                .toList();

        return (tooltip.size() == 1 ? -2 : 0) - 8 + clientTooltips.stream()
                .mapToInt(component -> component.getHeight(font))
                .sum();
    }

    /**
     * @return The {@link List} of {@link Component} instances to be rendered.
     */
    public static List<Component> get() {
        List<Component> tooltip = new ArrayList<>();

        if (Tooltip.message != null)
            tooltip.add(Tooltip.message);
        else if (Tooltip.multiline != null)
            tooltip.addAll(Tooltip.multiline);

        if (tooltip.size() > 1)
            tooltip.add(Component.empty());

        tooltip.add(Lang.Tooltip.HIDE.withStyle(ChatFormatting.GRAY));

        if (Tooltip.info != null) {
            if (false/*Screen.hasShiftDown()*/) { //TODO
                tooltip.removeLast();
                tooltip.addAll(Tooltip.info);
            } else
                tooltip.add(Lang.Tooltip.SHIFT.withStyle(ChatFormatting.GRAY));
        }

        return tooltip;
    }

    /**
     * Render a cached tooltip if it exists.
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    public static void render(Screen screen, GuiGraphicsExtractor graphics) {
        if (!Tooltip.visible)
            return;

        boolean isNotManager = ClassUtil.isNotInstanceOf(screen, TooltipManager.class);

        if (/*Screen.hasControlDown()*/false || isNotManager) { //TODO
            if (isNotManager)
                Tooltip.focused = null;

            return;
        }

        TooltipManager manager = (TooltipManager) screen;

        manager.setTooltipUsingMouse();

        if (Tooltip.message == null && Tooltip.multiline == null)
            manager.setTooltipUsingFocused();

        manager.resetTooltipTimers();

        if (Tooltip.message == null && Tooltip.multiline == null) {
            Tooltip.focused = null;
            return;
        }

        List<FormattedCharSequence> tooltips = Tooltip.get()
                .stream()
                .map(Component::getVisualOrderText)
                .collect(Collectors.toCollection(ArrayList::new));

        ClientTooltipPositioner position = (screenWidth, screenHeight, mouseX, mouseY, tooltipWidth, tooltipHeight) -> {
            Vector2i pos = new Vector2i(mouseX, mouseY).add(12, -12);

            if (Tooltip.focused != null) {
                pos.x = Tooltip.focused.getX() + 3;
                pos.y = Tooltip.focused.getY() + Tooltip.focused.getHeight() + 5;

                if (pos.x + tooltipWidth + 5 > screenWidth)
                    pos.x = Tooltip.focused.getX() + Tooltip.focused.getWidth() - tooltipWidth - 4;

                if (pos.y + tooltipHeight + 5 > screenHeight)
                    pos.y = Tooltip.focused.getY() - tooltipHeight - 5;
            }

            if (pos.x + tooltipWidth + 5 > screenWidth)
                pos.x = screenWidth - tooltipWidth - 5;

            if (pos.y + tooltipHeight + 5 > screenHeight)
                pos.y = screenHeight - tooltipHeight - 5;

            pos.x = Math.max(5, pos.x);
            pos.y = Math.max(5, pos.y);

            return pos;
        };

        graphics.setTooltipForNextFrame(GuiUtil.font(), tooltips, position, Tooltip.mouseX, Tooltip.mouseY, true);

        Tooltip.focused = null;
        Tooltip.multiline = null;
        Tooltip.info = null;
        Tooltip.message = null;
    }
}

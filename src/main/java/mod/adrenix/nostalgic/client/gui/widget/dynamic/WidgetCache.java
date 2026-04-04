package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class WidgetCache {
    /* Static */

    public final CacheValue<Integer> x;

    /* Fields */
    public final CacheValue<Integer> y;
    public final CacheValue<Integer> width;
    public final CacheValue<Integer> height;
    public final CacheValue<Boolean> active;
    public final CacheValue<Boolean> visible;
    public final CacheValue<Float> scaleHeight;
    public final CacheValue<Float> scaleWidth;
    public final ScreenCache screen;

    private WidgetCache(DynamicWidget<?, ?> widget) {
        this.x = CacheValue.create(widget::getX);
        this.y = CacheValue.create(widget::getY);
        this.width = CacheValue.create(widget::getWidth);
        this.height = CacheValue.create(widget::getHeight);
        this.active = CacheValue.create(widget::isActive);
        this.visible = CacheValue.create(widget::isVisible);
        this.scaleWidth = CacheValue.create(widget::getScaleWidth);
        this.scaleHeight = CacheValue.create(widget::getScaleHeight);
        this.screen = new ScreenCache(widget);
    }

    /* Constructor */

    /**
     * Create a new layout cache from a dynamic widget.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A new {@link WidgetCache} instance.
     */
    public static WidgetCache from(DynamicWidget<?, ?> widget) {
        return new WidgetCache(widget);
    }

    /* Methods */

    public void update() {
        this.x.update();
        this.y.update();
        this.width.update();
        this.height.update();
        this.active.update();
        this.visible.update();
        this.screen.update();
    }

    public boolean isAnyExpired() {
        boolean isWidgetExpired = CacheValue.isAnyExpired(this.x, this.y, this.width, this.height, this.active, this.visible, this.scaleWidth, this.scaleHeight);
        boolean isScreenExpired = CacheValue.isAnyExpired(this.screen.x, this.screen.y, this.screen.width, this.screen.height);

        return isWidgetExpired || isScreenExpired;
    }

    public boolean isScaleExpired() {
        return this.getScaleHeight().isExpired() || this.getScaleWidth().isExpired();
    }

    public CacheValue<Integer> getX() {
        return this.x;
    }

    public CacheValue<Integer> getY() {
        return this.y;
    }

    public CacheValue<Integer> getWidth() {
        return this.width;
    }

    public CacheValue<Integer> getHeight() {
        return this.height;
    }

    public CacheValue<Boolean> getActive() {
        return this.active;
    }

    public CacheValue<Boolean> getVisible() {
        return this.visible;
    }

    public CacheValue<Float> getScaleHeight() {
        return this.scaleHeight;
    }

    public CacheValue<Float> getScaleWidth() {
        return this.scaleWidth;
    }

    public ScreenCache getScreen() {
        return this.screen;
    }

    public static class ScreenCache {
        public final CacheValue<Integer> x;
        public final CacheValue<Integer> y;
        public final CacheValue<Integer> width;
        public final CacheValue<Integer> height;
        public final Supplier<Screen> screen;

        private ScreenCache(DynamicWidget<?, ?> widget) {
            this.width = CacheValue.create(widget::getScreenWidth);
            this.height = CacheValue.create(widget::getScreenHeight);
            this.screen = widget::getScreen;

            this.x = CacheValue.create(() -> {
                if (widget.getScreen() instanceof Overlay overlay)
                    return overlay.getInsideX();

                return 0;
            });

            this.y = CacheValue.create(() -> {
                if (widget.getScreen() instanceof Overlay overlay)
                    return overlay.getInsideY();

                return 0;
            });
        }

        public void update() {
            this.x.update();
            this.y.update();
            this.width.update();
            this.height.update();
        }

        public CacheValue<Integer> getX() {
            return this.x;
        }

        public CacheValue<Integer> getY() {
            return this.y;
        }

        public CacheValue<Integer> getWidth() {
            return this.width;
        }

        public CacheValue<Integer> getHeight() {
            return this.height;
        }
    }
}

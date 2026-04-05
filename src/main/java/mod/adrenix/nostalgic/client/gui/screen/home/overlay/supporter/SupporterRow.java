package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import mod.adrenix.nostalgic.client.gui.GearSpinner;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.gui.LinkUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.component.ResolvableProfile;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

class SupporterRow {
    /* Fields */

    private final LinkedHashSet<DynamicWidget<?, ?>> widgets;
    private final GithubJson.Supporter supporter;
    private final Row row;
    private final RowList rowList;
    private final Overlay overlay;
    private final DynamicWidget<?, ?> face;
    private final PlayerText text;
    private final String name;

    /* Constructor */

    SupporterRow(SupporterOverlay overlay, String name, GithubJson.Supporter supporter) {
        this.name = name;
        this.supporter = supporter;
        this.overlay = overlay.overlay;
        this.rowList = overlay.rowList;
        this.row = Row.create(overlay.rowList).build();
        this.widgets = new LinkedHashSet<>();

        DynamicBuilder<?, ?> builder;

        //TODO: tidy this up, and maybe move this (or parts of this) elsewhere?
        if (supporter.uuid != null) {
            BigInteger uuidVal = new BigInteger(supporter.uuid, 16);
            long lsb = uuidVal.longValue();
            long msb = uuidVal.shiftRight(Long.SIZE).longValue();

            UUID playerUUID = new UUID(msb, lsb);

            PlayerSkinRenderCache skinCache = Minecraft.getInstance().playerSkinRenderCache();
            ResolvableProfile profile = ResolvableProfile.createUnresolved(playerUUID);
            Supplier<PlayerSkinRenderCache.RenderInfo> renderInfo = skinCache.createLookup(profile);

            builder = BlankWidget.create()
                    .size(8)
                    .renderer((widget, graphics, mouseX, mouseY, partialTick) ->
                            PlayerFaceExtractor.extractRenderState(graphics, renderInfo.get().playerSkin(),
                                    widget.getX(), widget.getY(), 8));
        } else {
            builder = IconWidget.create(Icons.STEVE).size(8).emptySize(8);
        }

        this.face = builder
                .build(List.of(this.row::addWidget, this.widgets::add));

        this.text = new PlayerText(Holder.create(this.face), NullableHolder.empty(), NullableHolder.empty());

        this.setPlayerName();
        this.setBackground();

        if (this.supporter.member) {
            IconWidget.create(Icons.KOFI)
                    .size(GuiUtil.textHeight())
                    .tooltip(Lang.Home.KOFI_MEMBER, 45, 500L, TimeUnit.MILLISECONDS)
                    .alignVerticalTo(this.face, -1)
                    .rightOf(this.text.last().orElse(this.text.name().get()), 4)
                    .build(this.row::addWidget);
        }

        if (this.supporter.description != null) {
            TextWidget.create(this.supporter.description)
                    .extendWidthToEnd(this.row, 0)
                    .alignFlushTo(this.text.first().orElse(this.text.name().get()))
                    .below(this.text.first().orElse(this.text.name().get()), 2)
                    .build(List.of(this.row::addWidget, this.widgets::add));
        }

        if (this.supporter.twitter != null)
            this.makeLink(this.supporter.twitter, Icons.TWITTER);

        if (this.supporter.youtube != null)
            this.makeLink(this.supporter.youtube, Icons.YOUTUBE);

        if (this.supporter.twitch != null)
            this.makeLink(this.supporter.twitch, Icons.TWITCH);

        for (String link : this.supporter.links)
            this.makeLink(link, Icons.CIRCLE_EARTH);

        this.rowList.addBottomRow(this.row);
    }

    /* Methods */

    /**
     * Make a new {@link TextWidget} link using the given arguments.
     *
     * @param link    The URL as a string.
     * @param texture A {@link TextureIcon} instance.
     */
    private void makeLink(String link, TextureIcon texture) {
        IconWidget icon = IconWidget.create(texture)
                .size(GuiUtil.textHeight())
                .alignFlushTo(this.text.first().orElse(this.text.name().get()))
                .belowAll(this.widgets, 2)
                .build(List.of(this.row::addWidget, this.widgets::add));

        TextWidget.create(link)
                .rightOf(icon, 4)
                .alignVerticalTo(icon)
                .onPress(LinkUtil.onPress(link), Color.FRENCH_SKY_BLUE)
                .useTextWidth(this.rowList::getRowEndX)
                .build(List.of(this.row::addWidget, this.widgets::add));
    }

    /* Name Renderers */

    /**
     * Set the {@link TextWidget}(s) that represents a supporter's player name.
     */
    private void setPlayerName() {
        switch (this.name) {
            case "Adrenix" -> this.setTextAdrenix();
            case "PoeticRainbow" -> this.setTextPoeticRainbow();
            case "Captain_3" -> this.setTextCaptain3();
            default -> TextWidget.create(new Color(this.supporter.color).apply(this.name))
                    .useTextWidth()
                    .rightOf(this.face, 4)
                    .alignVerticalTo(this.face)
                    .build(List.of(this.row::addWidget, this.text.name()::set));
        }
    }

    /**
     * Set a custom {@link TextWidget} renderer for the mod's creator.
     */
    private void setTextAdrenix() {
        Animation animation = Animate.linear(2L, TimeUnit.SECONDS);

        IntSupplier color = () -> {
            animation.playOrRewind();

            return Color.HSBtoRGB(360.0F, 1.0F, Mth.lerp((float) animation.getValue(), 0.4F, 1.0F));
        };

        TextWidget text = TextWidget.create(this.name)
                .useTextWidth()
                .rightOf(this.face, 4)
                .alignVerticalTo(this.face)
                .color(color)
                .build(List.of(this.row::addWidget, this.text.name()::set));

        BlankWidget.create()
                .size(10, 9)
                .rightOf(text, 2)
                .alignVerticalTo(text)
                .tooltip(Lang.Home.MOD_CREATOR, 45, 500L, TimeUnit.MILLISECONDS)
                .renderer((widget, graphics, mouseX, mouseY, partialTick) -> {
                    GearSpinner.getInstance().extractRenderState(graphics, 0.019F, widget.getX(), widget.getY() - 1);

                    String splash = "N.T";
                    float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
                    scale = scale * 4.0F / (float) (GuiUtil.font().width(splash));

                    graphics.pose().pushMatrix();
                    graphics.pose().translate(widget.getX() + 3, widget.getY() + 5);
                    graphics.pose().rotate(-20.0F * Mth.DEG_TO_RAD);
                    graphics.pose().scale(scale, scale);

                    DrawText.begin(graphics, splash).color(Color.YELLOW).draw();

                    graphics.pose().popMatrix();
                })
                .build(this.row::addWidget);

        this.overlay.runOnClose(animation::stop);
    }

    /**
     * Set a custom {@link TextWidget} renderer for PoeticRainbow.
     */
    private void setTextPoeticRainbow() {
        Animation animation = Animate.linear(2L, TimeUnit.SECONDS);

        CollectionUtil.forLoop(Arrays.stream(this.name.split("")), (letter, index) -> {
            final float offset = index / (float) this.name.length();

            TextWidget widget = TextWidget.create(letter)
                    .rightOf(this.text.last().orElse(this.face), this.text.last().isEmpty() ? 4 : 0)
                    .alignVerticalTo(this.face)
                    .width(GuiUtil.font().width(letter))
                    .color(() -> {
                        if (animation.isFinished()) {
                            animation.reset();
                            animation.tick();
                        }

                        animation.play();

                        float degrees = 360.0F * (1.0F - (float) animation.getValue() + offset);
                        float hsb = MathUtil.normalizeInRange(degrees, 0.0F, 360.0F);

                        return Color.HSBtoRGB(hsb / 360.0F, 1.0F, 1.0F);
                    })
                    .build(List.of(this.row::addWidget, this.text.last()::set));

            if (index == 0)
                this.text.first().set(widget);
        });

        this.overlay.runOnClose(animation::stop);
    }

    /**
     * Set a custom {@link TextWidget} renderer for Captain_3.
     */
    private void setTextCaptain3() {
        Animation animation = Animate.linear(100L, TimeUnit.MILLISECONDS);
        CycleIndex cycleIndex = new CycleIndex(this.name.split(""), true);

        CollectionUtil.forLoop(Arrays.stream(this.name.split("")), (letter, index) -> {
            TextWidget widget = TextWidget.create(new Color(this.supporter.color).apply(letter))
                    .rightOf(this.text.last().orElse(this.face), this.text.last().isEmpty() ? 4 : 0)
                    .alignVerticalTo(this.face)
                    .width(GuiUtil.font().width(letter))
                    .posY(() -> {
                        if (animation.isFinished()) {
                            animation.reset();
                            cycleIndex.cycle();
                        }

                        animation.play();

                        return this.face.getY() + (cycleIndex.get() == index ? -1 : 0);
                    })
                    .build(List.of(this.row::addWidget, this.text.last()::set));

            if (index == 0)
                this.text.first().set(widget);
        });

        this.overlay.runOnClose(animation::stop);
    }

    /* Background Renderers */

    /**
     * Set any custom row backgrounds for supporters.
     */
    private void setBackground() {
        switch (this.name) {
            case "Adrenix" -> this.setBackgroundAdrenix();
            case "SalC1" -> this.setBackgroundSalC1();
        }
    }

    /**
     * Custom row background for the mod's developer.
     */
    private void setBackgroundAdrenix() {
        final int posX = -4;
        final int offset = 4;
        final int minRand = 8;
        final int maxRand = 18;

        for (int i = 0; i < 6; i++) {
            Animation animation = Animate.easeInOutCircular(MathUtil.randomInt(minRand, maxRand), TimeUnit.SECONDS);
            FloatSupplier random = () -> MathUtil.randomInt(0, 100) / 100.0F;

            Holder<Float> randX = Holder.create(random.getAsFloat());
            Holder<Float> randY = Holder.create(random.getAsFloat());

            IntSupplier posY = () -> (int) (randY.get() * this.row.getHeight()) + this.face.getY() - 2;

            BlankWidget.create()
                    .size(3, 1)
                    .posX(posX)
                    .posY(posY)
                    .renderer((widget, graphics, mouseX, mouseY, partialTick) -> {
                        if (animation.isFinished())
                            animation.reset();

                        animation.play();

                        int x = (int) Mth.lerp(animation.getValue(), posX, this.rowList.getRowEndX() + offset);
                        int y = posY.getAsInt();

                        widget.pos(x, y);

                        if (widget.getX() >= this.rowList.getRowEndX() + offset) {
                            randX.set(random.getAsFloat());
                            randY.set(random.getAsFloat());

                            widget.pos(posX, posY.getAsInt());

                            animation.setDuration(MathUtil.randomInt(minRand, maxRand), TimeUnit.SECONDS);
                            animation.stop();

                            return;
                        }

                        int x0 = widget.getX();
                        int y0 = widget.getY();
                        int x1 = widget.getEndX();
                        int y1 = widget.getEndY();

                        graphics.fill(x0, y0, x1 + 2, y1, Color.RED.fromAlpha(0.3F).get());
                    })
                    .build(this.row::addWidget);

            this.overlay.runOnClose(animation::stop);
        }
    }

    /**
     * Custom row background for SalC1.
     */
    private void setBackgroundSalC1() {
        Animation animation = Animate.linear(2L, TimeUnit.SECONDS);

        for (int i = 0; i < 24; i++) {
            FloatSupplier random = () -> MathUtil.randomInt(0, 100) / 100.0F;

            Holder<Float> randX = Holder.create(random.getAsFloat());
            Holder<Float> randY = Holder.create(random.getAsFloat());

            IntSupplier posX = () -> (int) (randX.get() * this.rowList.getRowWidth()) + this.face.getX() - 4;
            IntSupplier posY = () -> (int) (randY.get() * this.row.getHeight()) + this.face.getY() - 2;

            BlankWidget.create().size(1).pos(posX, posY).renderer((widget, graphics, mouseX, mouseY, partialTick) -> {
                if (animation.isFinished()) {
                    if (animation.wentBackward())
                        animation.play();

                    if (animation.wentForward())
                        animation.rewind();
                }

                if (animation.getValue() == 1.0D) {
                    randX.set(random.getAsFloat());
                    randY.set(random.getAsFloat());

                    widget.pos(posX.getAsInt(), posY.getAsInt());
                }

                int x0 = widget.getX();
                int y0 = widget.getY();
                int x1 = widget.getEndX();
                int y1 = widget.getEndY();

                graphics.fill(x0, y0, x1, y1, Color.WHITE.fromAlpha(1.0F - (float) animation.getValue()).get());
            }).build(this.row::addWidget);
        }

        this.overlay.runOnClose(animation::stop);
    }
}

package mod.adrenix.nostalgic.util.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.util.client.gui.GuiOffset;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import mod.adrenix.nostalgic.util.common.sprite.GuiSprite;
import mod.adrenix.nostalgic.util.common.sprite.GuiSpriteScaling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class RenderUtil
{
    /*
        Batching

        Some user interfaces may have many fills, lines, and texture rendering calls during each render pass. Drawing
        each of these elements one at a time is expensive and will result in a significant drop of FPS. To get around
        this issue, batching can be used at different points throughout a render pass to reduce the number of draw
        calls. For example, the opacity slider in the color picker overlay calls the fill method 160+ times to draw the
        opacity background. Without batching, the FPS hit is significant, but with batching enabled, there is no
        reduction in FPS since each fill is batched and drawn as a single piece of geometry.
    */

    private static boolean isPausedBatching = false;
    private static boolean isBatching = false;
    private static int fillZOffset = 0;
    private static int batchIndex = 0;
    private static TextureLayer layer = TextureLayer.DEFAULT;
    private static TextureLocation texture;
    private static RenderType renderType;

    private static final Tesselator MOD_TESSELATOR = new Tesselator(1536);
    private static final ArrayDeque<Runnable> DEFERRED_QUEUE = new ArrayDeque<>();
    private static final ArrayDeque<Scissor> SCISSOR_QUEUE = new ArrayDeque<>();
    private static final ArrayDeque<LineBuffer> LINE_QUEUE = new ArrayDeque<>();
    private static final HashSet<TextureLayer> TEXTURE_LAYERS = new HashSet<>();
    private static final ArrayDeque<ItemBuffer> ITEM_MODEL_QUEUE = new ArrayDeque<>();
    private static final ArrayDeque<ItemBuffer> BLOCK_MODEL_QUEUE = new ArrayDeque<>();
    private static final ArrayDeque<Consumer<BufferBuilder>> FILL_VERTICES = new ArrayDeque<>();
    private static final MultiBufferSource.BufferSource FONT_BATCH = MultiBufferSource.immediate(new BufferBuilder(1536));
    private static final MultiBufferSource.BufferSource FONT_IMMEDIATE = MultiBufferSource.immediate(new BufferBuilder(1536));

    /**
     * Offset the z-position of the matrix that renders fill calls. Useful if it is known that all fills are background
     * elements.
     *
     * @param offset The position matrix z-offset.
     */
    @PublicAPI
    public static void setFillZOffset(int offset)
    {
        fillZOffset = offset;
    }

    /**
     * @return Whether the render utility is currently batching draw calls.
     */
    @PublicAPI
    public static boolean isBatching()
    {
        return isBatching;
    }

    /**
     * Temporarily pause batching.
     */
    @PublicAPI
    public static void pauseBatching()
    {
        isPausedBatching = true;
        isBatching = false;
    }

    /**
     * Resume paused batching.
     */
    @PublicAPI
    public static void resumeBatching()
    {
        isPausedBatching = false;
        isBatching = true;
    }

    /**
     * Get an appropriate buffer source based on whether this utility is batching draw calls.
     *
     * @return A {@link MultiBufferSource.BufferSource} instance for rendering text.
     */
    @PublicAPI
    public static MultiBufferSource.BufferSource fontBuffer()
    {
        if (isBatching)
            return FONT_BATCH;

        return FONT_IMMEDIATE;
    }

    /* Scissoring */

    private enum ScissorType
    {
        NORMAL,
        ZONE;

        public static boolean isZone(Scissor scissor)
        {
            return scissor.scissorType.equals(ZONE);
        }
    }

    private record Scissor(ScissorType scissorType, int startX, int startY, int endX, int endY)
    {
        public void enable()
        {
            SCISSOR_QUEUE.stream().filter(ScissorType::isZone).findFirst().ifPresentOrElse(zone -> {
                int x0 = Mth.clamp(this.startX, zone.startX, zone.endX);
                int y0 = Mth.clamp(this.startY, zone.startY, zone.endY);
                int x1 = Mth.clamp(this.endX, zone.startX, zone.endX);
                int y1 = Mth.clamp(this.endY, zone.startY, zone.endY);

                GuiUtil.enableScissor(x0, y0, x1, y1);
            }, () -> GuiUtil.enableScissor(this.startX, this.startY, this.endX, this.endY));
        }
    }

    /**
     * Start a new scissoring section. Note that this type of scissoring push will be overridden by a scissoring
     * {@link ScissorType#ZONE}. Zones are made using {@link #pushZoneScissor(int, int, int, int)}. See that method's
     * documentation for more details about scissoring zones.
     *
     * @param startX The x-position of where scissoring starts.
     * @param startY The y-position of where scissoring starts.
     * @param endX   The x-position of where scissoring ends.
     * @param endY   The y-position of where scissoring ends.
     * @see #pushZoneScissor(int, int, int, int)
     * @see #popScissor()
     */
    @PublicAPI
    public static void pushScissor(int startX, int startY, int endX, int endY)
    {
        Scissor scissor = new Scissor(ScissorType.NORMAL, startX, startY, endX, endY);

        SCISSOR_QUEUE.push(scissor);
        scissor.enable();
    }

    /**
     * Start a new scissoring section.
     *
     * @param rectangle The {@link Rectangle} bounds of the scissoring section.
     * @see #pushScissor(int, int, int, int)
     */
    @PublicAPI
    public static void pushScissor(Rectangle rectangle)
    {
        pushScissor(rectangle.startX(), rectangle.startY(), rectangle.endX(), rectangle.endY());
    }

    /**
     * Start a new scissoring zone. The first zone found in the scissoring queue will override any normal push by
     * {@link #pushScissor(int, int, int, int)}. Scissoring zones will ensure rendered content stays within the zone's
     * boundary while also allowing for additional scissoring sections within the zone. There is not a special popping
     * method for scissoring zones, use {@link #popScissor()}.
     *
     * @param startX The x-position of where the scissoring zone starts.
     * @param startY The y-position of where the scissoring zone starts.
     * @param endX   The x-position of where the scissoring zone ends.
     * @param endY   The y-position of where the scissoring zone ends.
     * @see #pushScissor(int, int, int, int)
     * @see #popScissor()
     */
    @PublicAPI
    public static void pushZoneScissor(int startX, int startY, int endX, int endY)
    {
        SCISSOR_QUEUE.push(new Scissor(ScissorType.ZONE, startX, startY, endX, endY));
        GuiUtil.enableScissor(startX, startY, endX, endY);
    }

    /**
     * Start a new scissoring zone.
     *
     * @param rectangle The {@link Rectangle} bounds of the scissoring zone.
     * @see #pushZoneScissor(int, int, int, int)
     */
    @PublicAPI
    public static void pushZoneScissor(Rectangle rectangle)
    {
        pushZoneScissor(rectangle.startX(), rectangle.startY(), rectangle.endX(), rectangle.endY());
    }

    /**
     * End a scissoring section. If a previous scissoring session was in progress, then it will be restored and popped
     * from the scissoring queue. If there was not a previous session, then GL scissoring will be disabled. Note that
     * this will not manage any batch rendering the caller must handle this.
     *
     * @see #pushScissor(int, int, int, int)
     * @see #pushZoneScissor(int, int, int, int)
     */
    @PublicAPI
    public static void popScissor()
    {
        Scissor scissor = SCISSOR_QUEUE.poll();

        if (scissor == null)
            return;

        if (SCISSOR_QUEUE.isEmpty())
            GuiUtil.disableScissor();
        else
        {
            Scissor peek = SCISSOR_QUEUE.peek();
            GuiUtil.enableScissor(peek.startX, peek.startY, peek.endX, peek.endY);
        }
    }

    /* Buffers */

    private static class ItemBuffer
    {
        /**
         * Batch an item model. Two different batches will be created to separate flat lighting and 3D lighting.
         *
         * @param graphics    A {@link GuiGraphics} instance.
         * @param itemStack   An {@link ItemStack} instance.
         * @param model       A {@link BakedModel} instance.
         * @param x           Where the item model is rendered relative to the x-axis.
         * @param y           Where the item model is rendered relative to the y-axis.
         * @param packedLight A packed light integer that will be applied to item rendering.
         */
        static void create(GuiGraphics graphics, ItemStack itemStack, BakedModel model, int x, int y, int packedLight)
        {
            if (itemStack.isEmpty())
                return;

            ItemBuffer itemBuffer = new ItemBuffer(graphics.pose(), itemStack, model, x, y, packedLight);

            if (model.usesBlockLight())
                BLOCK_MODEL_QUEUE.add(itemBuffer);
            else
                ITEM_MODEL_QUEUE.add(itemBuffer);
        }

        private final int packedLight;
        private final BakedModel model;
        private final ItemStack itemStack;
        private final Matrix4f matrix;

        private ItemBuffer(PoseStack poseStack, ItemStack itemStack, BakedModel model, int x, int y, int packedLight)
        {
            this.packedLight = packedLight;
            this.itemStack = itemStack;
            this.model = model;
            this.matrix = getModelViewMatrix(poseStack, x, y);
        }
    }

    /**
     * A record class that defines the structure of a line instance.
     *
     * @param matrix    The position matrix that will be used for vertices.
     * @param x1        Where the line starts relative to the x-axis.
     * @param y1        Where the line starts relative to the y-axis.
     * @param x2        Where the line ends relative to the x-axis.
     * @param y2        Where the line ends relative to the y-axis.
     * @param width     The width of the line.
     * @param colorFrom The ARGB starting color of the line.
     * @param colorTo   The ARGB ending color of the line.
     */
    record LineBuffer(Matrix4f matrix, float x1, float y1, float x2, float y2, float width, int colorFrom, int colorTo)
    {
        LineBuffer
        {
            LINE_QUEUE.add(new LineBuffer(matrix, x1, y1, x2, y2, width, colorFrom, colorTo));
        }
    }

    /**
     * A record class that defines the structure of a texture instance.
     *
     * @param matrix  The position matrix that will be used for vertices.
     * @param x       The x-position on the screen to place the texture.
     * @param y       The y-position on the screen to place the texture.
     * @param uOffset The x-position of the texture on the texture sheet.
     * @param vOffset The y-position of the texture on the texture sheet.
     * @param uWidth  The width of the texture on the texture sheet.
     * @param vHeight The height of the texture on the texture sheet.
     * @param rgba    The RGBA[] array to apply to the vertices' colors.
     */
    record TextureBuffer(Matrix4f matrix, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight, float[] rgba)
    {
        /**
         * Add a texture to the texture queue array for batch rendering.
         */
        static void create(Matrix4f matrix, ResourceLocation location, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
        {
            float[] color = RenderSystem.getShaderColor();
            float[] rgba = new float[] { color[0], color[1], color[2], color[3] };

            layer.add(location, new TextureBuffer(matrix, x, y, uOffset, vOffset, uWidth, vHeight, rgba));
            TEXTURE_LAYERS.add(layer);
        }

        /**
         * @return A unique ARGB hash code.
         */
        int hashColor()
        {
            return Arrays.hashCode(this.rgba);
        }
    }

    /**
     * A record class that defines the structure of a sprite instance.
     *
     * @param matrix The position matrix that will be used for vertices.
     * @param x1     The x-coordinate of the first corner for the blit position.
     * @param x2     The x-coordinate of the second corner for the blit position.
     * @param y1     The y-coordinate of the first corner for the blit position.
     * @param y2     The y-coordinate of the second corner for the blit position.
     * @param minU   The minimum horizontal texture coordinate.
     * @param maxU   The maximum horizontal texture coordinate.
     * @param minV   The minimum vertical texture coordinate.
     * @param maxV   The maximum vertical texture coordinate.
     * @param rgba   The RGBA[] array to apply to the vertices' colors.
     */
    record SpriteBuffer(Matrix4f matrix, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV, float[] rgba)
    {
        /**
         * Add a sprite to the texture queue array for batch rendering.
         */
        static void create(Matrix4f matrix, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV)
        {
            float[] color = RenderSystem.getShaderColor();
            float[] rgba = new float[] { color[0], color[1], color[2], color[3] };

            layer.add(atlasLocation, new SpriteBuffer(matrix, x1, x2, y1, y2, minU, maxU, minV, maxV, rgba));
            TEXTURE_LAYERS.add(layer);
        }

        /**
         * @return A unique ARGB hash code.
         */
        int hashColor()
        {
            return Arrays.hashCode(this.rgba);
        }
    }

    /**
     * Set the render type to use when the batch is drawn, or to use immediately.
     *
     * @param type A {@link RenderType} instance.
     */
    @PublicAPI
    public static void setRenderType(RenderType type)
    {
        renderType = type;
    }

    /**
     * Get the tesselator builder being used by this utility. This does not start the buffer for any draw calls. That
     * must be handled separately.
     *
     * @return A {@link BufferBuilder} instance.
     */
    @PublicAPI
    public static BufferBuilder getTesselatorBuilder()
    {
        return MOD_TESSELATOR.getBuilder();
    }

    /**
     * Get and begin a new buffer builder for fill-quad vertices. This will also set up the render system.
     *
     * @return A new {@link BufferBuilder} instance with {@link BufferBuilder#begin(VertexFormat.Mode, VertexFormat)}
     * already called.
     */
    @PublicAPI
    public static BufferBuilder getAndBeginFill()
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder builder = getTesselatorBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        return builder;
    }

    /**
     * End a fill builder. This will also tear down the render system.
     *
     * @param builder A {@link BufferBuilder} instance.
     */
    @PublicAPI
    public static void endFill(BufferBuilder builder)
    {
        if (builder.building())
            draw(builder);

        RenderSystem.disableBlend();
    }

    /**
     * Get and begin a new buffer builder for line vertices. This will also set up the render system.
     *
     * @param width The width of the lines.
     * @return A new {@link BufferBuilder} instance with {@link BufferBuilder#begin(VertexFormat.Mode, VertexFormat)}
     * already called.
     */
    @PublicAPI
    public static BufferBuilder getAndBeginLine(float width)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(width);

        BufferBuilder builder = getTesselatorBuilder();
        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        return builder;
    }

    /**
     * End a line builder. This will also tear down the render system.
     *
     * @param builder A {@link BufferBuilder} instance.
     */
    @PublicAPI
    public static void endLine(BufferBuilder builder)
    {
        draw(builder);

        RenderSystem.lineWidth(1.0F);
        RenderSystem.disableBlend();
    }

    /**
     * Get and begin a new buffer builder for texture vertices. This will also set up the render system.
     *
     * @param location A {@link ResourceLocation} instance.
     * @return A new {@link BufferBuilder} instance with {@link BufferBuilder#begin(VertexFormat.Mode, VertexFormat)}
     * already called.
     */
    @PublicAPI
    public static BufferBuilder getAndBeginTexture(ResourceLocation location)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        BufferBuilder builder = getTesselatorBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        return builder;
    }

    /**
     * Get and begin a new buffer builder for texture vertices. This will set up the render system and cache the given
     * texture location that will be used when building vertices.
     *
     * @param location A {@link TextureLocation} instance.
     * @return A new {@link BufferBuilder} instance with {@link BufferBuilder#begin(VertexFormat.Mode, VertexFormat)}
     * already called.
     */
    @PublicAPI
    public static BufferBuilder getAndBeginTexture(TextureLocation location)
    {
        texture = location;

        return getAndBeginTexture((ResourceLocation) location);
    }

    /**
     * Define the current texture layer to buffer textured draw calls to.
     *
     * @param textureLayer A {@link TextureLayer} instance.
     */
    @PublicAPI
    public static void pushLayer(TextureLayer textureLayer)
    {
        layer = textureLayer;
        TEXTURE_LAYERS.add(textureLayer);
    }

    /**
     * Reset the {@link TextureLayer} back to the default instance.
     */
    @PublicAPI
    public static void popLayer()
    {
        layer = TextureLayer.DEFAULT;
    }

    /**
     * End a texture builder. This will also tear down the render system and nullify the texture location cache if it
     * was set.
     *
     * @param builder A {@link BufferBuilder} instance.
     */
    @PublicAPI
    public static void endTexture(BufferBuilder builder)
    {
        draw(builder);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        texture = null;
    }

    /**
     * If the renderer utility is batching its calls, then the matrix used for draw calls needs copied to prevent a loss
     * in position data when the buffer builders upload their data.
     *
     * @param poseStack The pose stack being used for drawing.
     * @return A matrix to use for vertex building.
     */
    private static Matrix4f getMatrix(PoseStack poseStack)
    {
        return isBatching ? new Matrix4f(poseStack.last().pose()) : poseStack.last().pose();
    }

    /**
     * Run the given runnable that has any applicable draw calls batched.
     *
     * @param runnable A {@link Runnable} instance.
     */
    @PublicAPI
    public static void batch(Runnable runnable)
    {
        beginBatching();
        runnable.run();
        endBatching();
    }

    /**
     * Begin the process of batching draw calls.
     */
    @PublicAPI
    public static void beginBatching()
    {
        batchIndex++;
        isBatching = true;
    }

    /**
     * Defer rendering instructions until after current batching has completed. If this utility is not batching draw
     * calls, then the given deferred instructions will run immediately. All deferred renderers will have their draw
     * calls batched as well. If this is not desired then {@link #pauseBatching()}, perform the draw calls, and then
     * {@link #resumeBatching()}.
     *
     * @param deferred A {@link Runnable} of rendering instructions to defer.
     */
    @PublicAPI
    public static void deferredRenderer(Runnable deferred)
    {
        DEFERRED_QUEUE.add(deferred);
    }

    /**
     * Draw to the screen with the set render type if it is available.
     *
     * @param builder The {@link BufferBuilder} to end and draw with.
     */
    private static void draw(BufferBuilder builder)
    {
        BufferBuilder.RenderedBuffer rendered = builder.endOrDiscardIfEmpty();

        if (rendered == null)
            return;

        if (renderType == null)
            BufferUploader.drawWithShader(rendered);
        else
        {
            renderType.setupRenderState();
            BufferUploader.drawWithShader(rendered);
            renderType.clearRenderState();

            if (!isBatching)
                renderType = null;
        }
    }

    /**
     * Ends batched fill queue.
     */
    private static void endBatchingFills(BufferBuilder builder)
    {
        if (FILL_VERTICES.isEmpty())
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        while (!FILL_VERTICES.isEmpty())
            FILL_VERTICES.pollLast().accept(builder);

        draw(builder);

        RenderSystem.disableBlend();
    }

    /**
     * Ends batched line queue.
     */
    private static void endBatchingLines(BufferBuilder builder)
    {
        if (LINE_QUEUE.isEmpty())
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);

        LINE_QUEUE.stream().map(LineBuffer::width).distinct().forEach(width -> {
            RenderSystem.lineWidth(width);

            if (!builder.building())
                builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            LINE_QUEUE.stream().filter(line -> line.width == width).forEach(line -> {
                float nx = MathUtil.sign(line.x2 - line.x1);
                float ny = MathUtil.sign(line.y2 - line.y1);

                // @formatter:off
                builder.vertex(line.matrix, line.x1, line.y1, 0.0F).color(line.colorFrom).normal(nx, ny, 0.0F).endVertex();
                builder.vertex(line.matrix, line.x2, line.y2, 0.0F).color(line.colorTo).normal(nx, ny, 0.0F).endVertex();
                // @formatter:on
            });

            draw(builder);
        });

        RenderSystem.lineWidth(1.0F);
        RenderSystem.disableBlend();

        LINE_QUEUE.clear();
    }

    /**
     * Ends batched texture queue.
     */
    private static void endBatchingTextures(BufferBuilder builder)
    {
        if (TEXTURE_LAYERS.isEmpty())
            return;

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();

        TEXTURE_LAYERS.stream().sorted(Comparator.comparingInt(TextureLayer::getIndex)).forEach(layer -> {
            layer.textureMap.forEach((location, queue) -> {
                RenderSystem.setShaderTexture(0, location);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                if (!builder.building())
                    builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                if (location instanceof TextureLocation sheet)
                    queue.forEach(buffer -> blitTexture(sheet, builder, buffer.matrix, buffer.x, buffer.y, buffer.uOffset, buffer.vOffset, buffer.uWidth, buffer.vHeight, buffer.rgba));
                else
                    queue.forEach(buffer -> blit256(buffer.matrix, buffer.x, buffer.y, buffer.uOffset, buffer.vOffset, buffer.uWidth, buffer.vHeight, buffer.rgba));

                draw(builder);
            });

            layer.textureLightMap.forEach((location, queue) -> {
                RenderSystem.setShaderTexture(0, location);

                if (location instanceof TextureLocation sheet)
                {
                    queue.stream().collect(Collectors.groupingBy(TextureBuffer::hashColor)).forEach((argb, buffers) -> {
                        if (!builder.building())
                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                        float r = buffers.get(0).rgba[0];
                        float g = buffers.get(0).rgba[1];
                        float b = buffers.get(0).rgba[2];
                        float a = buffers.get(0).rgba[3];

                        RenderSystem.setShaderColor(r, g, b, a);

                        buffers.forEach(buffer -> blitTexture(sheet, builder, buffer.matrix, buffer.x, buffer.y, buffer.uOffset, buffer.vOffset, buffer.uWidth, buffer.vHeight, buffer.rgba));
                        draw(builder);
                    });
                }
                else
                {
                    queue.stream().collect(Collectors.groupingBy(TextureBuffer::hashColor)).forEach((argb, buffers) -> {
                        if (!builder.building())
                            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                        float r = buffers.get(0).rgba[0];
                        float g = buffers.get(0).rgba[1];
                        float b = buffers.get(0).rgba[2];
                        float a = buffers.get(0).rgba[3];

                        RenderSystem.setShaderColor(r, g, b, a);

                        buffers.forEach(buffer -> blit256(buffer.matrix, buffer.x, buffer.y, buffer.uOffset, buffer.vOffset, buffer.uWidth, buffer.vHeight, buffer.rgba));
                        draw(builder);
                    });
                }
            });

            layer.spriteMap.forEach((sprite, queue) -> {
                RenderSystem.setShaderTexture(0, sprite);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                if (!builder.building())
                    builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                queue.forEach(buffer -> innerBlit(builder, buffer));

                draw(builder);
            });

            layer.spriteLightMap.forEach((sprite, queue) -> {
                RenderSystem.setShaderTexture(0, sprite);

                queue.stream().collect(Collectors.groupingBy(SpriteBuffer::hashColor)).forEach((argb, buffers) -> {
                    if (!builder.building())
                        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

                    float r = buffers.get(0).rgba[0];
                    float g = buffers.get(0).rgba[1];
                    float b = buffers.get(0).rgba[2];
                    float a = buffers.get(0).rgba[3];

                    RenderSystem.setShaderColor(r, g, b, a);

                    buffers.forEach(buffer -> innerBlit(builder, buffer));
                    draw(builder);
                });
            });
        });

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        TEXTURE_LAYERS.forEach(TextureLayer::clear);
        TEXTURE_LAYERS.clear();
    }

    /**
     * Ends batched items queue.
     */
    private static void endBatchingItemsQueue()
    {
        if (ITEM_MODEL_QUEUE.isEmpty())
            return;

        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        Lighting.setupForFlatItems();

        ITEM_MODEL_QUEUE.forEach(item -> {
            poseStack.last().pose().set(item.matrix);

            Minecraft.getInstance()
                .getItemRenderer()
                .render(item.itemStack, ItemDisplayContext.GUI, false, poseStack, buffer, item.packedLight, OverlayTexture.NO_OVERLAY, item.model);
        });

        buffer.endBatch();
        ITEM_MODEL_QUEUE.clear();

        Lighting.setupFor3DItems();
    }

    /**
     * Ends batched blocks queue.
     */
    private static void endBatchingBlocksQueue()
    {
        if (BLOCK_MODEL_QUEUE.isEmpty())
            return;

        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        BLOCK_MODEL_QUEUE.forEach(block -> {
            poseStack.last().pose().set(block.matrix);

            Minecraft.getInstance()
                .getItemRenderer()
                .render(block.itemStack, ItemDisplayContext.GUI, false, poseStack, buffer, block.packedLight, OverlayTexture.NO_OVERLAY, block.model);
        });

        buffer.endBatch();
        BLOCK_MODEL_QUEUE.clear();
    }

    /**
     * Finish the process of batching draw calls.
     */
    @PublicAPI
    public static void endBatching()
    {
        if (isPausedBatching)
            resumeBatching();

        if (batchIndex > 0)
            batchIndex--;

        if (!isBatching || batchIndex > 0)
        {
            fillZOffset = 0;
            return;
        }

        BufferBuilder builder = getTesselatorBuilder();

        RenderSystem.enableDepthTest();
        endBatchingFills(builder);
        endBatchingLines(builder);
        endBatchingTextures(builder);
        endBatchingItemsQueue();
        endBatchingBlocksQueue();

        RenderSystem.enableDepthTest();
        FONT_BATCH.endBatch();
        RenderSystem.disableDepthTest();

        fillZOffset = 0;
        isBatching = false;
        renderType = null;

        if (DEFERRED_QUEUE.isEmpty())
            return;

        batch(() -> {
            while (!DEFERRED_QUEUE.isEmpty())
                DEFERRED_QUEUE.poll().run();
        });
    }

    /**
     * Forcefully finish all batched draw calls. This is a safety measure that should be implemented when a render cycle
     * ends. Always use {@link RenderUtil#endBatching()} to finish batching.
     *
     * @return Whether batching was flushed.
     */
    @PublicAPI
    public static boolean flush()
    {
        boolean isFlushed = batchIndex > 0;

        while (batchIndex > 0)
            endBatching();

        return isFlushed;
    }

    /* Rendering */

    /**
     * Creates a filled rectangle at the given positions with the given color.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param matrix   The {@link Matrix4f} instance.
     * @param x0       The left x-coordinate of the rectangle.
     * @param y0       The top y-coordinate of the rectangle.
     * @param x1       The right x-coordinate of the rectangle.
     * @param y1       The bottom y-coordinate of the rectangle.
     * @param argb     The ARGB color of the rectangle.
     */
    @PublicAPI
    public static void fill(VertexConsumer consumer, Matrix4f matrix, float x0, float y0, float x1, float y1, int argb)
    {
        float z = 0.0F;

        consumer.vertex(matrix, x0, y1, z).color(argb).endVertex();
        consumer.vertex(matrix, x1, y1, z).color(argb).endVertex();
        consumer.vertex(matrix, x1, y0, z).color(argb).endVertex();
        consumer.vertex(matrix, x0, y0, z).color(argb).endVertex();
    }

    /**
     * Overload method for {@link RenderUtil#fill(VertexConsumer, Matrix4f, float, float, float, float, int)}. This
     * method does not require a 4D matrix.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param x0       The left x-coordinate of the rectangle.
     * @param y0       The top y-coordinate of the rectangle.
     * @param x1       The right x-coordinate of the rectangle.
     * @param y1       The bottom y-coordinate of the rectangle.
     * @param argb     The ARGB color of the rectangle.
     */
    @PublicAPI
    public static void fill(VertexConsumer consumer, float x0, float y0, float x1, float y1, int argb)
    {
        float z = 0.0F;

        consumer.vertex(x0, y1, z).color(argb).endVertex();
        consumer.vertex(x1, y1, z).color(argb).endVertex();
        consumer.vertex(x1, y0, z).color(argb).endVertex();
        consumer.vertex(x0, y0, z).color(argb).endVertex();
    }

    /**
     * Creates a filled rectangle at the given positions with the given color.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the rectangle.
     * @param y0       The top y-coordinate of the rectangle.
     * @param x1       The right x-coordinate of the rectangle.
     * @param y1       The bottom y-coordinate of the rectangle.
     * @param argb     The ARGB color of the rectangle.
     */
    @PublicAPI
    public static void fill(VertexConsumer consumer, GuiGraphics graphics, float x0, float y0, float x1, float y1, int argb)
    {
        fill(consumer, graphics.pose().last().pose(), x0, y0, x1, y1, argb);
    }

    /**
     * Overload method for {@link RenderUtil#fill(VertexConsumer, Matrix4f, float, float, float, float, int)}. This
     * method does not require a buffer builder or a 4D matrix, but instead uses {@link GuiGraphics}.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the rectangle.
     * @param y0       The top y-coordinate of the rectangle.
     * @param x1       The right x-coordinate of the rectangle.
     * @param y1       The bottom y-coordinate of the rectangle.
     * @param argb     The ARGB color of the rectangle.
     */
    @PublicAPI
    public static void fill(GuiGraphics graphics, float x0, float y0, float x1, float y1, int argb)
    {
        fillGradient(graphics.pose(), x0, y0, x1, y1, argb, argb, true);
    }

    /**
     * Overload method for {@link RenderUtil#fill(GuiGraphics, float, float, float, float, int)}.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the rectangle.
     * @param y0       The top y-coordinate of the rectangle.
     * @param x1       The right x-coordinate of the rectangle.
     * @param y1       The bottom y-coordinate of the rectangle.
     * @param color    A {@link Color} instance for the rectangle.
     */
    @PublicAPI
    public static void fill(GuiGraphics graphics, float x0, float y0, float x1, float y1, Color color)
    {
        int argb = color.get();

        fillGradient(graphics.pose(), x0, y0, x1, y1, argb, argb, true);
    }

    /**
     * Draws a filled gradient rectangle to the screen.
     *
     * @param poseStack  The current {@link PoseStack}.
     * @param x0         The left x-coordinate of the fill.
     * @param y0         The top y-coordinate of the fill.
     * @param x1         The right x-coordinate of the fill.
     * @param y1         The bottom y-coordinate of the fill.
     * @param colorFrom  The starting gradient ARGB integer color.
     * @param colorTo    The ending gradient ARGB integer color.
     * @param isVertical Whether the gradient is vertical, otherwise it will be horizontal.
     */
    private static void fillGradient(PoseStack poseStack, float x0, float y0, float x1, float y1, int colorFrom, int colorTo, boolean isVertical)
    {
        float z = isBatching ? (float) fillZOffset : 0.0F;
        Matrix4f matrix = getMatrix(poseStack);

        Consumer<BufferBuilder> vertices = (builder) -> {
            if (isVertical)
            {
                builder.vertex(matrix, x0, y1, z).color(colorTo).endVertex();
                builder.vertex(matrix, x1, y1, z).color(colorTo).endVertex();
                builder.vertex(matrix, x1, y0, z).color(colorFrom).endVertex();
                builder.vertex(matrix, x0, y0, z).color(colorFrom).endVertex();
            }
            else
            {
                builder.vertex(matrix, x0, y1, z).color(colorFrom).endVertex();
                builder.vertex(matrix, x1, y1, z).color(colorTo).endVertex();
                builder.vertex(matrix, x1, y0, z).color(colorTo).endVertex();
                builder.vertex(matrix, x0, y0, z).color(colorFrom).endVertex();
            }
        };

        if (!isBatching)
        {
            BufferBuilder builder = getAndBeginFill();
            vertices.accept(builder);

            endFill(builder);
        }
        else
            FILL_VERTICES.push(vertices);
    }

    /**
     * Draws a filled gradient rectangle that goes top-down onto the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The left x-coordinate of the fill.
     * @param y0        The top y-coordinate of the fill.
     * @param x1        The right x-coordinate of the fill.
     * @param y1        The bottom y-coordinate of the fill.
     * @param colorFrom The starting gradient ARGB integer color.
     * @param colorTo   The ending gradient ARGB integer color.
     */
    @PublicAPI
    public static void fromTopGradient(GuiGraphics graphics, float x0, float y0, float x1, float y1, int colorFrom, int colorTo)
    {
        fillGradient(graphics.pose(), x0, y0, x1, y1, colorFrom, colorTo, true);
    }

    /**
     * Draws a filled gradient rectangle that goes top-down onto the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The left x-coordinate of the fill.
     * @param y0        The top y-coordinate of the fill.
     * @param x1        The right x-coordinate of the fill.
     * @param y1        The bottom y-coordinate of the fill.
     * @param colorFrom The starting gradient {@link Color}.
     * @param colorTo   The ending gradient {@link Color}.
     */
    @PublicAPI
    public static void fromTopGradient(GuiGraphics graphics, float x0, float y0, float x1, float y1, Color colorFrom, Color colorTo)
    {
        fillGradient(graphics.pose(), x0, y0, x1, y1, colorFrom.get(), colorTo.get(), true);
    }

    /**
     * Draws a filled gradient rectangle that goes left to right onto the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The left x-coordinate of the fill.
     * @param y0        The top y-coordinate of the fill.
     * @param x1        The right x-coordinate of the fill.
     * @param y1        The bottom y-coordinate of the fill.
     * @param colorFrom The starting gradient ARGB integer color.
     * @param colorTo   The ending gradient ARGB integer color.
     */
    @PublicAPI
    public static void fromLeftGradient(GuiGraphics graphics, float x0, float y0, float x1, float y1, int colorFrom, int colorTo)
    {
        fillGradient(graphics.pose(), x0, y0, x1, y1, colorFrom, colorTo, false);
    }

    /**
     * Draws a filled gradient rectangle that goes left to right onto the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The left x-coordinate of the fill.
     * @param y0        The top y-coordinate of the fill.
     * @param x1        The right x-coordinate of the fill.
     * @param y1        The bottom y-coordinate of the fill.
     * @param colorFrom The starting gradient {@link Color}.
     * @param colorTo   The ending gradient {@link Color}.
     */
    @PublicAPI
    public static void fromLeftGradient(GuiGraphics graphics, float x0, float y0, float x1, float y1, Color colorFrom, Color colorTo)
    {
        fillGradient(graphics.pose(), x0, y0, x1, y1, colorFrom.get(), colorTo.get(), false);
    }

    /**
     * Draws a filled gradient rectangle based on the given {@link Gradient} instance.
     *
     * @param gradient A {@link Gradient} instance.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the fill.
     * @param y0       The top y-coordinate of the fill.
     * @param x1       The right x-coordinate of the fill.
     * @param y1       The bottom y-coordinate of the fill.
     */
    @PublicAPI
    public static void gradient(Gradient gradient, GuiGraphics graphics, float x0, float y0, float x1, float y1)
    {
        int from = gradient.from().get();
        int to = gradient.to().get();

        switch (gradient.direction())
        {
            case VERTICAL -> fromTopGradient(graphics, x0, y0, x1, y1, from, to);
            case HORIZONTAL -> fromLeftGradient(graphics, x0, y0, x1, y1, from, to);
        }
    }

    /**
     * Draw a line gradient to the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        Where the line starts relative to the x-axis.
     * @param y0        Where the line starts relative to the y-axis.
     * @param x1        Where the line starts relative to the x-axis.
     * @param y1        Where the line starts relative to the y-axis.
     * @param width     The width of the line.
     * @param colorFrom The ARGB starting color of the line.
     * @param colorTo   The ARGB ending color of the line.
     */
    @PublicAPI
    public static void lineGradient(GuiGraphics graphics, float x0, float y0, float x1, float y1, float width, int colorFrom, int colorTo)
    {
        float z = 0.0F;
        Matrix4f matrix = getMatrix(graphics.pose());

        if (isBatching)
            new LineBuffer(matrix, x0, y0, x1, y1, width, colorFrom, colorTo);
        else
        {
            BufferBuilder builder = getAndBeginLine(width);
            builder.vertex(matrix, x0, y0, z).color(colorFrom).normal(1.0F, 1.0F, 1.0F).endVertex();
            builder.vertex(matrix, x1, y1, z).color(colorTo).normal(1.0F, 1.0F, 1.0F).endVertex();

            endLine(builder);
        }
    }

    /**
     * Draw a line to the screen.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       Where the line starts relative to the x-axis.
     * @param y0       Where the line starts relative to the y-axis.
     * @param x1       Where the line starts relative to the x-axis.
     * @param y1       Where the line starts relative to the y-axis.
     * @param width    The width of the line.
     * @param argb     The ARGB color of the line.
     */
    @PublicAPI
    public static void line(GuiGraphics graphics, float x0, float y0, float x1, float y1, float width, int argb)
    {
        lineGradient(graphics, x0, y0, x1, y1, width, argb, argb);
    }

    /**
     * Draw a 1px vertical line.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The x-coordinate of the line.
     * @param y0       The top y-coordinate of the line.
     * @param y1       The bottom y-coordinate of the line.
     * @param argb     The ARGB color of the line.
     */
    @PublicAPI
    public static void vLine(GuiGraphics graphics, float x0, float y0, float y1, int argb)
    {
        fill(graphics, x0, y0, x0 + 1, y1, argb);
    }

    /**
     * Draw a 1px vertical line.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The x-coordinate of the line.
     * @param y0       The top y-coordinate of the line.
     * @param y1       The bottom y-coordinate of the line.
     * @param color    The {@link Color} of the line.
     */
    @PublicAPI
    public static void vLine(GuiGraphics graphics, float x0, float y0, float y1, Color color)
    {
        vLine(graphics, x0, y0, y1, color.get());
    }

    /**
     * Draw a 1px horizontal line.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the line.
     * @param y0       The y-coordinate of the line.
     * @param x1       The right x-coordinate of the line.
     * @param argb     The ARGB color of the line.
     */
    @PublicAPI
    public static void hLine(GuiGraphics graphics, float x0, float y0, float x1, int argb)
    {
        fill(graphics, x0, y0, x1, y0 + 1, argb);
    }

    /**
     * Draw a 1px horizontal line.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The left x-coordinate of the line.
     * @param y0       The y-coordinate of the line.
     * @param x1       The right x-coordinate of the line.
     * @param color    The {@link Color} of the line.
     */
    @PublicAPI
    public static void hLine(GuiGraphics graphics, float x0, float y0, float x1, Color color)
    {
        hLine(graphics, x0, y0, x1, color.get());
    }

    /**
     * Draw an outline (a hollow fill algorithm).
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The starting x-coordinate of the outline box.
     * @param y0        The starting y-coordinate of the outline box.
     * @param width     The width of the outline box.
     * @param height    The height of the outline box.
     * @param thickness The thickness of the outline box.
     * @param argb      The ARGB color of the outline box.
     */
    @PublicAPI
    public static void outline(GuiGraphics graphics, float x0, float y0, float width, float height, float thickness, int argb)
    {
        boolean notBatching = !isBatching;

        if (notBatching)
            beginBatching();

        fill(graphics, x0, y0 + thickness, x0 + thickness, y0 + height - thickness, argb);
        fill(graphics, x0 + width - thickness, y0, x0 + width, y0 + height - thickness, argb);

        fill(graphics, x0, y0, x0 + width - thickness, y0 + thickness, argb);
        fill(graphics, x0, y0 + height - thickness, x0 + width, y0 + height, argb);

        if (notBatching)
            endBatching();
    }

    /**
     * Draw an outline (a hollow fill algorithm).
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param x0        The starting x-coordinate of the outline box.
     * @param y0        The starting y-coordinate of the outline box.
     * @param width     The width of the outline box.
     * @param height    The height of the outline box.
     * @param thickness The thickness of the outline box.
     * @param color     A {@link Color} instance.
     */
    @PublicAPI
    public static void outline(GuiGraphics graphics, float x0, float y0, float width, float height, float thickness, Color color)
    {
        outline(graphics, x0, y0, width, height, thickness, color.get());
    }

    /**
     * Draw an outline (a hollow fill algorithm) with a default thickness of {@code 1}.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The starting x-coordinate of the outline box.
     * @param y0       The starting y-coordinate of the outline box.
     * @param width    The width of the outline box.
     * @param height   The height of the outline box.
     * @param argb     The ARGB color of the outline box.
     */
    @PublicAPI
    public static void outline(GuiGraphics graphics, float x0, float y0, float width, float height, int argb)
    {
        outline(graphics, x0, y0, width, height, 1.0F, argb);
    }

    /**
     * Draw an outline (a hollow fill algorithm) with a default thickness of {@code 1}.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param x0       The starting x-coordinate of the outline box.
     * @param y0       The starting y-coordinate of the outline box.
     * @param width    The width of the outline box.
     * @param height   The height of the outline box.
     * @param color    A {@link Color} instance.
     */
    @PublicAPI
    public static void outline(GuiGraphics graphics, float x0, float y0, float width, float height, Color color)
    {
        outline(graphics, x0, y0, width, height, color.get());
    }

    /**
     * Draw a circle to the screen.
     *
     * @param graphics A {@link GuiGraphics} instance.
     * @param centerX  The center point of the circle relative to the x-axis.
     * @param centerY  The center point of the circle relative to the y-axis.
     * @param radius   The radius of the circle.
     * @param argb     The ARGB integer color of the circle.
     */
    @PublicAPI
    public static void circle(GuiGraphics graphics, float centerX, float centerY, float radius, int argb)
    {
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        for (float f = 0.0F; f < 360.0F; f += 4.5F)
        {
            float rads = (float) Math.toRadians(f);
            float x = (float) (centerX + (Math.sin(rads) * radius));
            float y = (float) (centerY + (Math.cos(rads) * radius));

            builder.vertex(matrix, x, y, 0.0F).color(argb).endVertex();
        }

        draw(builder);

        RenderSystem.disableBlend();
    }

    /**
     * Draw a texture sprite from the given arguments. This operation supports batching.
     *
     * @param texture  A {@link TextureLocation} instance.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the image.
     * @param y        The y-coordinate of the image.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blitTexture(TextureLocation texture, GuiGraphics graphics, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (isBatching)
        {
            TextureBuffer.create(getMatrix(graphics.pose()), texture, x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }

        BufferBuilder builder = getAndBeginTexture(texture);

        blitTexture(builder, graphics, x, y, uOffset, vOffset, uWidth, vHeight);
        endTexture(builder);
    }

    /**
     * Draw a texture sprite from the given arguments. This operation supports batching.
     *
     * @param texture  A {@link TextureLocation} instance.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the image.
     * @param y        The y-coordinate of the image.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blitTexture(TextureLocation texture, GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        blitTexture(texture, graphics, (float) x, (float) y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Put vertex data into the given {@link BufferBuilder} instance using the cached {@link TextureLocation} from
     * {@link #getAndBeginTexture(TextureLocation)} and given arguments.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the image.
     * @param y        The y-coordinate of the image.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blitTexture(VertexConsumer consumer, GuiGraphics graphics, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        if (texture == null)
            return;

        Matrix4f matrix = graphics.pose().last().pose();

        blitTexture(texture, consumer, matrix, x, y, uOffset, vOffset, uWidth, vHeight, RenderSystem.getShaderColor());
    }

    /**
     * Internal blit instructions for any texture sheet.
     */
    private static void blitTexture(TextureLocation texture, VertexConsumer consumer, Matrix4f matrix, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight, float[] rgba)
    {
        float x2 = x + uWidth;
        float y2 = y + vHeight;
        float minU = uOffset / (float) texture.getWidth();
        float maxU = (uOffset + uWidth) / (float) texture.getWidth();
        float minV = vOffset / (float) texture.getHeight();
        float maxV = (vOffset + vHeight) / (float) texture.getHeight();
        int color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).get();

        consumer.vertex(matrix, x, y2, 0.0F).uv(minU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y2, 0.0F).uv(maxU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y, 0.0F).uv(maxU, minV).color(color).endVertex();
        consumer.vertex(matrix, x, y, 0.0F).uv(minU, minV).color(color).endVertex();
    }

    /**
     * Put vertex data into the given {@link BufferBuilder} instance using the cached {@link TextureLocation} from
     * {@link #getAndBeginTexture(TextureLocation)} and given arguments.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate of the image.
     * @param y        The y-coordinate of the image.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blitTexture(VertexConsumer consumer, GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        blitTexture(consumer, graphics, (float) x, (float) y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Build vertices that relative to the screen point (0, 0) using the given matrix, width, and height for a texture.
     */
    private static void blitTexture(Matrix4f matrix, int width, int height, float[] rgba)
    {
        BufferBuilder builder = getTesselatorBuilder();
        int color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).get();

        builder.vertex(matrix, 0, height, 0).uv(0.0F, 1.0F).color(color).endVertex();
        builder.vertex(matrix, width, height, 0).uv(1.0F, 1.0F).color(color).endVertex();
        builder.vertex(matrix, width, 0, 0).uv(1.0F, 0.0F).color(color).endVertex();
        builder.vertex(matrix, 0, 0, 0).uv(0.0F, 0.0F).color(color).endVertex();
    }

    /**
     * Draw a texture from the texture sheet. This method does <b color=red>not</b> set up the render system,
     * <b color=red>nor</b> end the building process. Render system set up must be done before invoking this method and
     * the builder <i>must</i> be closed after invoking this method.
     *
     * @param location A {@link TextureLocation} instance.
     * @param matrix   A {@link Matrix4f} instance.
     */
    private static void blitTexture(TextureLocation location, Matrix4f matrix)
    {
        blitTexture(matrix, location.getWidth(), location.getHeight(), RenderSystem.getShaderColor());
    }

    /**
     * Draw a texture from the given texture sheet location at the given location.
     *
     * @param location A {@link TextureLocation} instance.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     */
    @PublicAPI
    public static void blitTexture(TextureLocation location, GuiGraphics graphics, int x, int y)
    {
        blitTexture(location, graphics, 1.0F, x, y);
    }

    /**
     * Draw a texture from the texture sheet with a specific scale at the given location.
     *
     * @param location A {@link TextureLocation} instance.
     * @param graphics A {@link GuiGraphics} instance.
     * @param scale    The scale to apply to the image.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     */
    @PublicAPI
    public static void blitTexture(TextureLocation location, GuiGraphics graphics, float scale, int x, int y)
    {
        int width = location.getWidth();
        int height = location.getHeight();

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0D);
        graphics.pose().scale(scale, scale, scale);

        if (isBatching)
        {
            TextureBuffer.create(getMatrix(graphics.pose()), location, 0, 0, 0, 0, width, height);
            graphics.pose().popPose();

            return;
        }

        BufferBuilder builder = getAndBeginTexture(location);

        blitTexture(location, graphics.pose().last().pose());
        endTexture(builder);

        graphics.pose().popPose();
    }

    /**
     * Blit a portion of the texture specified by {@link #getAndBeginTexture(ResourceLocation)} onto the screen at the
     * given coordinates and blit texture coordinates.
     *
     * @param consumer      The {@link VertexConsumer} to build vertices with.
     * @param graphics      A {@link GuiGraphics} instance.
     * @param x             The x-coordinate of the blit position.
     * @param y             The y-coordinate of the blit position.
     * @param uOffset       The horizontal texture coordinate offset.
     * @param vOffset       The vertical texture coordinate offset.
     * @param uWidth        The width of the blit portion in texture coordinates.
     * @param vHeight       The height of the blit portion in texture coordinates.
     * @param textureWidth  The width of the texture.
     * @param textureHeight The height of the texture.
     */
    @PublicAPI
    public static void blitTexture(VertexConsumer consumer, GuiGraphics graphics, int x, int y, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight)
    {
        Matrix4f matrix = graphics.pose().last().pose();
        float[] rgba = RenderSystem.getShaderColor();
        float x2 = x + uWidth;
        float y2 = y + vHeight;
        float minU = uOffset / (float) textureWidth;
        float maxU = (uOffset + uWidth) / (float) textureWidth;
        float minV = vOffset / (float) textureHeight;
        float maxV = (vOffset + vHeight) / (float) textureHeight;
        int color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).get();

        consumer.vertex(matrix, x, y2, 0.0F).uv(minU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y2, 0.0F).uv(maxU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y, 0.0F).uv(maxU, minV).color(color).endVertex();
        consumer.vertex(matrix, x, y, 0.0F).uv(minU, minV).color(color).endVertex();
    }

    /**
     * Internal 256x256 texture sheet vertex builder.
     */
    private static void blit256(VertexConsumer consumer, Matrix4f matrix, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight, float[] rgba)
    {
        float x2 = x + uWidth;
        float y2 = y + vHeight;
        float minU = uOffset / 256.0F;
        float maxU = (uOffset + uWidth) / 256.0F;
        float minV = vOffset / 256.0F;
        float maxV = (vOffset + vHeight) / 256.0F;
        int color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).get();

        consumer.vertex(matrix, x, y2, 0.0F).uv(minU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y2, 0.0F).uv(maxU, maxV).color(color).endVertex();
        consumer.vertex(matrix, x2, y, 0.0F).uv(maxU, minV).color(color).endVertex();
        consumer.vertex(matrix, x, y, 0.0F).uv(minU, minV).color(color).endVertex();
    }

    /**
     * Put vertex data into the given {@link BufferBuilder} based on the current shader texture and x/y floats.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blit256(VertexConsumer consumer, GuiGraphics graphics, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        Matrix4f matrix = graphics.pose().last().pose();

        blit256(consumer, matrix, x, y, uOffset, vOffset, uWidth, vHeight, RenderSystem.getShaderColor());
    }

    /**
     * Put vertex data into the given {@link BufferBuilder} based on the current shader texture and x/y integers.
     *
     * @param consumer The {@link VertexConsumer} to build vertices with.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blit256(VertexConsumer consumer, GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        blit256(consumer, graphics, (float) x, (float) y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * Build a quad of vertices for a texture.
     */
    private static void blit256(Matrix4f matrix, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight, float[] rgba)
    {
        blit256(getTesselatorBuilder(), matrix, x, y, uOffset, vOffset, uWidth, vHeight, rgba);
    }

    /**
     * Render a texture from a texture sheet (256x256) using floating x/y positions.
     *
     * @param location A {@link ResourceLocation} that points to the texture sheet.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blit256(ResourceLocation location, GuiGraphics graphics, float x, float y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        Matrix4f matrix = getMatrix(graphics.pose());

        if (isBatching)
        {
            TextureBuffer.create(matrix, location, x, y, uOffset, vOffset, uWidth, vHeight);
            return;
        }

        BufferBuilder builder = getAndBeginTexture(location);

        blit256(matrix, x, y, uOffset, vOffset, uWidth, vHeight, RenderSystem.getShaderColor());
        endTexture(builder);
    }

    /**
     * Render a texture from a texture sheet (256x256) using integer x/y positions.
     *
     * @param location A {@link ResourceLocation} that points to the texture sheet.
     * @param graphics A {@link GuiGraphics} instance.
     * @param x        The x-coordinate on the screen to place the texture.
     * @param y        The y-coordinate on the screen to place the texture.
     * @param uOffset  The x-coordinate of the texture on the texture sheet.
     * @param vOffset  The y-coordinate of the texture on the texture sheet.
     * @param uWidth   The width of the texture on the texture sheet.
     * @param vHeight  The height of the texture on the texture sheet.
     */
    @PublicAPI
    public static void blit256(ResourceLocation location, GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        blit256(location, graphics, (float) x, (float) y, uOffset, vOffset, uWidth, vHeight);
    }

    /* Sprite Rendering */

    /**
     * Render a texture using a blit operation with the given scaling.
     *
     * @param sprite   The {@link GuiSprite} to get sprite data from.
     * @param graphics The {@link GuiGraphics} instance.
     * @param scale    The scale to apply.
     * @param x        The x-coordinate of where to start the blit.
     * @param y        The y-coordinate of where to start the blit.
     * @param width    The width of the blit.
     * @param height   The height of the blit.
     */
    @PublicAPI
    public static void blitSprite(GuiSprite sprite, GuiGraphics graphics, float scale, int x, int y, int width, int height)
    {
        GuiSpriteScaling spriteScaling = sprite.getScaling();

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, scale);

        if (spriteScaling instanceof GuiSpriteScaling.Stretch)
            blitSprite(sprite, graphics.pose(), 0, 0, width, height);
        else if (spriteScaling instanceof GuiSpriteScaling.Tile tile)
            blitTiledSprite(sprite, graphics.pose(), 0, 0, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height());
        else if (spriteScaling instanceof GuiSpriteScaling.NineSlice nineSlice)
            blitNineSlicedSprite(sprite, nineSlice, graphics.pose(), 0, 0, width, height);

        graphics.pose().popPose();
    }

    /**
     * Render a texture using a blit operation.
     *
     * @param sprite   The {@link GuiSprite} to get sprite data from.
     * @param graphics The {@link GuiGraphics} instance.
     * @param x        The x-coordinate of where to start the blit.
     * @param y        The y-coordinate of where to start the blit.
     * @param width    The width of the blit.
     * @param height   The height of the blit.
     */
    @PublicAPI
    public static void blitSprite(GuiSprite sprite, GuiGraphics graphics, int x, int y, int width, int height)
    {
        blitSprite(sprite, graphics, 1.0F, x, y, width, height);
    }

    /**
     * Render a {@link GuiSprite} at the given coordinates.
     *
     * @param sprite   The {@link GuiSprite} to get sprite data from.
     * @param graphics The {@link GuiGraphics} instance.
     * @param x        The x-coordinate of where to start the blit.
     * @param y        The y-coordinate of where to start the blit.
     */
    @PublicAPI
    public static void blitSprite(GuiSprite sprite, GuiGraphics graphics, int x, int y)
    {
        blitSprite(sprite, graphics, 1.0F, x, y, sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Performs an inner blit operation for rendering a texture if the width and height are both not zero.
     *
     * @param sprite    The {@link GuiSprite} instance.
     * @param poseStack The {@link PoseStack} instance.
     * @param x         The x-coordinate of the top-left corner for the blit.
     * @param y         The y-coordinate of the top-left corner for the blit.
     * @param width     The width of what to blit.
     * @param height    The height of what to blit.
     */
    @SuppressWarnings("SameParameterValue")
    private static void blitSprite(GuiSprite sprite, PoseStack poseStack, int x, int y, int width, int height)
    {
        if (width != 0 && height != 0)
            innerBlit(sprite.atlasLocation(), poseStack, x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    /**
     * Performs an inner blit operation for rendering a tiled texture if the width and height are both not zero.
     *
     * @param sprite      The {@link GuiSprite} instance.
     * @param poseStack   The {@link PoseStack} instance.
     * @param sliceWidth  The slice width.
     * @param sliceHeight The slice height.
     * @param tileWidth   The tile width.
     * @param tileHeight  The tile height.
     * @param x           The x-coordinate of the top-left corner for the blit.
     * @param y           The y-coordinate of the top-left corner for the blit.
     * @param width       The width of what to blit.
     * @param height      The height of what to blit.
     */
    private static void blitSprite(GuiSprite sprite, PoseStack poseStack, int sliceWidth, int sliceHeight, int tileWidth, int tileHeight, int x, int y, int width, int height)
    {
        if (width == 0 || height == 0)
            return;

        float minU = sprite.getU((float) tileWidth / (float) sliceWidth);
        float maxU = sprite.getU((float) (tileWidth + width) / (float) sliceWidth);
        float minV = sprite.getV((float) tileHeight / (float) sliceHeight);
        float maxV = sprite.getV((float) (tileHeight + height) / (float) sliceHeight);

        innerBlit(sprite.atlasLocation(), poseStack, x, x + width, y, y + height, minU, maxU, minV, maxV);
    }

    /**
     * Performs an inner blit operation for rendering a texture with the specified scaling, coordinates, and texture
     * coordinates.
     *
     * @param atlasLocation The {@link ResourceLocation} of the texture atlas.
     * @param poseStack     The {@link PoseStack} instance.
     * @param x1            The x-coordinate of the first corner for the blit position.
     * @param x2            The x-coordinate of the second corner for the blit position.
     * @param y1            The y-coordinate of the first corner for the blit position.
     * @param y2            The y-coordinate of the second corner for the blit position.
     * @param minU          The minimum horizontal texture coordinate.
     * @param maxU          The maximum horizontal texture coordinate.
     * @param minV          The minimum vertical texture coordinate.
     * @param maxV          The maximum vertical texture coordinate.
     */
    private static void innerBlit(ResourceLocation atlasLocation, PoseStack poseStack, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV)
    {
        if (isBatching)
        {
            SpriteBuffer.create(getMatrix(poseStack), atlasLocation, x1, x2, y1, y2, minU, maxU, minV, maxV);
            return;
        }

        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();

        float[] rgba = RenderSystem.getShaderColor();
        int color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]).get();

        BufferBuilder builder = getTesselatorBuilder();
        Matrix4f matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        builder.vertex(matrix, x1, y1, 0.0F).color(color).uv(minU, minV).endVertex();
        builder.vertex(matrix, x1, y2, 0.0F).color(color).uv(minU, maxV).endVertex();
        builder.vertex(matrix, x2, y2, 0.0F).color(color).uv(maxU, maxV).endVertex();
        builder.vertex(matrix, x2, y1, 0.0F).color(color).uv(maxU, minV).endVertex();

        BufferUploader.drawWithShader(builder.end());
        RenderSystem.disableBlend();
    }

    /**
     * Builds vertices for the given builder using the given sprite data.
     *
     * @param builder The {@link BufferBuilder} instance.
     * @param buffer  The {@link SpriteBuffer} instance.
     */
    private static void innerBlit(BufferBuilder builder, SpriteBuffer buffer)
    {
        int color = new Color(buffer.rgba[0], buffer.rgba[1], buffer.rgba[2], buffer.rgba[3]).get();

        builder.vertex(buffer.matrix, buffer.x1, buffer.y1, 0.0F).uv(buffer.minU, buffer.minV).color(color).endVertex();
        builder.vertex(buffer.matrix, buffer.x1, buffer.y2, 0.0F).uv(buffer.minU, buffer.maxV).color(color).endVertex();
        builder.vertex(buffer.matrix, buffer.x2, buffer.y2, 0.0F).uv(buffer.maxU, buffer.maxV).color(color).endVertex();
        builder.vertex(buffer.matrix, buffer.x2, buffer.y1, 0.0F).uv(buffer.maxU, buffer.minV).color(color).endVertex();
    }

    /**
     * Performs an inner blit operation for rendering a tiled texture with the specified scaling, sprite, and tile
     * data.
     *
     * @param sprite       The {@link GuiSprite} instance.
     * @param poseStack    The {@link PoseStack} instance.
     * @param x            The x-coordinate of where to start the blit.
     * @param y            The y-coordinate of where to start the blit.
     * @param width        The width of the blit.
     * @param height       The height of the blit.
     * @param tileWidth    The tile width.
     * @param tileHeight   The tile height.
     * @param spriteWidth  The sprite width.
     * @param spriteHeight The sprite height.
     * @param sliceWidth   The slice width.
     * @param sliceHeight  The slice height.
     */
    private static void blitTiledSprite(GuiSprite sprite, PoseStack poseStack, int x, int y, int width, int height, int tileWidth, int tileHeight, int spriteWidth, int spriteHeight, int sliceWidth, int sliceHeight)
    {
        if (width <= 0 || height <= 0)
            return;

        if (spriteWidth > 0 && spriteHeight > 0)
        {
            for (int i = 0; i < width; i += spriteWidth)
            {
                int minWidth = Math.min(spriteWidth, width - i);

                for (int j = 0; j < height; j += spriteHeight)
                {
                    int minHeight = Math.min(spriteHeight, height - j);

                    blitSprite(sprite, poseStack, sliceWidth, sliceHeight, tileWidth, tileHeight, x + i, y + j, minWidth, minHeight);
                }
            }
        }
        else
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + spriteWidth + "x" + spriteHeight);
    }

    /**
     * Performs an inner blit operation for rendering a nine-slice texture with the specified scaling, sprite, and
     * nine-slice data.
     *
     * @param sprite    The {@link GuiSprite} instance.
     * @param nineSlice The {@link GuiSpriteScaling.NineSlice} instance.
     * @param poseStack The {@link PoseStack} instance.
     * @param x         The x-coordinate of where to start the blit.
     * @param y         The y-coordinate of where to start the blit.
     * @param width     The width of the blit.
     * @param height    The height of the blit.
     */
    @SuppressWarnings("SameParameterValue")
    private static void blitNineSlicedSprite(GuiSprite sprite, GuiSpriteScaling.NineSlice nineSlice, PoseStack poseStack, int x, int y, int width, int height)
    {
        GuiSpriteScaling.NineSlice.Border border = nineSlice.border();

        int left = Math.min(border.left(), width / 2);
        int right = Math.min(border.right(), width / 2);
        int top = Math.min(border.top(), height / 2);
        int bottom = Math.min(border.bottom(), height / 2);

        if (width == nineSlice.width() && height == nineSlice.height())
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height);
        else if (height == nineSlice.height())
        {
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, 0, x, y, left, height);
            blitTiledSprite(sprite, poseStack, x + left, y, width - right - left, height, left, 0, nineSlice.width() - right - left, nineSlice.height(), nineSlice.width(), nineSlice.height());
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), nineSlice.width() - right, 0, x + width - right, y, right, height);
        }
        else if (width == nineSlice.width())
        {
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, top);
            blitTiledSprite(sprite, poseStack, x, y + top, width, height - bottom - top, 0, top, nineSlice.width(), nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - bottom, x, y + height - bottom, width, bottom);
        }
        else
        {
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, 0, x, y, left, top);
            blitTiledSprite(sprite, poseStack, x + left, y, width - right - left, top, left, 0, nineSlice.width() - right - left, top, nineSlice.width(), nineSlice.height());
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), nineSlice.width() - right, 0, x + width - right, y, right, top);
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - bottom, x, y + height - bottom, left, bottom);
            blitTiledSprite(sprite, poseStack, x + left, y + height - bottom, width - right - left, bottom, left, nineSlice.height() - bottom, nineSlice.width() - right - left, bottom, nineSlice.width(), nineSlice.height());
            blitSprite(sprite, poseStack, nineSlice.width(), nineSlice.height(), nineSlice.width() - right, nineSlice.height() - bottom, x + width - right, y + height - bottom, right, bottom);
            blitTiledSprite(sprite, poseStack, x, y + top, left, height - bottom - top, 0, top, left, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
            blitTiledSprite(sprite, poseStack, x + left, y + top, width - right - left, height - bottom - top, left, top, nineSlice.width() - right - left, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
            blitTiledSprite(sprite, poseStack, x + width - right, y + top, left, height - bottom - top, nineSlice.width() - right, top, right, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
        }
    }

    /* Item Rendering */

    /**
     * Get a model view matrix based on the given (x, y) coordinate.
     *
     * @param poseStack The current {@link PoseStack}.
     * @param x         Where the rendering starts relative to the x-axis.
     * @param y         Where the rendering starts relative to the y-axis.
     * @return A new {@link Matrix4f} instance based on the render system's model view.
     */
    private static Matrix4f getModelViewMatrix(PoseStack poseStack, int x, int y)
    {
        double zOffset = 0.0D;

        if (MatrixUtil.getZ(poseStack) < 10.0D)
            zOffset = 10.0D - zOffset;

        if (Minecraft.getInstance().screen instanceof GuiOffset offset)
            zOffset += offset.getZOffset();

        poseStack.pushPose();
        poseStack.translate(x, y, 0.0F);
        poseStack.translate(8.0D, 8.0D, zOffset);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);

        Matrix4f matrix = new Matrix4f(poseStack.last().pose());
        poseStack.popPose();

        return matrix;
    }

    /**
     * Get a packed light integer based on the given normalized brightness value.
     *
     * @param brightness A normalized [0.0F-1.0F] brightness.
     * @return A packed light integer that will be applied to item rendering.
     */
    @PublicAPI
    public static int getItemModelBrightness(float brightness)
    {
        int light = Mth.clamp(Math.round(15.0F * brightness), 0, 15);
        return light << 4 | light << 20;
    }

    /**
     * Render an item to the screen.
     *
     * @param graphics  A {@link GuiGraphics} instance.
     * @param itemStack An {@link ItemStack} instance.
     * @param x         Where the rendering starts relative to the x-axis.
     * @param y         Where the rendering starts relative to the y-axis.
     */
    @PublicAPI
    public static void renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y)
    {
        renderItem(graphics, itemStack, x, y, getItemModelBrightness(1.0F));
    }

    /**
     * Render an item to the screen.
     *
     * @param graphics   A {@link GuiGraphics} instance.
     * @param itemStack  An {@link ItemStack} instance.
     * @param x          Where the rendering starts relative to the x-axis.
     * @param y          Where the rendering starts relative to the y-axis.
     * @param brightness A normalized [0.0F-1.0F] brightness (<i>the {@code float} is clamped</i>).
     */
    @PublicAPI
    public static void renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y, float brightness)
    {
        renderItem(graphics, itemStack, x, y, getItemModelBrightness(brightness));
    }

    /**
     * Render an item to the screen.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param itemStack   An {@link ItemStack} instance.
     * @param x           Where the rendering starts relative to the x-axis.
     * @param y           Where the rendering starts relative to the y-axis.
     * @param packedLight A packed light integer that will be applied to item rendering.
     */
    @PublicAPI
    public static void renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y, int packedLight)
    {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(itemStack, null, null, 0);
        boolean isLightingFlat = !model.usesBlockLight();

        if (!isBatching)
        {
            if (isLightingFlat)
                Lighting.setupForFlatItems();

            PoseStack viewStack = new PoseStack();
            viewStack.last().pose().set(getModelViewMatrix(graphics.pose(), x, y));

            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            renderer.render(itemStack, ItemDisplayContext.GUI, false, viewStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
            buffer.endBatch();

            if (isLightingFlat)
                Lighting.setupFor3DItems();
        }
        else
            ItemBuffer.create(graphics, itemStack, model, x, y, packedLight);
    }
}

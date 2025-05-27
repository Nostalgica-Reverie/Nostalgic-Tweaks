package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.text.FallingBlockText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.MatrixUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class FallingBlockRenderer
{
    /* Fields */

    /**
     * The random source instance for the logo renderer.
     */
    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    /**
     * A two-dimensional array that holds falling block effect data. Array is set up in [x][y] format.
     */
    private final FallingEffect[][] logoEffects;

    /**
     * Stores the title screen logo characters.
     */
    private final List<String> logo;

    /**
     * The number of lines in the logo.
     */
    private final int height;

    /**
     * The length of the longest line in the logo.
     */
    private final int width;

    /**
     * Whether the animation should be skipped.
     */
    private final boolean immediate;

    /* Constructor */

    /**
     * Create a new {@link FallingBlockRenderer} instance using the default falling block settings. The data for this
     * form of the renderer uses the mod's {@code assets/texts/logo.txt} file.
     */
    public FallingBlockRenderer()
    {
        this.logo = FallingBlockText.getInstance().logo();
        this.width = FallingBlockText.getInstance().longestLine();
        this.height = FallingBlockText.getInstance().size();
        this.immediate = false;

        this.logoEffects = new FallingEffect[this.width][this.height];

        for (int x = 0; x < this.logoEffects.length; x++)
        {
            for (int y = 0; y < this.logoEffects[x].length; y++)
                this.logoEffects[x][y] = new FallingEffect(x, y, false, "#000000FF", Blocks.STONE.defaultBlockState());
        }
    }

    /**
     * Create a new {@link FallingBlockRenderer} instance using custom falling block data. The data for this form of the
     * renderer uses the {@code config/logo/falling_blocks.json} config file to determine logo visuals.
     *
     * @param blocks    The {@link ArrayList} of {@link FallingBlockData.Block} instances.
     * @param immediate Whether the animation should be skipped entirely.
     */
    public FallingBlockRenderer(ArrayList<FallingBlockData.Block> blocks, boolean immediate)
    {
        this.logo = new ArrayList<>();
        this.immediate = immediate;

        final Rectangle border = Rectangle.fromCollection(blocks, FallingBlockData.Block::getX, FallingBlockData.Block::getY);

        this.width = border.getWidth() + 1;
        this.height = border.getHeight() + 1;

        ArrayList<FallingBlockData.Block> trimmed = new ArrayList<>();

        blocks.forEach(block -> {
            FallingBlockData.Block copy = block.copy();

            copy.trimX(border.startX());
            copy.trimY(border.startY());
            trimmed.add(copy);
        });

        for (int y = 0; y < this.height; y++)
        {
            final StringBuilder lineBuilder = new StringBuilder();

            for (int x = 0; x < this.width; x++)
            {
                final int relX = x;
                final int relY = y;

                trimmed.stream()
                    .filter(block -> block.at(relX, relY))
                    .findFirst()
                    .ifPresentOrElse(block -> lineBuilder.append("#"), () -> lineBuilder.append(" "));
            }

            this.logo.add(lineBuilder.toString());
        }

        this.logoEffects = new FallingEffect[this.width][this.height];

        for (int x = 0; x < this.logoEffects.length; x++)
        {
            for (int y = 0; y < this.logoEffects[x].length; y++)
            {
                final int relX = x;
                final int relY = y;

                trimmed.stream().filter(block -> block.at(relX, relY)).findFirst().ifPresentOrElse(block -> {
                    boolean hasSound = block.hasSound();
                    String shadowColor = block.getShadowColor();
                    BlockState blockState = BlockUtil.getBlock(block.getBlockId()).defaultBlockState();

                    this.logoEffects[relX][relY] = new FallingEffect(relX, relY, hasSound, shadowColor, blockState);
                }, () -> {
                    BlockState blockState = Blocks.AIR.defaultBlockState();

                    this.logoEffects[relX][relY] = new FallingEffect(relX, relY, false, "#000000FF", blockState);
                });
            }
        }
    }

    /**
     * Create a new {@link FallingBlockRenderer} instance using custom falling block data. The data for this form of the
     * renderer uses the {@code config/logo/falling_blocks.json} config file to determine logo visuals.
     *
     * @param blocks The {@link ArrayList} of {@link FallingBlockData.Block} instances.
     */
    public FallingBlockRenderer(ArrayList<FallingBlockData.Block> blocks)
    {
        this(blocks, false);
    }

    /* Methods */

    /**
     * @return Whether all falling blocks have "fallen from the sky" and the animation is finished.
     */
    public boolean isFinished()
    {
        for (FallingEffect[] logoEffect : this.logoEffects)
        {
            for (FallingEffect logoEffectRandomizer : logoEffect)
            {
                if (!logoEffectRandomizer.hasFallen())
                    return false;
            }
        }

        return true;
    }

    /**
     * Renders the old logo and its falling animation.
     */
    public void render()
    {
        render(0.0F);
    }

    /**
     * Renders the old logo and its falling animation.
     *
     * @param yOffset Offset the falling block animation vertically.
     */
    public void render(float yOffset)
    {
        if (this.logo.isEmpty())
            return;

        for (FallingEffect[] logoEffect : this.logoEffects)
        {
            for (FallingEffect logoEffectRandomizer : logoEffect)
                logoEffectRandomizer.update();
        }

        Window window = GuiUtil.getWindow();
        int scaleHeight = (int) (120 * window.getGuiScale());

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().perspective(70.341F, window.getWidth() / (float) scaleHeight, 0.05F, 100.0F), VertexSorting.DISTANCE_TO_ORIGIN);
        RenderSystem.viewport(0, window.getHeight() - scaleHeight, window.getWidth(), scaleHeight);

        PoseStack poseStack = new PoseStack();
        Tesselator tesselator = Tesselator.getInstance();
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        NullableHolder<BufferBuilder> builder = NullableHolder.empty();
        float zOffset = MatrixUtil.getZ(modelViewStack);

        modelViewStack.pushMatrix();
        modelViewStack.translate(-0.05F, 0.78F + yOffset, (-1.0F * zOffset) - 10.0F);
        modelViewStack.scale(1.32F, 1.32F, 1.32F);

        poseStack.mulPose(modelViewStack);

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(true);

        final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.7F, 1.0F, 0.7F).normalize();
        final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-1.0F, 0.6F, 1.0F).normalize();

        RenderSystem.setShaderLights(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);

        for (int pass = 0; pass < 2; pass++)
        {
            poseStack.pushPose();

            if (pass == 0)
            {
                RenderSystem.clear(256, Minecraft.ON_OSX);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);

                poseStack.translate(0.0F, -0.4F, 0.0F);
                poseStack.scale(0.98F, 1.0F, 1.0F);

                builder.set(tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR));
            }

            if (pass == 1)
            {
                builder.ifPresent(buffer -> NullableAction.attempt(buffer.build(), BufferUploader::drawWithShader));

                RenderSystem.disableBlend();
                RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            poseStack.scale(-1.0F, 1.0F, -1.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(15.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

            poseStack.scale(0.89F, 1.0F, 0.4F);
            poseStack.translate((float) (-this.width) * 0.5F, (float) (this.height) * 0.5F, 0.0F);

            for (int y = 0; y < this.height; y++)
            {
                for (int x = 0; x < this.logo.get(y).length(); x++)
                {
                    if (this.logo.get(y).charAt(x) == ' ')
                        continue;

                    poseStack.pushPose();

                    FallingEffect effect = this.logoEffects[x][y];

                    float z = (float) effect.pos;
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (pass == 0)
                    {
                        scale = z * 0.04F + 1.0F;
                        alpha = Mth.clamp(1.0F / scale, 0.0F, 0.7F);
                        z = 0.0F;
                    }

                    poseStack.translate(x + 1, -y - 1, z + 1);
                    poseStack.scale(-scale, scale, -scale);

                    this.renderBlock(effect, builder.getOrThrow(), poseStack, pass, alpha);

                    poseStack.popPose();
                }
            }

            if (pass == 1)
                Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

            poseStack.popPose();
        }

        RenderSystem.restoreProjectionMatrix();
        RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());

        poseStack.setIdentity();
        poseStack.translate(0.0F, 0.0F, zOffset);

        modelViewStack.popMatrix();

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();

        Lighting.setupFor3DItems();
    }

    /**
     * Renders a block for the title logo.
     *
     * @param effect    The {@link FallingEffect} instance to get falling block data from.
     * @param builder   The {@link BufferBuilder} instance to batch vertices to.
     * @param poseStack The {@link PoseStack} model view instance.
     * @param pass      The render pass index.
     * @param alpha     The transparency of the shadow block.
     */
    private void renderBlock(FallingEffect effect, BufferBuilder builder, PoseStack poseStack, int pass, float alpha)
    {
        if (pass == 0)
        {
            renderShadow(effect, builder, poseStack, alpha);
            return;
        }

        if (effect.blockState.getRenderShape() != RenderShape.MODEL)
            return;

        RandomSource randomSource = RandomSource.create();
        BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(effect.blockState);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(effect.blockState, false);
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(renderType);

        int color = Minecraft.getInstance().getBlockColors().getColor(effect.blockState, null, null, 0);
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        PoseStack.Pose pose = poseStack.last();

        for (Direction direction : Direction.values())
        {
            randomSource.setSeed(42L);
            renderQuads(pose, vertexConsumer, red, green, blue, bakedModel.getQuads(effect.blockState, direction, randomSource));
        }

        randomSource.setSeed(42L);
        renderQuads(pose, vertexConsumer, red, green, blue, bakedModel.getQuads(effect.blockState, null, randomSource));
    }

    /**
     * Renders the given list of quads.
     */
    private static void renderQuads(PoseStack.Pose pose, VertexConsumer consumer, float red, float green, float blue, List<BakedQuad> quads)
    {
        for (BakedQuad quad : quads)
        {
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;

            if (quad.isTinted())
            {
                r = Mth.clamp(red, 0.0F, 1.0F);
                g = Mth.clamp(green, 0.0F, 1.0F);
                b = Mth.clamp(blue, 0.0F, 1.0F);
            }

            consumer.putBulkData(pose, quad, r, g, b, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }

    /**
     * Renders a shadow cube.
     *
     * @param effect    The {@link FallingEffect} associated with this shadow.
     * @param builder   The {@link BufferBuilder} instance.
     * @param poseStack The {@link PoseStack} model view instance.
     * @param alpha     The transparency amount of the shadow.
     */
    private static void renderShadow(FallingEffect effect, BufferBuilder builder, PoseStack poseStack, float alpha)
    {
        if (effect.alpha == 0.0F)
            return;

        int color = new Color(effect.red, effect.green, effect.blue, effect.alpha * alpha).get();
        Matrix4f matrix = poseStack.last().pose();

        // Front Face
        builder.addVertex(matrix, 0, 0, 0).setColor(color);
        builder.addVertex(matrix, 1, 0, 0).setColor(color);
        builder.addVertex(matrix, 1, 0, 1).setColor(color);
        builder.addVertex(matrix, 0, 0, 1).setColor(color);

        // Back Face
        builder.addVertex(matrix, 0, 1, 0).setColor(color);
        builder.addVertex(matrix, 1, 1, 0).setColor(color);
        builder.addVertex(matrix, 1, 1, 1).setColor(color);
        builder.addVertex(matrix, 0, 1, 1).setColor(color);

        // Top Face
        builder.addVertex(matrix, 0, 0, 1).setColor(color);
        builder.addVertex(matrix, 1, 0, 1).setColor(color);
        builder.addVertex(matrix, 1, 1, 1).setColor(color);
        builder.addVertex(matrix, 0, 1, 1).setColor(color);

        // Bottom Face
        builder.addVertex(matrix, 0, 0, 0).setColor(color);
        builder.addVertex(matrix, 1, 0, 0).setColor(color);
        builder.addVertex(matrix, 1, 1, 0).setColor(color);
        builder.addVertex(matrix, 0, 1, 0).setColor(color);

        // Left Face
        builder.addVertex(matrix, 0, 0, 0).setColor(color);
        builder.addVertex(matrix, 0, 1, 0).setColor(color);
        builder.addVertex(matrix, 0, 1, 1).setColor(color);
        builder.addVertex(matrix, 0, 0, 1).setColor(color);

        // Right Face
        builder.addVertex(matrix, 1, 0, 0).setColor(color);
        builder.addVertex(matrix, 1, 1, 0).setColor(color);
        builder.addVertex(matrix, 1, 1, 1).setColor(color);
        builder.addVertex(matrix, 1, 0, 1).setColor(color);
    }

    /* Randomizer */

    private class FallingEffect
    {
        /* Fields */

        public double prevPos;
        public double pos;
        public double speed;

        public final boolean sound;
        public final float red;
        public final float green;
        public final float blue;
        public final float alpha;

        public final BlockState blockState;

        /* Constructor */

        /**
         * Create a new logo effect randomizer instance.
         *
         * @param x The starting x-position.
         * @param y The starting y-position.
         */
        public FallingEffect(int x, int y, boolean sound, String shadowColor, BlockState blockState)
        {
            this.pos = (10.0D + y) + RANDOM_SOURCE.nextDouble() * 32.0D + (double) x;
            this.prevPos = this.pos;

            this.sound = sound;
            this.blockState = blockState;

            float[] rgba = HexUtil.parseFloatRGBA(shadowColor);

            this.red = rgba[0];
            this.green = rgba[1];
            this.blue = rgba[2];
            this.alpha = rgba[3];

            if (FallingBlockRenderer.this.immediate)
            {
                this.pos = 0.0D;
                this.prevPos = 0.0D;
            }
        }

        /**
         * Update the position of this randomizer instance.
         */
        public void update()
        {
            this.prevPos = this.pos;

            if (this.pos > 0.0D)
                this.speed -= 0.4D;

            this.pos += this.speed * PartialTick.realtime();
            this.speed *= 0.9D;

            if (this.pos < 0.0D)
            {
                this.pos = 0.0D;
                this.speed = 0.0D;

                if (this.prevPos > 0.0D && this.sound)
                {
                    Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(this.blockState.getSoundType().getPlaceSound(), 1.0F, 0.2F));
                }
            }
        }

        /**
         * @return Whether this effect is finished and has completely "fallen from the sky."
         */
        public boolean hasFallen()
        {
            return this.pos <= 0.0D && this.speed <= 0.0D;
        }
    }
}

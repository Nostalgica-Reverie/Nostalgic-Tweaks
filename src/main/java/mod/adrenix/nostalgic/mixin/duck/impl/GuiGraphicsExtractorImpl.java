package mod.adrenix.nostalgic.mixin.duck.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import mod.adrenix.nostalgic.mixin.duck.RenderUtil;
import mod.adrenix.nostalgic.util.client.renderer.state.HorizontalColoredRectangleRenderState;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorImpl implements RenderUtil {
    @Shadow @Final
    private Matrix3x2fStack pose;
    @Shadow @Final
    private GuiRenderState guiRenderState;
    @Shadow @Final
    private GuiGraphicsExtractor.ScissorStack scissorStack;

    @Shadow
    public abstract void fill(int x0, int y0, int x1, int y1, int col);

    @Shadow
    public abstract void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2);

    @Override
    public void nt$outline(int x, int y, int width, int height, int thickness, int color) {
        this.fill(x, y, x + width, y + thickness, color);
        this.fill(x, y + height - thickness, x + width, y + height, color);
        this.fill(x, y + thickness, x + thickness, y + height - thickness, color);
        this.fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color);
    }

    @Override
    public void nt$fillGradient(Gradient gradient, int x0, int y0, int x1, int y1) {
        int from = gradient.from().get();
        int to = gradient.to().get();

        switch (gradient.direction()) {
            case VERTICAL -> this.fillGradient(x0, y0, x1, y1, from, to);
            case HORIZONTAL -> this.nt$fillHorizontalGradient(x0, y0, x1, y1, from, to);
        }
    }

    @Override
    public void nt$fillHorizontalGradient(int x0, int y0, int x1, int y1, int colorFrom, int colorTo) {
        this.nt$innerFillHorizontal(RenderPipelines.GUI, TextureSetup.noTexture(), x0, y0, x1, y1, colorFrom, colorTo);
    }

    @SuppressWarnings("SameParameterValue")
    @Unique
    private void nt$innerFillHorizontal(
        RenderPipeline renderPipeline,
        TextureSetup textureSetup,
        int x0, int y0,
        int x1, int y1,
        int color1,
        @Nullable Integer color2
    ) {
        this.guiRenderState.addGuiElement(new HorizontalColoredRectangleRenderState(
                renderPipeline, textureSetup, new Matrix3x2f(this.pose),
                x0, y0, x1, y1, color1, color2 != null ? color2 : color1, this.scissorStack.peek()));
    }
}

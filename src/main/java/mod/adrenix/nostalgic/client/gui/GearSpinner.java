package mod.adrenix.nostalgic.client.gui;

import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GearSpinner {
    /* Static */

    private final static HashMap<Integer, Identifier> GEAR_IMAGES = new HashMap<>();

    /* Fields */
    private final static GearSpinner GEAR_SPINNER = new GearSpinner();
    private final SimpleTimer timer;

    /* Singleton */
    private int frame;

    private GearSpinner() {
        this.timer = SimpleTimer.create(30L, TimeUnit.MILLISECONDS).build();
        this.frame = 0;

        if (GEAR_IMAGES.isEmpty()) {
            for (int i = 0; i < 16; i++)
                GEAR_IMAGES.put(i, LocateResource.mod("textures/gear/" + String.format("%s.png", i)));
        }
    }

    /**
     * The spinner is used by multiple user interfaces. A singleton instance keeps the spinning in sync.
     *
     * @return The singleton gear spinner instance.
     */
    public static GearSpinner getInstance() {
        return GEAR_SPINNER;
    }

    /* Methods */

    /**
     * Render the spinning gear logo.
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     * @param scale    The scale to render the gear at.
     * @param x        The x-coordinate of the top-left part of the gear.
     * @param y        The y-coordinate of the top-left part of the gear.
     */
    public void extractRenderState(GuiGraphicsExtractor graphics, float scale, int x, int y) {
        if (this.frame > 15)
            this.frame = 0;

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GEAR_IMAGES.get(this.frame), 0, 0, 0, 0, 512, 512, 512, 512);
        graphics.pose().popMatrix();

        if (this.timer.hasElapsed())
            this.frame++;
    }
}

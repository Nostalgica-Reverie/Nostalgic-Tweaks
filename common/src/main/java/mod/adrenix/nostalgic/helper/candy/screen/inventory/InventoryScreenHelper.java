package mod.adrenix.nostalgic.helper.candy.screen.inventory;

import mod.adrenix.nostalgic.mixin.access.AbstractContainerScreenAccess;
import mod.adrenix.nostalgic.mixin.access.ImageButtonAccess;
import mod.adrenix.nostalgic.mixin.access.InventoryScreenAccess;
import mod.adrenix.nostalgic.mixin.access.ScreenAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.InventoryShield;
import mod.adrenix.nostalgic.tweak.enums.RecipeBook;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.sprite.WidgetSprites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This utility class is used only by the client.
 */
public abstract class InventoryScreenHelper
{
    /* Sprites */

    private static final WidgetSprites SMALL_RECIPE_BUTTON = new WidgetSprites(ModSprite.RECIPE_BUTTON_SMALL, ModSprite.RECIPE_BUTTON_SMALL_HIGHLIGHTED);
    private static final WidgetSprites LARGE_RECIPE_BUTTON = new WidgetSprites(ModSprite.RECIPE_BUTTON_LARGE, ModSprite.RECIPE_BUTTON_LARGE_HIGHLIGHTED);

    /* Sprite Button */

    private static class SpriteButton extends ImageButton
    {
        private final WidgetSprites sprites;

        private SpriteButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress)
        {
            super(x, y, width, height, 0, 0, LocateResource.game("textures/gui/recipe_button"), onPress);

            this.sprites = sprites;
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            RenderUtil.blitSprite(this.sprites.get(this.isActive(), this.isHoveredOrFocused()), graphics, this.getX(), this.getY(), this.width, this.height);
        }
    }

    /* Methods */

    /**
     * Set the inventory's screen slot positions based on tweak context.
     *
     * @param slots   A {@link NonNullList} of {@link Slot}.
     * @param recipes The inventory {@link RecipeBookComponent} instance.
     * @return The offhand {@link Slot} instance to use, or {@code null}.
     */
    @Nullable
    public static Slot setPositionsForSlots(NonNullList<Slot> slots, RecipeBookComponent recipes)
    {
        InventoryShield shield = CandyTweak.INVENTORY_SHIELD.get();
        Slot offHand = null;

        for (Slot slot : slots)
        {
            SlotTracker.CRAFT_TOP_LEFT.moveOrReset(slot, 88, 26);
            SlotTracker.CRAFT_TOP_RIGHT.moveOrReset(slot, 106, 26);
            SlotTracker.CRAFT_BOTTOM_LEFT.moveOrReset(slot, 88, 44);
            SlotTracker.CRAFT_BOTTOM_RIGHT.moveOrReset(slot, 106, 44);
            SlotTracker.CRAFT_RESULT.moveOrReset(slot, 144, 36);

            if (SlotTracker.OFF_HAND.isEqualTo(slot))
            {
                offHand = slot;

                if (InventoryShield.MODERN == shield)
                    SlotTracker.OFF_HAND.moveOrReset(slot, 152, 62);

                switch (shield)
                {
                    case INVISIBLE -> SlotTracker.OFF_HAND.move(slot, -9999, -9999);
                    case MIDDLE_RIGHT -> SlotTracker.OFF_HAND.move(slot, 152, 62);
                    case BOTTOM_LEFT ->
                    {
                        if (recipes.isVisible())
                            SlotTracker.OFF_HAND.move(slot, 174, 142);
                        else
                            SlotTracker.OFF_HAND.move(slot, -14, 142);
                    }
                }
            }
        }

        return offHand;
    }

    /**
     * Get a new image button that represents the "large" recipe button.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     * @param original  The original {@link ImageButton} instance.
     * @return A new {@link ImageButton} instance.
     */
    private static ImageButton getLargeBook(AbstractContainerScreenAccess inventory, ImageButton original)
    {
        Button.OnPress onPress = button -> {
            original.onPress();
            button.setPosition(inventory.nt$getLeftPos() + 151, inventory.nt$getTopPos() + 7);
        };

        return new SpriteButton(inventory.nt$getLeftPos() + 151, inventory.nt$getTopPos() + 7, 18, 18, LARGE_RECIPE_BUTTON, onPress);
    }

    /**
     * Get a new image button that represents the "small" recipe button.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     * @param original  The original {@link ImageButton} instance.
     * @return A new {@link ImageButton} instance.
     */
    private static ImageButton getSmallBook(AbstractContainerScreenAccess inventory, ImageButton original)
    {
        Button.OnPress onPress = button -> {
            original.onPress();
            button.setPosition(inventory.nt$getLeftPos() + 160, inventory.nt$getTopPos() + 7);
        };

        return new SpriteButton(inventory.nt$getLeftPos() + 160, inventory.nt$getTopPos() + 7, 9, 10, SMALL_RECIPE_BUTTON, onPress);
    }

    /**
     * Set the inventory screen's recipe button to use.
     *
     * @param inventory The inventory {@link AbstractContainerScreenAccess} instance.
     * @param bookType  The {@link RecipeBook} value to use.
     */
    public static void setRecipeButton(AbstractContainerScreenAccess inventory, RecipeBook bookType)
    {
        ImageButton recipeButton = null;
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null)
            throw new NullPointerException("Tried adding recipe book button to a null screen");

        ResourceLocation bookLocation = InventoryScreenAccess.NT$RECIPE_BUTTON_LOCATION();

        for (GuiEventListener widget : screen.children())
        {
            if (widget instanceof ImageButton button)
            {
                if (((ImageButtonAccess) button).nt$getResourceLocation().equals(bookLocation))
                {
                    recipeButton = button;
                    break;
                }
            }
        }

        if (recipeButton == null)
            return;

        switch (bookType)
        {
            case DISABLED ->
            {
                recipeButton.active = false;
                recipeButton.visible = false;
            }
            case LARGE ->
            {
                ((ScreenAccess) screen).nt$removeWidget(recipeButton);
                ((ScreenAccess) screen).nt$addRenderableWidget(getLargeBook(inventory, recipeButton));
            }
            case SMALL ->
            {
                ((ScreenAccess) screen).nt$removeWidget(recipeButton);
                ((ScreenAccess) screen).nt$addRenderableWidget(getSmallBook(inventory, recipeButton));
            }
        }
    }

    /**
     * Render the off-hand slot.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param recipes  The inventory {@link RecipeBookComponent} instance.
     * @param offHand  The off-hand {@link Slot}.
     * @param leftPos  The left-position of the screen.
     * @param height   The height of the screen.
     */
    public static void renderOffHandSlot(GuiGraphics graphics, RecipeBookComponent recipes, @Nullable Slot offHand, int leftPos, int height)
    {
        InventoryShield shield = CandyTweak.INVENTORY_SHIELD.get();
        boolean isModernOverride = CandyTweak.OLD_INVENTORY.get() && shield == InventoryShield.MODERN;

        if (InventoryShield.BOTTOM_LEFT == shield && offHand != null)
        {
            if (recipes.isVisible())
            {
                graphics.blit(TextureLocation.INVENTORY, leftPos + 172, height / 2 + 51, 200, 33, 26, 32);
                SlotTracker.OFF_HAND.move(offHand, 174, 142);
            }
            else
            {
                graphics.blit(TextureLocation.INVENTORY, leftPos - 22, height / 2 + 51, 200, 0, 25, 32);
                SlotTracker.OFF_HAND.move(offHand, -14, 142);
            }
        }
        else if (InventoryShield.MIDDLE_RIGHT == shield || isModernOverride)
            graphics.blit(TextureLocation.INVENTORY, leftPos + 151, height / 2 - 22, 178, 56, 18, 18);

        if (!CandyTweak.OLD_INVENTORY.get() && InventoryShield.MODERN != shield)
            graphics.blit(TextureLocation.INVENTORY, leftPos + 76, height / 2 - 22, 178, 74, 18, 18);
    }
}

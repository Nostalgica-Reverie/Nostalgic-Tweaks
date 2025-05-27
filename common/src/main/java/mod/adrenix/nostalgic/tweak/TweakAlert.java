package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.FogColor;
import mod.adrenix.nostalgic.tweak.enums.InventoryShield;
import mod.adrenix.nostalgic.tweak.enums.SkyColor;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Defines the conditions of when an alert tag should be displayed. These tags help identify tweaks that will not work
 * as intended if a certain condition (such as another tweak being disabled) is met.
 */
public enum TweakAlert
{
    NONE(TweakAlert::none, Lang.Alert.NONE),
    ROW_HIGHLIGHT_DISABLED(TweakAlert::isRowHighlightDisabled, Lang.Alert.ROW_HIGHLIGHT_DISABLED),
    WINDOW_TITLE_DISABLED(TweakAlert::isWindowTitleDisabled, Lang.Alert.WINDOW_TITLE_DISABLED),
    CUSTOM_GUI_DISABLED(TweakAlert::getCustomGuiDisabled, Lang.Alert.CUSTOM_GUI_GRADIENT),
    SHIELD_CONFLICT(TweakAlert::isShieldConflict, Lang.Alert.SHIELD),
    BRIGHTNESS_CONFLICT(TweakAlert::isBrightnessConflict, Lang.Alert.BRIGHTNESS),
    DYNAMIC_FOG(TweakAlert::isDynamicFogConflict, Lang.Alert.DYNAMIC_FOG),
    DYNAMIC_SKY(TweakAlert::isDynamicSkyConflict, Lang.Alert.DYNAMIC_SKY),
    UNIVERSAL_FOG(TweakAlert::isUniversalFogConflict, Lang.Alert.UNIVERSAL_FOG),
    UNIVERSAL_SKY(TweakAlert::isUniversalSkyConflict, Lang.Alert.UNIVERSAL_SKY),
    OVERWORLD_FOG_DENSITY(TweakAlert::isOverworldFogDensityConflict, Lang.Alert.OVERWORLD_FOG_DENSITY),
    OVERWORLD_FOG_COLOR(TweakAlert::isOverworldFogColorConflict, Lang.Alert.OVERWORLD_FOG_COLOR),
    NETHER_FOG_DENSITY(TweakAlert::isNetherFogDensityConflict, Lang.Alert.NETHER_FOG_DENSITY),
    NETHER_FOG_COLOR(TweakAlert::isNetherFogColorConflict, Lang.Alert.NETHER_FOG_COLOR),
    VOID_CONFLICT(TweakAlert::isVoidConflict, Lang.Alert.VOID),
    FOOD_HEALTH_CONFLICT(TweakAlert::isCustomFoodHealthConflict, Lang.Alert.FOOD_HEALTH),
    FOOD_STACKING_CONFLICT(TweakAlert::isCustomFoodStackingConflict, Lang.Alert.FOOD_STACKING),
    ARM_SWAY_CONFLICT(TweakAlert::isArmSwayConflict, Lang.Alert.ARM_SWAY),
    STAMINA_SPRINTING_CONFLICT(TweakAlert::isStaminaSprintingConflict, Lang.Alert.STAMINA_SPRINTING),
    CUSTOM_FALLING_LOGO_DISABLED(TweakAlert::isCustomFallingLogoDisabled, Lang.Alert.CUSTOM_FALLING_LOGO_DISABLED);

    /* Fields */

    private final Supplier<Boolean> condition;
    private final Translation langKey;

    /* Constructor */

    /**
     * Create a new tweak alert enumeration.
     *
     * @param condition A supplier that returns a truthful boolean when an alert tag needs displayed.
     * @param langKey   The lang key instance associated with this alert instance.
     */
    TweakAlert(Supplier<Boolean> condition, Translation langKey)
    {
        this.condition = condition;
        this.langKey = langKey;
    }

    /* Methods */

    /**
     * @return A supplier that returns a truthful boolean when an alert tag needs displayed.
     */
    public Supplier<Boolean> getCondition()
    {
        return this.condition;
    }

    /**
     * @return The message component associated with this tweak alert instance.
     */
    public Component getMessage()
    {
        return this.langKey.get();
    }

    /* Conditions */

    /**
     * This is <i>reserved</i> for the {@link TweakAlert#NONE} definition.
     */
    private static boolean none()
    {
        return false;
    }

    /**
     * Checks if the custom falling logo off, or if the old alpha logo is off. If either is, then the custom falling
     * block logo will not be shown on the title screen.
     */
    private static boolean isCustomFallingLogoDisabled()
    {
        boolean isCustomFallingLogoOff = !CandyTweak.USE_CUSTOM_FALLING_LOGO.fromCache();
        boolean isOldAlphaLogoOff = !CandyTweak.OLD_ALPHA_LOGO.fromCache();

        return isCustomFallingLogoOff || isOldAlphaLogoOff;
    }

    /**
     * Checks if the override row highlight is disabled. If it is, then custom row highlighting opacity will not work.
     */
    private static boolean isRowHighlightDisabled()
    {
        return !ModTweak.OVERRIDE_ROW_HIGHLIGHT.fromCache();
    }

    /**
     * An alert tag will be displayed by window title tweaks if the enable window title tweak is disabled.
     */
    private static boolean isWindowTitleDisabled()
    {
        return !CandyTweak.ENABLE_WINDOW_TITLE.fromCache();
    }

    /**
     * Checks if the custom screen background is disabled. If it is, then custom gradient colors cannot be applied.
     */
    private static boolean getCustomGuiDisabled()
    {
        return !CandyTweak.CUSTOM_GUI_BACKGROUND.fromCache();
    }

    /**
     * Checks if the user has selected the modern shield position while the old inventory tweak is enabled.
     * <p>
     * The inventory slot cannot be in the modern position since it will be too close to the crafting grid and overlap
     * the wider black player box.
     */
    private static boolean isShieldConflict()
    {
        boolean isModernShield = CandyTweak.INVENTORY_SHIELD.fromCache() == InventoryShield.MODERN;
        boolean isOldInventory = CandyTweak.OLD_INVENTORY.fromCache();

        return isModernShield && isOldInventory;
    }

    /**
     * Checks if the user has old lighting engine disabled and disabled vanilla brightness enabled.
     * <p>
     * Having disabled vanilla brightness enabled without old lighting engine enabled will not work since the brightness
     * tweak does not impact the vanilla lightmap calculations.
     */
    private static boolean isBrightnessConflict()
    {
        boolean isOldLightColor = CandyTweak.OLD_LIGHT_COLOR.fromCache();
        boolean isDisabledBrightness = CandyTweak.DISABLE_LIGHT_BRIGHTNESS.fromCache();
        boolean isDynamicBrightness = CandyTweak.DYNAMIC_LIGHT_BRIGHTNESS.fromCache();

        return !isOldLightColor && (isDisabledBrightness || isDynamicBrightness);
    }

    /**
     * Checks if the user has both the dark void height and blue void override tweaks enabled.
     * <p>
     * Having both enabled will not work since the blue void will always override the dark void. Therefore, the dark
     * void cannot render.
     */
    private static boolean isVoidConflict()
    {
        boolean isDarkVoidHeight = CandyTweak.OLD_DARK_VOID_HEIGHT.fromCache();
        boolean isBlueVoidOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.fromCache();

        return isDarkVoidHeight && isBlueVoidOverride;
    }

    /**
     * Checks if the user has dynamic fog or custom fog tweaks enabled. Having either enabled will not work because
     * dynamic and custom fog will override universal fog.
     */
    private static boolean isUniversalFogConflict()
    {
        FogColor fogColor = CandyTweak.UNIVERSAL_FOG_COLOR.fromCache();
        boolean isFogCustom = CandyTweak.USE_CUSTOM_OVERWORLD_FOG_COLOR.fromCache();
        boolean isFogDynamic = CandyTweak.OLD_DYNAMIC_FOG_COLOR.fromCache();

        return fogColor != FogColor.DISABLED && (isFogCustom || isFogDynamic);
    }

    /**
     * Checks if the user has dynamic sky or custom sky tweaks enabled. Having either enabled will not work because
     * dynamic and custom sky will override universal sky.
     */
    private static boolean isUniversalSkyConflict()
    {
        SkyColor skyColor = CandyTweak.UNIVERSAL_SKY_COLOR.fromCache();
        boolean isSkyCustom = CandyTweak.CUSTOM_OVERWORLD_SKY.fromCache();
        boolean isSkyDynamic = CandyTweak.OLD_DYNAMIC_SKY_COLOR.fromCache();

        return skyColor != SkyColor.DISABLED && (isSkyCustom || isSkyDynamic);
    }

    /**
     * Checks if the user has custom fog enabled. Having custom fog enabled will override dynamic fog.
     */
    private static boolean isDynamicFogConflict()
    {
        boolean isFogCustom = CandyTweak.USE_CUSTOM_OVERWORLD_FOG_COLOR.fromCache();
        boolean isFogDynamic = CandyTweak.OLD_DYNAMIC_FOG_COLOR.fromCache();

        return isFogCustom && isFogDynamic;
    }

    /**
     * Checks if the user has custom sky enabled. Having custom sky enabled will override dynamic sky.
     */
    private static boolean isDynamicSkyConflict()
    {
        boolean isSkyCustom = CandyTweak.CUSTOM_OVERWORLD_SKY.fromCache();
        boolean isSkyDynamic = CandyTweak.OLD_DYNAMIC_SKY_COLOR.fromCache();

        return isSkyCustom && isSkyDynamic;
    }

    /**
     * Checks if the user has not enabled custom overworld fog coloring.
     */
    private static boolean isOverworldFogColorConflict()
    {
        return !CandyTweak.USE_CUSTOM_OVERWORLD_FOG_COLOR.fromCache();
    }

    /**
     * Checks if the user has not enabled custom overworld fog density.
     */
    private static boolean isOverworldFogDensityConflict()
    {
        return !CandyTweak.USE_CUSTOM_OVERWORLD_FOG_DENSITY.fromCache();
    }

    /**
     * Checks if the user has not enabled custom Nether fog coloring.
     */
    private static boolean isNetherFogColorConflict()
    {
        return !CandyTweak.USE_CUSTOM_NETHER_FOG_COLOR.fromCache();
    }

    /**
     * Checks if the user has not enabled custom Nether fog density.
     */
    private static boolean isNetherFogDensityConflict()
    {
        return !CandyTweak.USE_CUSTOM_NETHER_FOG_DENSITY.fromCache();
    }

    /**
     * Checks if the user has the disabled hunger tweak off. If so, custom food health will not work.
     */
    private static boolean isCustomFoodHealthConflict()
    {
        return !GameplayTweak.DISABLE_HUNGER.fromCache();
    }

    /**
     * Checks if the user has the tweak old food stacking off. If so, custom food stacking will not work.
     */
    private static boolean isCustomFoodStackingConflict()
    {
        return !GameplayTweak.OLD_FOOD_STACKING.fromCache();
    }

    /**
     * Checks if the user has the old arm sway tweak enabled. If so, custom arm sway tweaks will not work.
     */
    private static boolean isArmSwayConflict()
    {
        return AnimationTweak.PREVENT_ARM_SWAY.fromCache();
    }

    /**
     * Checks if the user has sprinting disabled. If so, the stamina sprint system is useless.
     */
    private static boolean isStaminaSprintingConflict()
    {
        return GameplayTweak.DISABLE_SPRINT.fromCache();
    }
}

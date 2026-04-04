package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.resources.Identifier;

public record TextureLocation(Identifier location, int width, int height) {
    /* Locations */

    public static final Identifier DEV_MODE = ModAsset.texture("gui/nt_dev.png");
    public static final Identifier INVENTORY = ModAsset.texture("gui/inventory.png");
    public static final Identifier MOJANG_ALPHA = ModAsset.texture("gui/mojang_alpha.png");
    public static final Identifier MOJANG_BETA = ModAsset.texture("gui/mojang_beta.png");
    public static final Identifier MOJANG_RELEASE_ORANGE = ModAsset.texture("gui/mojang_release_orange.png");
    public static final Identifier MOJANG_RELEASE_BLACK = ModAsset.texture("gui/mojang_release_black.png");
    public static final Identifier PANORAMA_OVERLAY = ModAsset.texture("panorama/overlay.png");
    public static final Identifier DIRT_BACKGROUND = GameAsset.texture("block/dirt.png");
    public static final Identifier MENU_LIST_BACKGROUND = GameAsset.texture("gui/menu_list_background.png");
    public static final Identifier LEVEL_MENU_LIST_BACKGROUND = GameAsset.texture("gui/inworld_menu_list_background.png");

    public static final TextureLocation NT_LOGO_64 = new TextureLocation(ModAsset.icon("nt_logo_64.png"), 64);
    public static final TextureLocation NT_SUPPORTER_64 = new TextureLocation(ModAsset.icon("nt_supporter_64.png"), 64);
    public static final TextureLocation SOUND_16 = new TextureLocation(ModAsset.icon("sound_16.png"), 16);
    public static final TextureLocation CANDY_16 = new TextureLocation(ModAsset.icon("candy_16.png"), 16);
    public static final TextureLocation ANIMATION_16 = new TextureLocation(ModAsset.icon("animation_16.png"), 16);
    public static final TextureLocation GAMEPLAY_16 = new TextureLocation(ModAsset.icon("gameplay_16.png"), 16);
    public static final TextureLocation NOSTALGIC_TWEAKS = new TextureLocation("nostalgic_tweaks.png", 1920, 182);

    /* Missing Textures */

    public static final String MISSING_BETA = String.format("assets/%s/textures/missing/beta.png", NostalgicTweaks.MOD_ID);
    public static final String MISSING_1_5 = String.format("assets/%s/textures/missing/1_5.png", NostalgicTweaks.MOD_ID);
    public static final String MISSING_1_6_1_12 = String.format("assets/%s/textures/missing/1_6-1_12.png", NostalgicTweaks.MOD_ID);

    /* Fields */

    /* Constructor */

    /**
     * Create a new image texture location from a resource location.
     *
     * @param location The {@link Identifier} instance.
     * @param width    The image's width.
     * @param height   The image's height.
     */
    public TextureLocation {
    }

    /**
     * Create a new image texture location.
     *
     * @param path   A path in the mod's textures directory.
     * @param width  The image's width.
     * @param height The image's height.
     */
    public TextureLocation(String path, int width, int height) {
        this(LocateResource.mod("textures/" + path), width, height);
    }

    /**
     * Create a new image texture location.
     *
     * @param Identifier The {@link Identifier} instance.
     * @param size       The square size of the texture.
     */
    public TextureLocation(Identifier Identifier, int size) {
        this(Identifier, size, size);
    }

    /**
     * Create a new square texture location.
     *
     * @param path A path in the mod's textures directory.
     * @param size The square size of the texture.
     */
    public TextureLocation(String path, int size) {
        this(path, size, size);
    }

    /* Methods */

    public float getAverageSize() {
        return (this.width + this.height) / 2.0F;
    }
}

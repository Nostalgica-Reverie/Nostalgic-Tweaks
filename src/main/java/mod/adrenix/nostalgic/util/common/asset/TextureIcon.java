package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * This class is server-safe and must remain server-safe since config definitions use icons. The client will render
 * icons via an icon widget.
 */
public class TextureIcon {
    /* Static */

    public static final TextureIcon EMPTY = new TextureIcon(0, 0);

    /* Fields */

    private final int width;
    private final int height;
    private float maxBrightness;

    @Nullable
    private Identifier spriteLocation;
    @Nullable
    private TextureLocation textureLocation;
    @Nullable
    private Block block;
    @Nullable
    private Item item;

    /* Constructors */

    /**
     * Create a new texture icon instance.
     *
     * @param width  The width of the icon.
     * @param height The height of the icon.
     */
    private TextureIcon(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxBrightness = 1.02F;
    }

    /**
     * Create a new texture icon instance using an item.
     *
     * @param item The item to render an icon from.
     */
    private TextureIcon(@Nullable Item item) {
        this(16, 16);

        this.item = item;
    }

    /**
     * Create a new texture icon instance using a sprite.
     *
     * @param spriteLocation The sprite location instance.
     * @param width          The width of the sprite texture.
     * @param height         The height of the sprite texture.
     */
    private TextureIcon(@Nullable Identifier spriteLocation, int width, int height) {
        this(width, height);

        this.spriteLocation = spriteLocation;
    }

    /**
     * Create a new texture icon instance using a texture location.
     *
     * @param location A texture location instance.
     */
    private TextureIcon(TextureLocation location) {
        this(location.width(), location.height());

        this.textureLocation = location;
    }

    /* Getters */

    /**
     * Create a new icon instance from an item.
     *
     * @param item An item instance.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromItem(Item item) {
        TextureIcon icon = new TextureIcon(item);
        icon.item = item;

        return icon;
    }

    /**
     * Create a new icon instance from a block.
     *
     * @param block A block instance.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromBlock(Block block) {
        TextureIcon icon = new TextureIcon(block.asItem());
        icon.block = block;

        return icon;
    }

    /**
     * Create a new icon instance from a texture image.
     *
     * @param location A texture location instance.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromTexture(TextureLocation location) {
        return new TextureIcon(location);
    }

    /**
     * Create a new icon instance from a sprite location.
     *
     * @param spriteLocation A sprite location instance.
     * @param width          The width of the sprite texture.
     * @param height         The height of the sprite texture.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(Identifier spriteLocation, int width, int height) {
        return new TextureIcon(spriteLocation, width, height);
    }

    /**
     * Create a new icon instance from a sprite path.
     *
     * @param path   The sprite path in {@code assets/minecraft/textures/gui/sprites/mod_id/icon/}.
     * @param width  The width of the sprite texture.
     * @param height The height of the sprite texture.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(String path, int width, int height) {
        return fromSprite(ModSprite.icon(path), width, height);
    }

    /**
     * Create a new icon instance from a sprite location.
     *
     * @param spriteLocation A sprite location instance.
     * @param width          The width of the sprite texture.
     * @param height         The height of the sprite texture.
     * @param maxBrightness  The maximum amount of brightness that can be applied.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(Identifier spriteLocation, int width, int height, float maxBrightness) {
        return fromSprite(spriteLocation, width, height).maxBrightness(maxBrightness);
    }

    /**
     * Create a new icon instance from a sprite path.
     *
     * @param path          The sprite path in {@code assets/minecraft/textures/gui/sprites/mod_id/icon/}.
     * @param width         The width of the sprite texture.
     * @param height        The height of the sprite texture.
     * @param maxBrightness The maximum amount of brightness that can be applied.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(String path, int width, int height, float maxBrightness) {
        return fromSprite(ModSprite.icon(path), width, height, maxBrightness);
    }

    /* Methods */

    /**
     * Create a new icon instance from a sprite location.
     *
     * @param spriteLocation A sprite location instance.
     * @param squareSize     The square size of the sprite texture.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(Identifier spriteLocation, int squareSize) {
        return new TextureIcon(spriteLocation, squareSize, squareSize);
    }

    /**
     * Create a new icon instance from a sprite path.
     *
     * @param path       The sprite path in {@code assets/minecraft/textures/gui/sprites/mod_id/icon/}.
     * @param squareSize The square size of the sprite texture.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(String path, int squareSize) {
        return fromSprite(ModSprite.icon(path), squareSize);
    }

    /**
     * Create a new icon instance from a sprite location.
     *
     * @param spriteLocation A sprite location instance.
     * @param squareSize     The square size of the sprite texture.
     * @param maxBrightness  The maximum amount of brightness that can be applied.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(Identifier spriteLocation, int squareSize, float maxBrightness) {
        return fromSprite(spriteLocation, squareSize).maxBrightness(maxBrightness);
    }

    /* Builders */

    /**
     * Create a new icon instance from a sprite path.
     *
     * @param path          The sprite path in {@code assets/minecraft/textures/gui/sprites/mod_id/icon/}.
     * @param squareSize    The square size of the sprite texture.
     * @param maxBrightness The maximum amount of brightness that can be applied.
     * @return A new {@link TextureIcon} instance.
     */
    @PublicAPI
    public static TextureIcon fromSprite(String path, int squareSize, float maxBrightness) {
        return fromSprite(ModSprite.icon(path), squareSize, maxBrightness);
    }

    /**
     * @return The width of this icon on the texture sheet.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return The height of this icon on the texture sheet.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @return The maximum amount of brightness that can be applied to an icon.
     */
    public float getMaxBrightenAmount() {
        return this.maxBrightness;
    }

    /**
     * @return The {@link Identifier} of the sprite icon, if it exists.
     */
    public Optional<Identifier> getSpriteLocation() {
        return Optional.ofNullable(this.spriteLocation);
    }

    /**
     * @return The {@link TextureLocation} of this icon, if it exists.
     */
    public Optional<TextureLocation> getTextureLocation() {
        return Optional.ofNullable(this.textureLocation);
    }

    /**
     * @return The block instance associated with this icon if it is present.
     */
    public Optional<Block> getBlock() {
        return Optional.ofNullable(this.block);
    }

    /**
     * @return The item instance associated with this icon if it is present.
     */
    public Optional<Item> getItem() {
        return Optional.ofNullable(this.item);
    }

    /**
     * Checks if this instance uses {@link TextureIcon#EMPTY}.
     *
     * @return Whether this icon is {@code EMPTY}.
     */
    public boolean isEmpty() {
        return this.equals(TextureIcon.EMPTY);
    }

    /**
     * Functional shortcut to check if this instance is not using {@link TextureIcon#EMPTY}.
     *
     * @return Whether this icon is not {@code EMPTY}.
     */
    public boolean isPresent() {
        return !this.isEmpty();
    }

    /**
     * @param amount The amount of brightness to use when the icon is brightened.
     * @return The invoking instance so that functional chaining can be used.
     */
    public TextureIcon maxBrightness(float amount) {
        this.maxBrightness = amount;
        return this;
    }
}

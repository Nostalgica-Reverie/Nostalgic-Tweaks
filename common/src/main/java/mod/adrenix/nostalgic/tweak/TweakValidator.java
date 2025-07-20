package mod.adrenix.nostalgic.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.ClientConfig;
import mod.adrenix.nostalgic.config.ServerConfig;
import mod.adrenix.nostalgic.config.factory.ConfigBuilder;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.config.factory.LoaderException;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.tweak.listing.ListingMap;
import mod.adrenix.nostalgic.tweak.listing.ListingSet;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.log.LogColor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TweakValidator
{
    /* Static */

    private static final String THROW_REASON = "Some tweaks failed validation. This is a developer error.";

    /**
     * Scan the tweak values that are currently loaded in the {@link TweakPool}.
     *
     * @param config The {@link ConfigMeta} instance to scan - either {@code client} or {@code server}.
     */
    public static <T extends ConfigMeta> void check(Class<T> config)
    {
        try
        {
            new TweakValidator(false).scan(config);
        }
        catch (LoaderException exception)
        {
            ConfigBuilder.getHandler().reset();
        }
    }

    /**
     * Validate the given tweak and sync it if it was not valid.
     *
     * @param tweak The {@link Tweak} instance to inspect.
     * @param <T>   The class type of the given tweak.
     */
    public static <T> void inspect(Tweak<T> tweak)
    {
        TweakValidator validator = new TweakValidator(false);

        if (tweak.validate(validator))
            return;

        tweak.sync();
    }

    /* Fields */

    private final LinkedHashMap<String, String> exceptions = new LinkedHashMap<>();
    private final boolean internal;

    /* Constructors */

    /**
     * Create a new {@link TweakValidator} instance that will scan the tweaks currently loaded into memory. Validation
     * can be executed at any time.
     *
     * @param internal Whether the internal default config data will be scanned.
     */
    public TweakValidator(boolean internal)
    {
        this.internal = internal;
    }

    /* Methods */

    /**
     * Scan the tweaks that are currently loaded into the {@link TweakPool}.
     *
     * @param config The {@link ConfigMeta} instance to scan - either {@code client} or {@code server}.
     * @throws LoaderException When invalid data cannot be reset and requires the game to stop loading.
     */
    public <T extends ConfigMeta> void scan(Class<T> config) throws LoaderException
    {
        if (CollectionUtil.isNotEmpty(this.exceptions))
            throw new LoaderException(new Throwable(THROW_REASON));

        if (this.internal)
            NostalgicTweaks.LOGGER.debug("[Config Validator] Scanning default %s...", config.getSimpleName());
        else
            NostalgicTweaks.LOGGER.debug("[Config Validator] Scanning loaded %s...", config.getSimpleName());

        if (config.equals(ClientConfig.class))
            TweakPool.values().forEach(this::validate);
        else if (config.equals(ServerConfig.class))
            TweakPool.filter(Tweak::isMultiplayerLike).forEach(this::validate);

        if (CollectionUtil.isNotEmpty(this.exceptions))
        {
            for (Map.Entry<String, String> entry : this.exceptions.entrySet())
            {
                String key = LogColor.apply(LogColor.GOLD, entry.getKey());
                String value = LogColor.apply(LogColor.LIGHT_PURPLE, entry.getValue());

                NostalgicTweaks.LOGGER.error("[Config Validator] %s did not pass validation: %s", key, value);
            }

            throw new LoaderException(new Throwable(THROW_REASON));
        }
    }

    /**
     * Add a validation exception message.
     *
     * @param tweak   The tweak that is invalid.
     * @param message The reason the tweak is invalid.
     */
    public void exception(Tweak<?> tweak, String message)
    {
        this.exceptions.put(String.format("[Config Validator] %s [#%s]", tweak, this.exceptions.size()), message);
    }

    /**
     * Output a debug message to the game's console.
     *
     * @param message The debug message.
     * @param args    A varargs list of arguments.
     */
    public void debug(String message, Object... args)
    {
        NostalgicTweaks.LOGGER.debug("[Config Validator] " + message, args);
    }

    /**
     * Output a warning message to the game's console.
     *
     * @param message The warning message.
     * @param args    A varargs list of arguments.
     */
    public void warn(String message, Object... args)
    {
        NostalgicTweaks.LOGGER.warn("[Config Validator] " + message, args);
    }

    /**
     * @return Whether the validator's exception map is empty.
     */
    public boolean ok()
    {
        return this.exceptions.isEmpty();
    }

    /**
     * @return Whether the validator has generated exceptions.
     */
    public boolean erred()
    {
        return !this.ok();
    }

    /**
     * Check if the given tweak passes config validation.
     */
    private void validate(Tweak<?> tweak)
    {
        if (tweak.fromDisk() instanceof Collection && ClassUtil.isNotInstanceOf(tweak, ListingSet.class))
            this.exception(tweak, String.format("Collection tweaks must use %s.class", ListingSet.class.getSimpleName()));

        if (tweak.fromDisk() instanceof Map && ClassUtil.isNotInstanceOf(tweak, ListingMap.class))
            this.exception(tweak, String.format("Map tweaks must use an instance of %s.class", ListingMap.class.getSimpleName()));

        if (tweak.validate(this))
            return;

        tweak.sync();
    }
}

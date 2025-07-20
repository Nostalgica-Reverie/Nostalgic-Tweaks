package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.factory.TweakMap;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

abstract class ListingValidator
{
    /**
     * Validate a map-like listing. This will perform generic class type checks and ensure number-like values are within
     * map value bounds.
     *
     * @param listing   A {@link ListingMap} instance.
     * @param map       A {@link Map} instance.
     * @param validator A {@link TweakValidator} instance.
     * @param tweak     A {@link TweakListing} instance.
     * @param onChange  A {@link Consumer} that accepts a {@link Map.Entry} that was changed by the validator.
     * @param <V>       The class type stored in the {@link ListingMap}.
     * @param <L>       The class type of the {@link Listing}.
     * @return Whether validation was successful. If a value was changed, or an error happened, then {@code false} is
     * returned.
     */
    static <V, L extends ListingMap<V, L>> boolean map(ListingMap<V, L> listing, Map<String, V> map, TweakValidator validator, TweakListing<V, L> tweak, Consumer<Map.Entry<String, V>> onChange)
    {
        if (!Number.class.isAssignableFrom(listing.genericType()))
            return true;

        Optional<TweakMap<?, ?>> tweakMap = TweakMap.cast(tweak);

        if (tweakMap.isEmpty())
        {
            validator.exception(tweak, "Number maps must be assigned to tweaks that extend TweakMap.class");
            return false;
        }

        Optional<TweakSlider> mapSlider = tweakMap.get().getSlider();

        if (mapSlider.isEmpty())
        {
            validator.exception(tweak, "Number maps must have slider data defined in the tweak builder");
            return false;
        }

        TweakSlider slider = mapSlider.get();
        double min = slider.getMin().doubleValue();
        double max = slider.getMax().doubleValue();
        boolean hasNotChanged = true;

        for (Map.Entry<String, V> entry : map.entrySet())
        {
            if (entry.getValue() instanceof Number number)
            {
                double value = number.doubleValue();

                if (value < min || value > max)
                {
                    hasNotChanged = false;
                    String message = "%s key [%s] has an invalid number [%s] which was changed to: %s";
                    validator.warn(message, tweak, entry.getKey(), value, listing.getDefaultValue());
                    onChange.accept(entry);
                }
            }
        }

        if (validator.ok())
            validator.debug("%s was successfully validated with %s entries", tweak, map.size());

        return hasNotChanged;
    }

    /**
     * Validate a set-like listing. This will perform generic class type checks.
     *
     * @param listing   A {@link ListingSet} instance.
     * @param set       A {@link Set} instance.
     * @param validator A {@link TweakValidator} instance.
     * @param tweak     A {@link TweakListing} instance.
     * @param <V>       The class type stored in the {@link ListingSet}.
     * @param <L>       The class type of the {@link Listing}.
     * @return Whether validation was successful. If a value was changed, or an error happened, then {@code false} is
     * returned.
     */
    static <V, L extends Listing<V, L>> boolean set(Listing<V, L> listing, Set<V> set, TweakValidator validator, TweakListing<V, L> tweak)
    {
        if (Number.class.isAssignableFrom(listing.genericType()))
            validator.exception(tweak, "Number sets are currently not supported");

        if (validator.erred())
            return false;

        if (validator.ok())
            validator.debug("%s was successfully validated with %s entries", tweak, set.size());

        return true;
    }
}

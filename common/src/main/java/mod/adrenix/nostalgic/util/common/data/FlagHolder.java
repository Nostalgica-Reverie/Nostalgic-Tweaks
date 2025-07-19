package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class FlagHolder extends Holder<Boolean>
{
    /* Static */

    /**
     * @return A new {@link FlagHolder} instance where the default flag is {@code true}.
     * @see #off()
     * @see #FlagHolder(boolean)
     */
    public static FlagHolder on()
    {
        return new FlagHolder(true);
    }

    /**
     * @return A new {@link FlagHolder} instance where the default flag is {@code false}.
     * @see #on()
     * @see #FlagHolder(boolean)
     */
    public static FlagHolder off()
    {
        return new FlagHolder(false);
    }

    /**
     * Link flags together to form a radio group.
     *
     * @param flags A {@link Collection} of {@link FlagHolder} that will be linked together and will be considered part
     *              of a radio group. Different logic will occur in {@link #toggle()} when a flag is part of a radio
     *              group.
     */
    public static void radio(Collection<FlagHolder> flags)
    {
        for (FlagHolder holder : flags)
            holder.radios.addAll(flags);
    }

    /**
     * Link flags together to form a radio group.
     *
     * @param flags A varargs of {@link FlagHolder} that will be linked together and will be considered part of a radio
     *              group. Different logic will occur in {@link #toggle()} when a flag is part of a radio group.
     */
    public static void radio(FlagHolder... flags)
    {
        radio(Arrays.asList(flags));
    }

    /* Fields */

    private final LinkedHashSet<FlagHolder> radios;

    /* Constructor */

    /**
     * Reference {@code see also}.
     *
     * @param flag The default flag to create a {@link Holder} reference for.
     * @see FlagHolder#on()
     * @see FlagHolder#off()
     */
    public FlagHolder(boolean flag)
    {
        super(flag);

        this.radios = new LinkedHashSet<>();
    }

    /* Methods */

    /**
     * Toggle the flag stored in this holder.
     */
    public void toggle()
    {
        if (this.radios.isEmpty())
            this.set(!this.get());
        else
        {
            boolean state = this.get();

            this.radios.forEach(FlagHolder::disable);
            this.set(!state);
        }
    }

    /**
     * Enable this flag which will set the holder value to {@code true}.
     */
    public void enable()
    {
        this.set(true);
    }

    /**
     * Enable this flag, if and only if, the given supplier yields {@code true}.
     *
     * @param supplier A {@link Supplier} that provides a flag state.
     */
    public void enableIf(Supplier<Boolean> supplier)
    {
        if (supplier.get())
            this.set(true);
    }

    /**
     * Disable this flag which will set the holder value to {@code false}.
     */
    public void disable()
    {
        this.set(false);
    }

    /**
     * Disable this flag, if and only if, the given supplier yields {@code false}.
     *
     * @param supplier A {@link Supplier} that provides a flag state.
     */
    public void disableIf(Supplier<Boolean> supplier)
    {
        if (supplier.get())
            this.set(false);
    }

    /**
     * @return Whether the holder was disabled.
     */
    @PublicAPI
    public boolean ifDisabledThenEnable()
    {
        if (!this.value)
        {
            this.enable();

            return true;
        }

        return false;
    }

    /**
     * @return Whether the holder was enabled.
     */
    @PublicAPI
    public boolean ifEnabledThenDisable()
    {
        if (this.value)
        {
            this.disable();

            return true;
        }

        return false;
    }
}

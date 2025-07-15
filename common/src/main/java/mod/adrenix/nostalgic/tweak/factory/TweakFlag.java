package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.ClientboundTweakFlag;
import mod.adrenix.nostalgic.network.packet.tweak.ServerboundTweakFlag;
import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TweakFlag extends TweakValue<Boolean>
{
    /* Factories */

    /**
     * Build a new {@link TweakFlag} instance that is only available for the client. Reference the {@code see also} link
     * for more information about client tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakFlag.Builder client(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.CLIENT, container);
    }

    /**
     * Build a new {@link TweakFlag} instance that is available for both the client and server. Reference the
     * {@code see also} link for more information about server tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#SERVER
     */
    public static TweakFlag.Builder server(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.SERVER, container);
    }

    /**
     * Build a new {@link TweakFlag} instance that is dynamic. Reference the {@code see also} link for more information
     * about dynamic tweaks.
     *
     * @param defaultFlag The tweak's default value.
     * @param container   The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakFlag.Builder} instance.
     * @see TweakEnv#DYNAMIC
     */
    public static TweakFlag.Builder dynamic(boolean defaultFlag, Container container)
    {
        return new Builder(defaultFlag, TweakEnv.DYNAMIC, container);
    }

    /* Constructor */

    TweakFlag(TweakFlag.Builder builder)
    {
        super(builder);
    }

    /* Methods */

    /**
     * @return The {@link TweakFlag.Builder} for this {@link TweakFlag} instance.
     */
    private TweakFlag.Builder getBuilder()
    {
        return (Builder) this.builder;
    }

    @Override
    protected Boolean result()
    {
        return super.result() || this.getBuilder().orIf.stream().anyMatch(BooleanSupplier::getAsBoolean);
    }

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return new ClientboundTweakFlag(this);
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return new ServerboundTweakFlag(this);
    }

    /**
     * Toggles the flag based on the value stored on disk, {@code if true then set false} or
     * {@code if false then set true}. This will update the cache and then save change to disk.
     */
    public void toggle()
    {
        this.setCacheAndDiskThenSave(!this.fromDisk());
    }

    /* Builder */

    public static class Builder extends TweakValue.Builder<Boolean, Builder>
    {
        /* Fields */

        Set<BooleanSupplier> orIf = Set.of(BooleanSupplier.NEVER);

        /* Constructor */

        Builder(Boolean defaultValue, TweakEnv env, Container container)
        {
            super(defaultValue, env, container);
        }

        /* Methods */

        @Override
        Builder self()
        {
            return this;
        }

        /**
         * Any tweak flag that does not set its disabled value will automatically be set to {@code false} when the mod
         * or tweak is put into a disabled state. {@inheritDoc}
         *
         * @param value The boolean to use when the tweak is set into a "disabled" state.
         * @see TweakValue#getDisabled()
         */
        @Override
        public Builder whenDisabled(Boolean value)
        {
            return super.whenDisabled(value);
        }

        /**
         * A supplier that indicates if this flag should be considered enabled. This is useful if an additional "or"
         * check should be made in the mod config when it gets the value of a flag tweak. If both the tweak and the
         * given supplier yield {@code false}, then the disabled tweak value will be used.
         *
         * @param conditions A varargs of {@link BooleanSupplier} that provides whether a tweak flag should be
         *                   considered enabled.
         */
        public Builder orIf(BooleanSupplier... conditions)
        {
            this.orIf = Set.of(conditions);
            return this.self();
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakFlag} instance.
         */
        public TweakFlag build()
        {
            return new TweakFlag(this);
        }
    }
}

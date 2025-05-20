package mod.adrenix.nostalgic.tweak.factory;

import mod.adrenix.nostalgic.network.packet.tweak.TweakPacket;
import mod.adrenix.nostalgic.tweak.TweakEnv;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.tweak.gui.ControllerId;
import org.jetbrains.annotations.Nullable;

/**
 * Use this form of a tweak if you need to provide a custom controller to the tweak row list. Only use this option if
 * making another tweak data type isn't appropriate.
 * <p>
 * A good example of when to use this is the custom falling logo blocks editor. When that button is used in the tweak
 * row list, a custom editor screen is opened to allow for better editing control of the falling blocks.
 * <p>
 * This can only be used by {@link TweakEnv#CLIENT}, since these tweaks are only intended to provide custom controller
 * buttons for the tweak row list.
 */
public class TweakCustom extends TweakValue<Object>
{
    /* Factories */

    /**
     * Build a new {@link TweakCustom} instance that is only available for the client. Use this form of a tweak if you
     * need to provide a custom controller button to the tweak row list.
     *
     * @param type      The {@link ControllerId} instance this tweak will use.
     * @param container The tweak's {@link Container}, either a {@code category} or {@code group}.
     * @return A new {@link TweakCustom.Builder} instance.
     * @see TweakEnv#CLIENT
     */
    public static TweakCustom.Builder client(ControllerId type, Container container)
    {
        return new Builder(type, TweakEnv.CLIENT, container);
    }

    /* Fields */

    private final TweakCustom.Builder builder;

    /* Constructor */

    TweakCustom(TweakCustom.Builder builder)
    {
        super(builder);

        this.builder = builder;
    }

    /* Methods */

    /**
     * @return The {@link ControllerId} instance used by this {@link TweakCustom}.
     */
    public ControllerId getController()
    {
        return this.builder.type;
    }

    @Override
    public @Nullable TweakPacket getClientboundPacket()
    {
        return null;
    }

    @Override
    public @Nullable TweakPacket getServerboundPacket()
    {
        return null;
    }

    /* Builder */

    public static class Builder extends TweakValue.Builder<Object, Builder>
    {
        /* Fields */

        final ControllerId type;

        /* Constructor */

        Builder(ControllerId type, TweakEnv env, Container container)
        {
            super("null", env, container);

            this.type = type;
        }

        /* Methods */

        @Override
        Builder self()
        {
            return this;
        }

        /**
         * Finalize the building process.
         *
         * @return A new {@link TweakCustom} instance.
         */
        public TweakCustom build()
        {
            return new TweakCustom(this);
        }
    }
}

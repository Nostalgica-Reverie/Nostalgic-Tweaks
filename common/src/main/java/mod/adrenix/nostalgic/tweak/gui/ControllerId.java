package mod.adrenix.nostalgic.tweak.gui;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

import java.util.function.Supplier;

/**
 * Definitions of the custom controllers used by {@link mod.adrenix.nostalgic.tweak.factory.TweakCustom} tweaks.
 */
public enum ControllerId
{
    FALLING_LOGO(Lang.Button.OPEN_EDITOR, () -> ClientController::openFallingLogoEditor);

    /* Fields */

    private final Translation title;
    private final Supplier<Runnable> onPress;

    /* Constructor */

    ControllerId(Translation title, Supplier<Runnable> onPress)
    {
        this.title = title;
        this.onPress = onPress;
    }

    /* Methods */

    public Translation getTitle()
    {
        return this.title;
    }

    public Runnable getOnPress()
    {
        return this.onPress.get();
    }
}

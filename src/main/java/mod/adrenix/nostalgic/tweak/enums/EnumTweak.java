package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * This interface defines the requirements for a tweak that uses an enumeration class type. The following requirements
 * are used by the configuration menu user interface.
 */
public interface EnumTweak {
    /**
     * @return A {@link Translation} instance that points to a title name for the enumeration value.
     */
    Translation getTitle();
}

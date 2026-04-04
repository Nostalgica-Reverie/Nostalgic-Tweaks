package mod.adrenix.nostalgic.util.common;

public interface MixinPriority {
    /**
     * Forces the mixin to be applied first. Any mod using a lower value will take precedence.
     */
    int APPLY_FIRST = 999;

    /**
     * Forces the mixin to be applied last. Any mod using a higher value will load after.
     */
    int APPLY_LAST = 1001;
}

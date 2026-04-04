package mod.adrenix.nostalgic.client.gui.widget.dynamic;

/**
 * A {@link DynamicFunction} can have its instructions applied before layout updates are performed or after. The
 * enumeration {@link #HIGH} will apply custom dynamic functions after any layout functions are applied. An enumeration
 * of {@link #LOW} will reverse this logic.
 */
public enum DynamicPriority {
    HIGH,
    LOW
}

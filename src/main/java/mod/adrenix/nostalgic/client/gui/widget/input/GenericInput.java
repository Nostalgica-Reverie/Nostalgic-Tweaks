package mod.adrenix.nostalgic.client.gui.widget.input;

public class GenericInput extends AbstractInput<GenericInputBuilder, GenericInput> {
    /* Builder */

    protected GenericInput(GenericInputBuilder builder) {
        super(builder);
    }

    /* Constructor */

    /**
     * Begin the process of building a new {@link GenericInput} widget.
     *
     * @return A new {@link GenericInputBuilder} instance.
     */
    public static GenericInputBuilder create() {
        return new GenericInputBuilder();
    }
}

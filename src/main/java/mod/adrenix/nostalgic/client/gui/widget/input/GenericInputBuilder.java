package mod.adrenix.nostalgic.client.gui.widget.input;

public class GenericInputBuilder extends AbstractInputMaker<GenericInputBuilder, GenericInput> {
    /* Constructor */

    protected GenericInputBuilder() {
        super();
    }

    /* Methods */

    @Override
    public GenericInputBuilder self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GenericInput construct() {
        return new GenericInput(this);
    }
}

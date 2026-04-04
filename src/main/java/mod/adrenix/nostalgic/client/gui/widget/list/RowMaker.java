package mod.adrenix.nostalgic.client.gui.widget.list;

public class RowMaker extends AbstractRowMaker<RowMaker, Row> {
    /* Constructor */

    protected RowMaker(RowList rowList) {
        super(rowList);
    }

    /* Methods */

    @Override
    public RowMaker self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Row construct() {
        return new Row(this);
    }
}

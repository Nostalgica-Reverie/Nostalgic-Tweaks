package mod.adrenix.nostalgic.client.gui.widget.list;

public class Row extends AbstractRow<RowMaker, Row> {
    /* Builder */

    /**
     * Create a new {@link Row} instance using a row maker.
     *
     * @param builder A {@link RowMaker} instance.
     */
    protected Row(RowMaker builder) {
        super(builder);
    }

    /* Constructor */

    /**
     * Create a new {@link Row} by using a builder.
     *
     * @param list The {@link RowList} this row is being subscribed to.
     * @return A {@link RowMaker} instance.
     */
    public static RowMaker create(RowList list) {
        return new RowMaker(list);
    }
}

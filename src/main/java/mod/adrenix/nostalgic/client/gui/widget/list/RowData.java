package mod.adrenix.nostalgic.client.gui.widget.list;

/**
 * Row data properties that can be attached to widgets.
 */
public enum RowData {
    /**
     * If a widget syncs with its row's height, then it must attach this data to the widget. The row resizer will skip
     * widgets that contain this data when it calculates the new height for the row.
     */
    WIDGET_SYNCED_WITH_HEIGHT
}

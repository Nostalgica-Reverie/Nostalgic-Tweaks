package mod.adrenix.nostalgic.client.gui.screen.vanilla.world.select;

import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRowMaker;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;

import java.util.function.Function;
import java.util.function.Supplier;

class WorldRowMaker extends AbstractRowMaker<WorldRowMaker, WorldRow> {
    /* Fields */

    final Function<LevelSummary, Supplier<String>> getLevelSize;
    final WorldSelectionList.WorldListEntry entry;
    final NullableHolder<WorldRow> selected;
    final LevelSummary summary;

    /* Constructor */

    WorldRowMaker(RowList rowList, WorldSelectionList.WorldListEntry entry, LevelSummary summary, NullableHolder<WorldRow> selected, Function<LevelSummary, Supplier<String>> getLevelSize) {
        super(rowList);

        this.entry = entry;
        this.summary = summary;
        this.selected = selected;
        this.getLevelSize = getLevelSize;
    }

    /* Methods */

    @Override
    public WorldRowMaker self() {
        return this;
    }

    @Override
    protected WorldRow construct() {
        return new WorldRow(this);
    }
}

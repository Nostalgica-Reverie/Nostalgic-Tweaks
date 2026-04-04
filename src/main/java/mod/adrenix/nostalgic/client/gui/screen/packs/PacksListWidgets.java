package mod.adrenix.nostalgic.client.gui.screen.packs;

import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public class PacksListWidgets implements WidgetManager {
    /* Fields */

    private final PacksListScreen listScreen;

    /* Constructor */

    PacksListWidgets(PacksListScreen packsListScreen) {
        this.listScreen = packsListScreen;
    }

    /* Methods */

    @Override
    public void init() {
        int padding = 3;

        Group group = Group.create(this.listScreen)
                .background(Color.BLACK.fromAlpha(0.75F))
                .outline(Color.WHITE, 1)
                .widthOfScreen(0.7F)
                .padding(4, 8)
                .centerInScreenX()
                .centerInScreenY()
                .build(this.listScreen::addWidget);

        TextWidget header = TextWidget.create(Lang.Packs.HEADER)
                .icon(Icons.ZIP_FILE)
                .scale(2.0F)
                .centerAligned()
                .width(group::getInsideWidth)
                .build(group::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
                .height(1)
                .below(header, padding)
                .width(group::getInsideWidth)
                .build(group::addWidget);

        TextWidget.create(Lang.Packs.MESSAGE)
                .centerAligned()
                .below(separator, padding)
                .width(group::getInsideWidth)
                .build(group::addWidget);

        ButtonWidget.create(Lang.Affirm.QUIT_CANCEL)
                .icon(Icons.GO_BACK)
                .posX(padding)
                .fromScreenEndY(padding)
                .useTextWidth()
                .onPress(this.listScreen::onClose)
                .build(this.listScreen::addWidget);
    }
}

//package mod.adrenix.nostalgic.client.gui.widget.keybinding;
//
//import mod.adrenix.nostalgic.client.ClientKeyMapping;
//import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButtonMaker;
//import mod.adrenix.nostalgic.tweak.factory.TweakBinding;
//import net.minecraft.client.KeyMapping;
//import net.minecraft.network.chat.Component;
//
//public class KeybindingBuilder extends AbstractButtonMaker<KeybindingBuilder, KeybindingWidget> {
//    /* Fields */
//
//    final TweakBinding tweak;
//    final KeyMapping mapping;
//
//    /* Constructor */
//
//    public KeybindingBuilder(TweakBinding tweak) {
//        super(Component.empty());
//
//        this.tweak = tweak;
//        this.mapping = ClientKeyMapping.getFromId(tweak.getKeybindingId());
//    }
//
//    /* Methods */
//
//    @Override
//    public KeybindingBuilder self() {
//        return this;
//    }
//
//    @Override
//    protected KeybindingWidget construct() {
//        return new KeybindingWidget(this, this::onPress);
//    }
//}

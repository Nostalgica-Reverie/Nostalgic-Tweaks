//package mod.adrenix.nostalgic.client.gui.widget.keybinding;
//
//import com.mojang.blaze3d.platform.InputConstants;
//import mod.adrenix.nostalgic.client.gui.widget.button.AbstractButton;
//import mod.adrenix.nostalgic.tweak.factory.TweakBinding;
//import mod.adrenix.nostalgic.util.client.KeyboardUtil;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.KeyMapping;
//import net.minecraft.client.Minecraft;
//import net.minecraft.network.chat.Component;
//
//import java.util.function.Consumer;
//
//public class KeybindingWidget extends AbstractButton<KeybindingBuilder, KeybindingWidget> {
//    /* Builder */
//
//    private final TweakBinding tweak;
//
//    /* Fields */
//    private final KeyMapping mapping;
//    protected KeybindingWidget(KeybindingBuilder builder, Consumer<KeybindingWidget> onPress) {
//        super(builder, onPress);
//
//        this.tweak = builder.tweak;
//        this.mapping = builder.mapping;
//
//        builder.title(this::setTitle);
//    }
//
//    /* Constructor */
//
//    /**
//     * Create a new {@link KeybindingWidget}.
//     *
//     * @param tweak A {@link TweakBinding} instance.
//     * @return A new {@link KeybindingBuilder} instance.
//     */
//    public static KeybindingBuilder create(TweakBinding tweak) {
//        return new KeybindingBuilder(tweak);
//    }
//
//    /* Methods */
//
//    /**
//     * Reset the key mapping associated with this controller.
//     */
//    public void reset() {
//        InputConstants.Key defaultKey = this.mapping.getDefaultKey();
//
//        Minecraft.getInstance().options.setKey(this.mapping, defaultKey);
//        this.tweak.setCacheAndDiskThenSave(defaultKey.getValue());
//        KeyMapping.resetMapping();
//
//        this.setFocused(false);
//    }
//
//    /**
//     * Change the key associated with this controller's key mapping.
//     *
//     * @param keyCode  The key code that was pressed.
//     * @param scanCode A key scan code.
//     */
//    protected void setKey(int keyCode, int scanCode) {
//        if (KeyboardUtil.isEsc(keyCode)) {
//            Minecraft.getInstance().options.setKey(this.mapping, InputConstants.UNKNOWN);
//            this.tweak.setCacheAndDiskThenSave(InputConstants.UNKNOWN.getValue());
//        } else {
//            InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
//
//            Minecraft.getInstance().options.setKey(this.mapping, key);
//            this.tweak.setCacheAndDiskThenSave(key.getValue());
//        }
//
//        KeyMapping.resetMapping();
//
//        this.setFocused(false);
//    }
//
//    /**
//     * Change the button title based on focus context.
//     *
//     * @return A {@link Component}.
//     */
//    protected Component setTitle() {
//        if (this.isFocused()) {
//            return Component.literal("> ")
//                    .append(this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW))
//                    .append(" <")
//                    .withStyle(ChatFormatting.YELLOW);
//        } else if (this.mapping.isUnbound())
//            return Lang.Binding.UNBOUND.withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC);
//        else if (KeyboardUtil.isMappingConflict(this.mapping))
//            return this.mapping.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.RED);
//
//        return this.mapping.getTranslatedKeyMessage();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (this.isFocused() && this.isActive() && modifiers == 0) {
//            this.setKey(keyCode, scanCode);
//            this.setFocused(false);
//
//            return true;
//        }
//
//        return false;
//    }
//}

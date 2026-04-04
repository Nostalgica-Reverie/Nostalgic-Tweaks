//package mod.adrenix.nostalgic.client.gui.toast;
//
//import com.mojang.math.Axis;
//import mod.adrenix.nostalgic.NostalgicTweaks;
//import mod.adrenix.nostalgic.client.gui.GearSpinner;
//import mod.adrenix.nostalgic.network.ModConnection;
//import mod.adrenix.nostalgic.tweak.config.ModTweak;
//import mod.adrenix.nostalgic.util.client.gui.DrawText;
//import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
//import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
//import mod.adrenix.nostalgic.util.common.ClassUtil;
//import mod.adrenix.nostalgic.util.common.asset.ModSprite;
//import mod.adrenix.nostalgic.util.common.color.Color;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
//import net.minecraft.ChatFormatting;
//import net.minecraft.util.Util;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import net.minecraft.client.gui.components.toasts.Toast;
//import net.minecraft.client.gui.components.toasts.ToastComponent;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.gui.screens.TitleScreen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.util.FormattedCharSequence;
//import net.minecraft.util.Mth;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class ModToast implements Toast {
//    /* Singleton References */
//
//    private final static ModToast WELCOME = new ModToast(ToastId.WELCOME);
//    private final static ModToast HANDSHAKE = new ModToast(ToastId.HANDSHAKE);
//    private final static ModToast LAN_CHANGE = new ModToast(ToastId.LAN_CHANGE);
//    private final static ModToast LAN_REJECTION = new ModToast(ToastId.LAN_REJECTION);
//    private final static ModToast SERVERBOUND = new ModToast(ToastId.SERVERBOUND_TWEAK);
//    private final static ModToast CLIENTBOUND = new ModToast(ToastId.CLIENTBOUND_TWEAK);
//    private final ToastId id;
//
//    /* State Tracking */
//    private SimpleTimer timer = null;
//
//    /* Fields */
//    private Component title;
//    private List<FormattedCharSequence> lines;
//    private boolean isVisible = false;
//    private int width;
//    /**
//     * Create a new toast instance.
//     *
//     * @param id A toast identifier enumeration value.
//     */
//    private ModToast(ToastId id) {
//        this.id = id;
//    }
//
//    /**
//     * Get a nostalgic toast singleton reference based on the given toast identifier.
//     *
//     * @param id A toast identifier enumeration value.
//     * @return A nostalgic toast singleton instance.
//     */
//    public static ModToast getInstance(ToastId id) {
//        return switch (id) {
//            case WELCOME -> WELCOME;
//            case HANDSHAKE -> HANDSHAKE;
//            case LAN_CHANGE -> LAN_CHANGE;
//            case LAN_REJECTION -> LAN_REJECTION;
//            case SERVERBOUND_TWEAK -> SERVERBOUND;
//            case CLIENTBOUND_TWEAK -> CLIENTBOUND;
//        };
//    }
//
//    /* Constructor */
//
//    /**
//     * Instructions that run at the end of each gui render pass. This will check if certain conditions are met so that
//     * toasts are properly opened and/or closed.
//     */
//    public static void update(Screen screen) {
//        if (WELCOME.isClosed() && screen instanceof TitleScreen && !ModTweak.OPENED_CONFIG_SCREEN.get())
//            WELCOME.open();
//
//        if (WELCOME.isOpened() && ClassUtil.isNotInstanceOf(screen, TitleScreen.class))
//            WELCOME.close();
//    }
//
//    /* Methods */
//
//    /**
//     * Sets the toast's title text and message body text.
//     */
//    private void setText() {
//        this.title = switch (this.id) {
//            case WELCOME -> Lang.Toast.WELCOME_TITLE.get();
//            case HANDSHAKE -> Lang.Toast.HANDSHAKE_TITLE.get();
//            case LAN_CHANGE -> Lang.Toast.LAN_CHANGE_TITLE.get();
//            case LAN_REJECTION -> Lang.Toast.LAN_REJECTED_TITLE.get();
//            case SERVERBOUND_TWEAK -> Lang.Toast.SERVERBOUND_TWEAK_TITLE.get();
//            case CLIENTBOUND_TWEAK -> Lang.Toast.CLIENTBOUND_TWEAK_TITLE.get();
//        };
//
//        Component message = getMessage(NostalgicTweaks.getConnection().orElseGet(ModConnection::disconnected));
//
//        this.lines = GuiUtil.font().split(message, 182);
//        this.width = 24 + this.lines.stream().mapToInt(GuiUtil.font()::width).max().orElse(182);
//
//        if (this.lines.size() == 1) {
//            int titleWidth = 42 + GuiUtil.font().width(this.title);
//            int messageWidth = 24 + GuiUtil.font().width(this.lines.getFirst());
//
//            this.width = Math.max(titleWidth, messageWidth);
//        }
//    }
//
//    /**
//     * Get a toast message using the current server connection.
//     *
//     * @param connection A {@link ModConnection} instance.
//     * @return A {@link Component} message.
//     */
//    private Component getMessage(ModConnection connection) {
//        String client = ChatFormatting.GOLD + NostalgicTweaks.getTinyVersion();
//        String server = ChatFormatting.GOLD + connection.getVersion();
//        String loader = ChatFormatting.LIGHT_PURPLE + connection.getLoader();
//
//        return switch (this.id) {
//            case WELCOME -> Lang.Toast.WELCOME_MESSAGE.get(client);
//            case HANDSHAKE -> Lang.Toast.HANDSHAKE_MESSAGE.get(server, loader);
//            case LAN_CHANGE -> Lang.Toast.LAN_CHANGE_MESSAGE.get();
//            case LAN_REJECTION -> Lang.Toast.LAN_REJECTED_MESSAGE.get();
//            case SERVERBOUND_TWEAK -> Lang.Toast.SERVERBOUND_TWEAK_MESSAGE.get();
//            case CLIENTBOUND_TWEAK -> Lang.Toast.CLIENTBOUND_TWEAK_MESSAGE.get();
//        };
//    }
//
//    /**
//     * Change the visibility of the toast.
//     *
//     * @param visible Whether the toast is visible.
//     */
//    private void setVisible(boolean visible) {
//        this.isVisible = visible;
//        this.setText();
//    }
//
//    /**
//     * Open this toast. This will add the toast to the game's toast system if it isn't there already.
//     */
//    public void open() {
//        this.setVisible(true);
//        Minecraft.getInstance().getToasts().addToast(this);
//    }
//
//    /**
//     * Close this toast. This will remove the toast from the game's toast system if it wasn't already closed.
//     */
//    public void close() {
//        this.setVisible(false);
//    }
//
//    /**
//     * @return Whether this toast is opened.
//     */
//    public boolean isOpened() {
//        return this.isVisible;
//    }
//
//    /**
//     * @return Whether this toast is closed.
//     */
//    public boolean isClosed() {
//        return !this.isOpened();
//    }
//
//    /**
//     * Set a timer for this toast instance. This will override any previous timer.
//     *
//     * @param time How long in milliseconds this toast will be open.
//     * @return The toast instance so that additional instructions can be chained.
//     */
//    public ModToast setTimer(long time) {
//        this.timer = SimpleTimer.create(time, TimeUnit.MILLISECONDS).waitFirst().build();
//
//        return this;
//    }
//
//    /**
//     * Used by the game's internal toast system so that it can check if this toast is already open or not.
//     *
//     * @return A toast identifier enumeration value.
//     */
//    @Override
//    public Object getToken() {
//        return this.id;
//    }
//
//    /**
//     * Used by the renderer and internal toast system.
//     *
//     * @return Gets the maximum width of this toast instance.
//     */
//    @Override
//    public int width() {
//        return this.width;
//    }
//
//    /**
//     * Used by the internal toast system.
//     *
//     * @return Gets the maximum height of this toast instance.
//     */
//    @Override
//    public int height() {
//        return 25 + (this.lines.size() * 12);
//    }
//
//    /**
//     * Render the toast.
//     *
//     * @param graphics             A {@link GuiGraphicsExtractor} reference.
//     * @param toast                A {@link ToastComponent} instance.
//     * @param timeSinceLastVisible The time in milliseconds.
//     * @return A visibility enumeration instance that indicates whether the toast is visible or not.
//     */
//    @Override
//    public Visibility extractRenderState(GuiGraphicsExtractor graphics, ToastComponent toast, long timeSinceLastVisible) {
//        String splash = "N.T";
//        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
//        scale = scale * 4.0F / (float) (GuiUtil.font().width(splash));
//
//        RenderUtil.blitSprite(ModSprite.OVERLAY, graphics, 0, 0, this.width(), this.height());
//
//        GearSpinner.getInstance().render(graphics, 0.0215F, 6, 3);
//
//        RenderUtil.beginBatching();
//        graphics.fill(8, 15, this.width() - 8, this.height() - 8, 0xAF000000);
//
//        graphics.pose().pushPose();
//        graphics.pose().translate(12.0F, 12.0F, 0.0F);
//        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
//        graphics.pose().scale(scale, scale, scale);
//
//        DrawText.begin(graphics, splash).pos(1, -6).color(Color.YELLOW).draw();
//
//        graphics.pose().popPose();
//
//        DrawText.begin(graphics, this.title).pos(21, 4).color(Color.YELLOW).draw();
//
//        for (int i = 0; i < this.lines.size(); i++)
//            DrawText.begin(graphics, this.lines.get(i)).pos(12, 18 + i * 12).draw();
//
//        RenderUtil.endBatching();
//
//        if (this.timer != null && this.timer.hasElapsed())
//            this.close();
//
//        return this.isVisible ? Visibility.SHOW : Visibility.HIDE;
//    }
//}
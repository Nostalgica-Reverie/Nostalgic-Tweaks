package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.ClientKeyMapping;
import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeSplash;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.text.FallingBlockText;
import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
import mod.adrenix.nostalgic.helper.candy.hud.HudHelper;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.helper.candy.light.LightTextureHelper;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaHelper;
import mod.adrenix.nostalgic.listener.client.GuiListener;
import mod.adrenix.nostalgic.listener.client.TooltipListener;
import mod.adrenix.nostalgic.network.packet.sync.ServerboundSyncTweak;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.client.animate.Animator;
import mod.adrenix.nostalgic.util.client.timer.ClientTimer;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.network.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.PackType;

abstract class ClientInitializer
{
    /**
     * The maximum number of tweak sync requests the client will send to the server before giving up.
     */
    private static final int MAX_SYNC_ATTEMPTS = 10;

    /**
     * Tracks how many times the client tried syncing its tweaks with a server running Nostalgic Tweaks. Usually,
     * timeout occurs when the client's and server's mod versions don't match but have the same protocol version.
     */
    private static final IntegerHolder SERVERBOUND_SYNC_ATTEMPTS = IntegerHolder.create(0);

    /**
     * Registers client events.
     */
    static void register()
    {
        ClientKeyMapping.register();
        ClientSound.register();
        GuiListener.register();
        TooltipListener.register();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, HomeSplash.getInstance());
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, FallingBlockText.getInstance());

        for (Panorama panorama : Panorama.values())
            ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, panorama);

        ClientTickEvent.CLIENT_PRE.register(ClientInitializer::onPreTick);
        ClientTickEvent.CLIENT_POST.register(ClientInitializer::onPostTick);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(ClientInitializer::onPlayerQuit);

        AfterConfigSave.addInstruction(HudHelper::runAfterSave);

        ChestHelper.init();
        LightingHelper.init();
        FallingBlockConfig.init();
    }

    /**
     * Removes the verification of the mod connection when the player leaves a world with the mod installed and performs
     * the other necessary tasks when the local player leaves the world.
     *
     * @param player The {@link LocalPlayer} instance.
     */
    private static void onPlayerQuit(LocalPlayer player)
    {
        SERVERBOUND_SYNC_ATTEMPTS.set(0);

        TweakPool.stream().forEach(Tweak::disconnect);

        NostalgicTweaks.setNetworkVerification(false);
        NostalgicTweaks.setConnection(null);

        LightingHelper.resetLightingCache();
        LightTextureHelper.resetLightingCache();

        OverworldFogRenderer.reset();
        WaterFogRenderer.reset();
        VoidFogRenderer.reset();
        StaminaHelper.reset();
    }

    /**
     * Instructions to perform at the start of every tick.
     *
     * @param minecraft The {@link Minecraft} singleton instance.
     */
    private static void onPreTick(Minecraft minecraft)
    {
        ClientTimer.getInstance().onTick();
        Animator.onTick();
        Panorama.onTick();

        LightingHelper.onTick();
    }

    /**
     * Instructions to perform at the end of every tick.
     *
     * @param minecraft The {@link Minecraft} singleton instance.
     */
    private static void onPostTick(Minecraft minecraft)
    {
        TweakPool.stream().forEach(Tweak::invalidate);

        if (NostalgicTweaks.isNetworkVerified() && SERVERBOUND_SYNC_ATTEMPTS.get() < MAX_SYNC_ATTEMPTS)
            ClientTimer.getInstance().runAfter(3000L, ClientInitializer::syncAllTweaks);
    }

    /**
     * Check and sync tweaks that are not currently in sync with a verified server.
     */
    private static void syncAllTweaks()
    {
        if (!NostalgicTweaks.isNetworkVerified())
            return;

        TweakPool.filter(Tweak::isNotConnected)
            .forEach(tweak -> PacketUtil.sendToServer(new ServerboundSyncTweak(tweak)));

        SERVERBOUND_SYNC_ATTEMPTS.getAndIncrement();
    }
}

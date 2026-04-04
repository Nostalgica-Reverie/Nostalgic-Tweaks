//package mod.adrenix.nostalgic.util.common.network;
//
//import dev.architectury.networking.NetworkManager;
//import mod.adrenix.nostalgic.NostalgicTweaks;
//import net.fabricmc.api.EnvType;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerPlayer;
//
//import java.util.ArrayList;
//
//public abstract class PacketUtil {
//    /**
//     * <h3>S2C: Server-to-Client</h3>
//     * <p>
//     * Send a packet to a connected server player.
//     *
//     * @param player The server player to send the packet to.
//     * @param packet The packet to send to the server player.
//     */
//    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
//        if (NetworkManager.canPlayerReceive(player, packet.type())) {
//            NetworkManager.sendToPlayer(player, packet);
//            NostalgicTweaks.LOGGER.debug("(S2C) Sent (%s) to (%s)", packet, player);
//        } else
//            NostalgicTweaks.LOGGER.debug("(S2C) Player (%s) cannot receive (%s)", player, packet);
//    }
//
//    /**
//     * <h3>S2C: Server-to-Client</h3>
//     * <p>
//     * This is the best method to use when sending a packet to multiple players. If a player list is unavailable use
//     * {@link #sendToAll(CustomPacketPayload)}.
//     *
//     * @param players A list of players to send the packet to.
//     * @param packet  The packet to send to the list of players.
//     */
//    public static void sendToAll(Iterable<ServerPlayer> players, CustomPacketPayload packet) {
//        ArrayList<ServerPlayer> receivers = new ArrayList<>();
//
//        players.forEach(player -> {
//            if (NetworkManager.canPlayerReceive(player, packet.type()))
//                receivers.add(player);
//        });
//
//        NetworkManager.sendToPlayers(receivers, packet);
//        NostalgicTweaks.LOGGER.debug("(S2C) Sent (%s) to all players", packet);
//    }
//
//    /**
//     * <h3>S2C: Server-to-Client</h3>
//     * <p><br>
//     * This method can only be used in a dedicated server environment.
//     * <p><br>
//     * The alternative {@link #sendToAll(Iterable, CustomPacketPayload)} should be used unless there is no access to a
//     * player list, which should only happen in a dedicated server environment anyway.
//     *
//     * @param packet The packet to send to all players.
//     * @throws NullPointerException Will be thrown if there is no dedicated server instance.
//     */
//    public static void sendToAll(CustomPacketPayload packet) throws NullPointerException {
//        MinecraftServer server = NostalgicTweaks.getServer();
//
//        if (server == null)
//            throw new NullPointerException(String.format("Tried to send (%s) but there was no server instance available", packet.getClass()));
//
//        sendToAll(server.getPlayerList().getPlayers(), packet);
//    }
//
//    /**
//     * <h3>C2S: Client-to-Server</h3>
//     * <p>
//     * Send a packet to the server from the client.
//     *
//     * @param packet The packet to send to the server.
//     */
//    public static void sendToServer(CustomPacketPayload packet) {
//        if (NostalgicTweaks.isNetworkVerified()) {
//            if (NetworkManager.canServerReceive(packet.type())) {
//                NetworkManager.sendToServer(packet);
//                NostalgicTweaks.LOGGER.debug("(C2S) Sent (%s) to server", packet);
//            } else
//                NostalgicTweaks.LOGGER.debug("(C2S) Server cannot receive (%s)", packet);
//        } else
//            NostalgicTweaks.LOGGER.info("Mod network is unverified: Not sending (%s)", packet.getClass());
//    }
//
//    /**
//     * Check if the server player has operator permissions.
//     *
//     * @param player The server player to check permissions on.
//     * @return Whether the server player is an operator (permissionLevel >= 2).
//     */
//    public static boolean isPlayerOp(ServerPlayer player) {
//        return player.hasPermissions(2);
//    }
//
//    /**
//     * Informs the console that a packet was processed on the wrong logical side.
//     *
//     * @param side   The side to the packet was accidentally sent to.
//     * @param packet A packet class.
//     */
//    public static void warn(EnvType side, Class<?> packet) {
//        String receiver = side.equals(EnvType.CLIENT) ? "Client" : "Server";
//        String bound = side.equals(EnvType.CLIENT) ? "server" : "client";
//        String warn = String.format("%s is processing (%s) a %s bound packet. This shouldn't happen!", receiver, packet, bound);
//
//        NostalgicTweaks.LOGGER.warn(warn);
//    }
//}

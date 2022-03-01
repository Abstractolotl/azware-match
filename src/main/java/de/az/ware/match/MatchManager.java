package de.az.ware.match;

import de.az.ware.common.packets.MasterAuthenticate;
import de.az.ware.common.packets.Ping;
import de.az.ware.connection.Connection;
import de.az.ware.connection.ConnectionProvider;
import de.az.ware.connection.packet.*;
import de.az.ware.match.lobby.MatchLobby;
import de.az.ware.match.lobby.MatchLobbyListener;
import de.az.ware.common.packets.MatchCreation;
import de.az.ware.common.packets.MatchLogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listens to Connections and authenticates them.
 * Creates and manages Matches and delegates Connection/Packets to the right Match.
 */
public class MatchManager implements PacketHandler, MatchLobbyListener, MatchListener {

    private final static String authToken = "df2dc4fb-571b-4bff-bb77-84edcbfc3172";

    private final Map<MatchLobby, List<Connection>> lobbyConnections;
    private final Map<Match, List<Connection>> matchConnections;
    private Map<Connection, MatchConnectionMapper> matchAdapters;

    private Connection master;

    private final MatchRegistry registry;

    public MatchManager(ConnectionProvider provider, MatchRegistry registry, PacketParser parser) {
        this.registry = registry;
        lobbyConnections = new HashMap<>();
        matchConnections = new HashMap<>();
        matchAdapters = new HashMap<>();

        provider.setConnectionListener(new ConnectionPacketListenerAdapter(parser, new DelegatedPacketListener<>(Connection.class, parser, this){
            @Override
            public void onDisconnected(Connection connection) {
                MatchManager.this.onDisconnected(connection);
            }
        }){
            @Override
            public void onMessage(Connection connection, String message) {
                super.onMessage(connection, message);
                System.out.println(message);
            }
        });
    }

    private static void send(Connection connection, Packet packet){
        connection.sendMessage(PacketParser.SerializePacket(packet));
    }

    public void on(Connection connection, Ping.Request packet){
        send(connection, new Ping.Response(packet.getMessage()));
    }

    public void on(Connection connection, MasterAuthenticate.Request req){
        if(master != null || !req.getAuthToken().equals(authToken)) {
            send(connection, new MasterAuthenticate.Response(MasterAuthenticate.Status.ERROR));
            throw new RuntimeException("Master is already Set!");
        }

        master = connection;
        send(connection, new MasterAuthenticate.Response(MasterAuthenticate.Status.OK));
    }

    public void on(Connection connection, MatchCreation.Request packet){
        if(connection != master) return;

        MatchLobby lobby = new MatchLobby(packet, this, registry);
        lobbyConnections.put(lobby, new ArrayList<>());
    }

    public void on(Connection connection, MatchLogin.Request packet){
        for(MatchLobby lobby : lobbyConnections.keySet()) {
            MatchPlayer player = lobby.tryLogin(packet);
            if(player != null) {
                lobbyConnections.get(lobby).add(connection);
                lobby.getConnectionMapper().registerConnection(connection, player);
                lobby.tryStartMatch();
                return;
            }
        };
    }

    public void on(Connection connection, Packet packet) {
        MatchConnectionMapper adapter = matchAdapters.get(connection);
        if(adapter != null) adapter.onPacket(connection, packet);
    }

    public void onDisconnected(Connection connection) {
        MatchConnectionMapper adapter = matchAdapters.get(connection);
        if(adapter != null) adapter.onDisonnect(connection);

        for(MatchLobby lobby : lobbyConnections.keySet()) {
            if(lobbyConnections.get(lobby).contains(connection)){
                onLobbyCanceled(lobby);
            }
        }
    }

    @Override
    public void onMatchStart(MatchLobby lobby, Match match) {
        match.setListener(this);

        List<Connection> connections = lobbyConnections.get(lobby);
        lobbyConnections.remove(lobby);
        matchConnections.put(match, connections);

        MatchConnectionMapper adapter = lobby.getConnectionMapper();
        connections.forEach(c -> matchAdapters.put(c, adapter));
    }


    @Override
    public void onLobbyCanceled(MatchLobby lobby) {
        lobbyConnections.remove(lobby);
    }

    @Override
    public void onMatchOver(Match match) {
        List<Connection> connections = matchConnections.get(match);
        connections.forEach(matchAdapters::remove);
        matchConnections.remove(match);
    }

}

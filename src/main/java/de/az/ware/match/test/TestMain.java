package de.az.ware.match.test;

import de.az.ware.common.model.MatchType;
import de.az.ware.connection.ConnectionServer;
import de.az.ware.connection.packet.Packet;
import de.az.ware.connection.packet.PacketParser;
import de.az.ware.connection.websocket.WebSocketServerAdapter;
import de.az.ware.match.MatchRegistry;
import de.az.ware.match.MatchManager;
import de.az.ware.match.ttt.TTTMatch;

public class TestMain {

    public static void main(String[] args) {
        ConnectionServer server = new WebSocketServerAdapter(12001);
        PacketParser parser = new PacketParser(){
            @Override
            public void registerPacketClass(Class<? extends Packet> c) {
                super.registerPacketClass(c);
                System.out.println("Registering: " + c.getName());
            }
        };
        MatchRegistry registry = new MatchRegistry(parser);

        registry.registerMatch(MatchType.TTT, TTTMatch.class, TTTMatch.class);
        MatchManager manager = new MatchManager(server, registry, parser);

        server.start();
    }

}

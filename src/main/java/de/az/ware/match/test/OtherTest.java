package de.az.ware.match.test;

import de.az.ware.common.model.MatchType;
import de.az.ware.common.packets.MatchCreation;
import de.az.ware.common.packets.Ping;
import de.az.ware.connection.Connection;
import de.az.ware.connection.ConnectionListener;
import de.az.ware.connection.packet.PacketParser;
import de.az.ware.connection.websocket.WebSocketClient;

import java.util.UUID;

public class OtherTest {

    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient("ws://192.168.1.162:12001");
        PacketParser parser = new PacketParser();
        client.setConnectionListener(new ConnectionListener() {
            @Override
            public void onMessage(Connection connection, String message) {
                System.out.println(message);
            }

            @Override
            public void onConnected(Connection connection) {
                System.out.println("Connected");
            }

            @Override
            public void onDisconnected(Connection connection) {
                System.out.println("Disconnected");
            }
        });
        client.connect();

        //client.sendMessage(PacketParser.SerializePacket(new MatchCreation.Request(MatchType.TTT, new String[]{"123", "456"}, UUID.randomUUID())));
        client.sendMessage(PacketParser.SerializePacket(new Ping.Request("Hello!")));

    }


}

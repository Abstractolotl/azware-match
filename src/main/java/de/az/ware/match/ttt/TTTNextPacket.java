package de.az.ware.match.ttt;

import de.az.ware.connection.packet.Packet;

public class TTTNextPacket implements Packet {

    private final boolean yourTurn;

    public TTTNextPacket(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

}

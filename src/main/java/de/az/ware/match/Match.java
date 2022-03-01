package de.az.ware.match;

import de.az.ware.connection.packet.DelegatedPacketListener;
import de.az.ware.connection.packet.Packet;
import de.az.ware.connection.packet.PacketHandler;
import de.az.ware.connection.packet.PacketParser;

import java.util.Arrays;


public abstract class Match {

    protected final MatchPlayer[] players;

    private final MatchConnectionMapper adapter;
    private MatchListener listener;

    public Match(MatchConnectionMapper adapter, MatchPlayer[] players){
        this.players = players;
        this.adapter = adapter;

        adapter.setListener(new DelegatedPacketListener(MatchPlayer.class, getPacketHandler()));
    }

    public void onPlayerDisconnect(MatchPlayer player){
        var p = Arrays.stream(players).filter(pl -> pl != player).findAny();
        onMatchOver(p.orElse(null));
    }

    protected void send(MatchPlayer player, Packet packet){
        adapter.send(player, PacketParser.SerializePacket(packet));
    }

    protected void sendAll(Packet packet){
        for(MatchPlayer player : players) send(player, packet);
    }

    protected MatchPlayer getPlayerByIndex(int index){
        if(index < 0 || index >= players.length) System.err.println("Bad Index");
        return players[index];
    }

    protected void onMatchOver(MatchPlayer winner){
        if(listener != null) listener.onMatchOver(this);
    }

    public PacketHandler getPacketHandler(){
        if(this instanceof PacketHandler) {
            return (PacketHandler) this;
        }
        return null;
    }

    public void setListener(MatchListener listener) {
        this.listener = listener;
    }

}

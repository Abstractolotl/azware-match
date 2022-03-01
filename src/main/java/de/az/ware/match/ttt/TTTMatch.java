package de.az.ware.match.ttt;

import de.az.ware.connection.packet.PacketHandler;
import de.az.ware.match.Match;
import de.az.ware.match.MatchConnectionMapper;
import de.az.ware.match.MatchPlayer;

public class TTTMatch extends Match implements PacketHandler {

    private TicTacToe tttGame;

    public TTTMatch(MatchConnectionMapper adapter, MatchPlayer[] players) {
        super(adapter, players);
        tttGame = new TicTacToe();
    }

    public void on(MatchPlayer player, TTTPacket packet){
        try{
            tttGame.setField(player.getIndex(), packet.getFieldX(), packet.getFieldY());
        } catch (TicTacToe.IllegalTurnException e){
            send(player, null /* new ErrorResponsePacket("You are bad at this game :c \n"+e.getMessage()) */);
            return;
        }

        int nextPlayer = tttGame.getNextPlayer();
        send(getPlayerByIndex(nextPlayer), packet);
        if(checkMatchOver()){
            int winnerIndex = tttGame.checkPlayerWin();
            onMatchOver(getPlayerByIndex(winnerIndex));
        } else {
            for(MatchPlayer p : players) send(p, new TTTNextPacket(p.getIndex() == nextPlayer));
        }
    }

    private boolean checkMatchOver(){
        int playerWon = tttGame.checkPlayerWin();
        return playerWon != -1 || tttGame.isMatchOver();
    }
}

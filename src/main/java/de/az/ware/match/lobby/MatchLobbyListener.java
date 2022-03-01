package de.az.ware.match.lobby;

import de.az.ware.match.Match;

public interface MatchLobbyListener {

    void onMatchStart(MatchLobby lobby, Match match);
    void onLobbyCanceled(MatchLobby lobby);

}

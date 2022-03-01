package de.az.ware.match;

import de.az.ware.common.model.MatchType;
import de.az.ware.connection.packet.Packet;
import de.az.ware.connection.packet.PacketDelegator;
import de.az.ware.connection.packet.PacketHandler;
import de.az.ware.connection.packet.PacketParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of existing Match types.
 * Can create a Match with a given Match Type.
 *
 * If a packet parser is provided:
 * Finds all necessary Packet Classes for a given Match Class and registers them to the provided PacketParser
 */
public class MatchRegistry {

    private final PacketParser parser;
    private final Map<MatchType, Constructor<Match>> matchClasses;

    public MatchRegistry(PacketParser parser) {
        this.parser = parser;
        matchClasses = new HashMap<>();
    }

    public MatchRegistry() {
        this(null);
    }

    public void registerMatch(MatchType matchType, Class<? extends Match> matchClass, Class<? extends PacketHandler> packetHandlerClass){
        matchClasses.put(matchType, getConstructor(matchClass));
        if(parser == null) return;

        Method[] packetHandlers = PacketDelegator.getHandlerMethods(packetHandlerClass, MatchPlayer.class);
        for(Method m : packetHandlers) {
            Class<?> packetClass = m.getParameterTypes()[1];
            parser.registerPacketClass((Class<? extends Packet>) packetClass);
        }
    }

    private Constructor<Match> getConstructor(Class<? extends Match> matchClass){
        for(Constructor<?> c : matchClass.getConstructors()){
            if(c.getParameterCount() != 2) continue;

            Class<?> adapterClass = c.getParameterTypes()[0];
            Class<?> playerArrayClass = c.getParameterTypes()[1];
            if(!MatchConnectionMapper.class.isAssignableFrom(adapterClass)) continue;
            if(!MatchPlayer[].class.isAssignableFrom(playerArrayClass)) continue;
            return (Constructor<Match>) c;
        }
        return null;
    }

    public Match createMatch(MatchType matchType, MatchConnectionMapper adapter, MatchPlayer[] players){
        Constructor<Match> constr = matchClasses.get(matchType);
        if(constr == null) {
            throw new IllegalArgumentException("MatchType not in MatchRegistry");
        }

        try {
            return constr.newInstance(adapter, players);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

}

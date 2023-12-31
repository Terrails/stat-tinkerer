package terrails.stattinkerer.feature.event;

import net.minecraft.server.level.ServerPlayer;

public interface PlayerStateEvents {

    @FunctionalInterface
    interface JoinServer {
        void onPlayerJoinServer(ServerPlayer player);
    }

    @FunctionalInterface
    interface Clone {
        void onPlayerClone(boolean wasDeath, ServerPlayer newPlayer, ServerPlayer oldPlayer);
    }

    @FunctionalInterface
    interface Respawn {
        void onPlayerRespawn(ServerPlayer player);
    }

}

package terrails.stattinkerer.feature.event;

import net.minecraft.server.level.ServerPlayer;

public interface PlayerStateEvents {

    @FunctionalInterface
    interface JoinServer {
        void onPlayerJoinServer(ServerPlayer player);
    }

    @FunctionalInterface
    interface Clone {
        void onPlayerClone(boolean isEnd, ServerPlayer newPlayer, ServerPlayer oldPlayer);
    }

}

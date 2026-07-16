package org.galemc.gale.util.collection.packed;

import net.minecraft.server.level.ServerPlayer;

/**
 * An efficient iterable {@link ServerPlayer} storage.
 */
public class IterablePackedServerPlayers {

    private PackedBooleanArray packedArray;

    private IterablePackedServerPlayers(PackedBooleanArray packedArray) {
        this.packedArray = packedArray;
    }

    public static IterablePackedServerPlayers createEmpty() {
        return new IterablePackedServerPlayers(PackedBooleanArray.createEmpty());
    }

    public static IterablePackedServerPlayers createEmpty(int minCapacity) {
        return new IterablePackedServerPlayers(PackedBooleanArray.createEmpty(minCapacity));
    }

    public static PackedBooleanArray createWithInitial(ServerPlayer player) {
        return new IterablePackedServerPlayers(PackedBooleanArray.createWithInitial(player.gale$packedWordIndex, player.gale$packedWordMask));
    }

}

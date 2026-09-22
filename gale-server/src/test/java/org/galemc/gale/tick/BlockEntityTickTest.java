package org.galemc.gale.tick;

import io.papermc.paper.configuration.WorldConfiguration;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Normal
class BlockEntityTickTest {
    private Level level;
    private TickRateManager tickRate;
    private List<TickingBlockEntity> active;
    private List<TickingBlockEntity> pending;
    private final List<Integer> order = new ArrayList<>();

    @BeforeEach
    void setup() throws ReflectiveOperationException {
        this.level = mock(Level.class);
        this.tickRate = mock(TickRateManager.class);
        this.active = new ArrayList<>();
        this.pending = new ArrayList<>();
        setField("blockEntityTickers", this.active);
        setField("pendingBlockEntityTickers", this.pending);
        when(this.level.tickRateManager()).thenReturn(this.tickRate);
        when(this.tickRate.runsNormally()).thenReturn(true);
        when(this.level.shouldTickBlocksAt(any(BlockPos.class))).thenReturn(true);
        WorldConfiguration config = mock(WorldConfiguration.class);
        config.unsupportedSettings = config.new UnsupportedSettings();
        config.unsupportedSettings.ticking = config.unsupportedSettings.new Ticking();
        when(this.level.paperConfig()).thenReturn(config);
        doCallRealMethod().when(this.level).tickBlockEntities();
        doCallRealMethod().when(this.level).addBlockEntityTicker(any());
    }

    @Test
    void keepsTickOrderWhenNoTickerIsRemoved() {
        for (int i = 0; i < 20; i++) {
            this.active.add(new Ticker(i));
        }
        List<TickingBlockEntity> before = List.copyOf(this.active);
        this.level.tickBlockEntities();
        assertEquals(before, this.active);
        assertEquals(java.util.stream.IntStream.range(0, 20).boxed().toList(), this.order);
        verify(this.level, times(2)).moonrise$midTickTasks();
    }

    @Test
    void removesByIdentityEvenWhenTickersCompareEqual() {
        Ticker removed = new Ticker(1);
        removed.removed = true;
        Ticker live = new Ticker(2);
        this.active.addAll(List.of(removed, live, removed));
        this.level.tickBlockEntities();
        assertEquals(1, this.active.size());
        assertSame(live, this.active.getFirst());
        assertEquals(List.of(2), this.order);
    }

    @Test
    void laterTickerRemovedDuringTickDoesNotRun() {
        Ticker first = new Ticker(1);
        Ticker second = new Ticker(2);
        first.action = () -> second.removed = true;
        this.active.addAll(List.of(first, second));
        this.level.tickBlockEntities();
        assertEquals(List.of(1), this.order);
        assertEquals(1, this.active.size());
    }

    @Test
    void earlierTickerRemovedDuringTickIsCleanedOnFollowingTick() {
        Ticker first = new Ticker(1);
        Ticker second = new Ticker(2);
        second.action = () -> first.removed = true;
        this.active.addAll(List.of(first, second));
        this.level.tickBlockEntities();
        assertEquals(2, this.active.size());
        this.level.tickBlockEntities();
        assertEquals(List.of(1, 2, 2), this.order);
        assertEquals(1, this.active.size());
        assertSame(second, this.active.getFirst());
    }

    @Test
    void newTickersAreDeferredAndPendingEntriesRetainOrder() {
        Ticker first = new Ticker(1);
        Ticker added = new Ticker(3);
        first.action = () -> {
            this.level.addBlockEntityTicker(added);
            first.action = () -> {};
        };
        this.active.add(first);
        this.pending.add(new Ticker(2));
        this.level.tickBlockEntities();
        assertEquals(List.of(1, 2), this.order);
        assertEquals(1, this.pending.size());
        this.level.tickBlockEntities();
        assertEquals(List.of(1, 2, 1, 2, 3), this.order);
        assertTrue(this.pending.isEmpty());
    }

    @Test
    void frozenTickStillRemovesDeadEntries() {
        when(this.tickRate.runsNormally()).thenReturn(false);
        Ticker removed = new Ticker(1);
        removed.removed = true;
        Ticker live = new Ticker(2);
        this.active.addAll(List.of(removed, live));
        this.level.tickBlockEntities();
        assertTrue(this.order.isEmpty());
        assertSame(live, this.active.getFirst());
        assertEquals(1, this.active.size());
        verify(this.level, never()).moonrise$midTickTasks();
    }

    @Test
    void doesNotTickOutsideTickingChunks() {
        when(this.level.shouldTickBlocksAt(any(BlockPos.class))).thenReturn(false);
        this.active.add(new Ticker(1));
        this.level.tickBlockEntities();
        assertTrue(this.order.isEmpty());
        assertEquals(1, this.active.size());
    }

    @Test
    void removesNullLeftInAnAlreadyVisitedSlot() {
        Ticker first = new Ticker(1);
        Ticker second = new Ticker(2);
        second.action = () -> this.active.set(0, null);
        this.active.addAll(List.of(first, second));
        this.level.tickBlockEntities();
        assertEquals(1, this.active.size());
        assertSame(second, this.active.getFirst());
    }

    @Test
    void directAddsAfterTickGoToActiveList() {
        this.level.tickBlockEntities();
        Ticker ticker = new Ticker(1);
        this.level.addBlockEntityTicker(ticker);
        assertSame(ticker, this.active.getFirst());
        assertTrue(this.pending.isEmpty());
    }

    @Test
    void observedRemovalIsNotReversedByLaterRevival() {
        Ticker removed = new Ticker(1);
        removed.removed = true;
        Ticker live = new Ticker(2);
        live.action = () -> removed.removed = false;
        this.active.addAll(List.of(removed, live));
        this.level.tickBlockEntities();
        assertEquals(1, this.active.size());
        assertSame(live, this.active.getFirst());
    }

    @Test
    void cleanupAlsoRemovesNullWhenADeadTickerWasObserved() {
        Ticker first = new Ticker(1);
        Ticker dead = new Ticker(2);
        dead.removed = true;
        Ticker last = new Ticker(3);
        last.action = () -> this.active.set(0, null);
        this.active.addAll(List.of(first, dead, last));
        this.level.tickBlockEntities();
        assertEquals(List.of(1, 3), this.order);
        assertEquals(1, this.active.size());
        assertSame(last, this.active.getFirst());
    }

    @Test
    void doesNotCacheChunkEligibilityAcrossTickerCallbacks() {
        Ticker first = new Ticker(1);
        first.action = () -> when(this.level.shouldTickBlocksAt(any(BlockPos.class))).thenReturn(false);
        this.active.addAll(List.of(first, new Ticker(2)));
        this.level.tickBlockEntities();
        assertEquals(List.of(1), this.order);
        assertEquals(2, this.active.size());
    }

    private void setField(String name, Object value) throws ReflectiveOperationException {
        Field field = Level.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(this.level, value);
    }

    private final class Ticker implements TickingBlockEntity {
        private final int id;
        private boolean removed;
        private Runnable action = () -> {};

        private Ticker(int id) {
            this.id = id;
        }

        @Override
        public void tick() {
            order.add(this.id);
            this.action.run();
        }

        @Override
        public boolean isRemoved() {
            return this.removed;
        }

        @Override
        public BlockPos getPos() {
            return BlockPos.ZERO;
        }

        @Override
        public String getType() {
            return "test";
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof BlockEntityTickTest.Ticker;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}

package org.galemc.gale.tick;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@Normal
class GoalSelectorTickTest {
    private final GoalSelector selector = new GoalSelector();
    private final List<String> events = new ArrayList<>();

    @ParameterizedTest
    @EnumSource(Goal.Flag.class)
    void stoppedGoalReleasesEveryControlFlag(Goal.Flag flag) {
        TestGoal first = new TestGoal("first", flag);
        TestGoal second = new TestGoal("second", flag);
        this.selector.addGoal(0, first);
        this.selector.addGoal(1, second);
        this.selector.tick();
        assertEquals(List.of("first start", "first tick"), this.events);
        this.events.clear();
        first.usable = false;
        this.selector.tick();
        assertEquals(List.of("first stop", "second start", "second tick"), this.events);
    }

    @Test
    void disabledFlagStopsGoalAndCanBeEnabledAgain() {
        TestGoal goal = new TestGoal("goal", Goal.Flag.MOVE);
        this.selector.addGoal(0, goal);
        this.selector.tick();
        this.events.clear();
        this.selector.disableControlFlag(Goal.Flag.MOVE);
        this.selector.tick();
        assertEquals(List.of("goal stop"), this.events);
        this.events.clear();
        this.selector.enableControlFlag(Goal.Flag.MOVE);
        this.selector.tick();
        assertEquals(List.of("goal start", "goal tick"), this.events);
    }

    @Test
    void higherPriorityGoalPreemptsOnlyWhenUsable() {
        TestGoal first = new TestGoal("first", Goal.Flag.MOVE);
        TestGoal second = new TestGoal("second", Goal.Flag.MOVE);
        second.usable = false;
        this.selector.addGoal(1, first);
        this.selector.addGoal(0, second);
        this.selector.tick();
        this.events.clear();
        second.usable = true;
        this.selector.tick();
        assertEquals(List.of("first stop", "second start", "second tick"), this.events);
    }

    @Test
    void removesMultipleLocksBeforeStartingReplacements() {
        TestGoal both = new TestGoal("both", Goal.Flag.MOVE, Goal.Flag.LOOK);
        TestGoal move = new TestGoal("move", Goal.Flag.MOVE);
        TestGoal look = new TestGoal("look", Goal.Flag.LOOK);
        this.selector.addGoal(0, both);
        this.selector.addGoal(1, move);
        this.selector.addGoal(1, look);
        this.selector.tick();
        this.events.clear();
        both.usable = false;
        this.selector.tick();
        assertEquals(List.of("both stop", "move start", "look start", "move tick", "look tick"), this.events);
    }

    @Test
    void removingRunningGoalReleasesItsLockOnNextTick() {
        TestGoal first = new TestGoal("first", Goal.Flag.TARGET);
        TestGoal second = new TestGoal("second", Goal.Flag.TARGET);
        this.selector.addGoal(0, first);
        this.selector.addGoal(1, second);
        this.selector.tick();
        this.events.clear();
        this.selector.removeGoal(first);
        this.selector.tick();
        assertEquals(List.of("first stop", "second start", "second tick"), this.events);
    }

    @Test
    void runningGoalsRetainUpdateEveryTickRules() {
        TestGoal slow = new TestGoal("slow", Goal.Flag.MOVE);
        TestGoal fast = new TestGoal("fast", Goal.Flag.LOOK);
        fast.everyTick = true;
        this.selector.addGoal(0, slow);
        this.selector.addGoal(0, fast);
        this.selector.tick();
        this.events.clear();
        this.selector.tickRunningGoals(false);
        assertEquals(List.of("fast tick"), this.events);
        assertTrue(this.selector.hasTasks());
    }

    private final class TestGoal extends Goal {
        private final String name;
        private boolean usable = true;
        private boolean everyTick;

        private TestGoal(String name, Goal.Flag... flags) {
            this.name = name;
            this.setFlags(flags.length == 0 ? EnumSet.noneOf(Goal.Flag.class) : EnumSet.copyOf(List.of(flags)));
        }

        @Override
        public boolean canUse() {
            return this.usable;
        }

        @Override
        public void start() {
            events.add(this.name + " start");
        }

        @Override
        public void stop() {
            events.add(this.name + " stop");
        }

        @Override
        public void tick() {
            events.add(this.name + " tick");
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return this.everyTick;
        }
    }
}

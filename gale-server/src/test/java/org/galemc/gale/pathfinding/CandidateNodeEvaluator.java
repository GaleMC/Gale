package org.galemc.gale.pathfinding;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;

public final class CandidateNodeEvaluator extends NodeEvaluator {
    private final Node[] endpoints;
    private final boolean reached;

    public CandidateNodeEvaluator(Node[] endpoints, boolean reached) {
        this.endpoints = endpoints;
        this.reached = reached;
    }

    @Override
    public void prepare(PathNavigationRegion level, Mob entity) {
    }

    @Override
    public Node getStart() {
        return new Node(0, 0, 0);
    }

    @Override
    public Target getTarget(double x, double y, double z) {
        Target target = new Target(this.reached ? 0 : 1_000_000, 0, 0);
        target.updateBest(-1, this.endpoints[(int) x]);
        return target;
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node current) {
        return 0;
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        return PathType.WALKABLE;
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return PathType.WALKABLE;
    }
}

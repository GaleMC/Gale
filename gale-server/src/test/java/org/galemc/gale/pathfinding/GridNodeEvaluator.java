package org.galemc.gale.pathfinding;

import java.util.Random;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;

public class GridNodeEvaluator extends NodeEvaluator {
    private final int size;
    private final boolean[] blocked;
    private final float[] costs;
    private int expansions;

    public GridNodeEvaluator(int size, long seed, boolean obstacles) {
        this.size = size;
        this.blocked = new boolean[size * size];
        this.costs = new float[size * size];
        Random random = new Random(seed);
        for (int i = 1; i < this.blocked.length; i++) {
            this.blocked[i] = obstacles && random.nextDouble() < 0.25;
            this.costs[i] = obstacles ? random.nextInt(4) : 0;
        }
    }

    @Override
    public void prepare(PathNavigationRegion level, Mob entity) {
        this.nodes.clear();
        this.expansions = 0;
    }

    @Override
    public Node getStart() {
        return this.getNode(0, 0, 0);
    }

    @Override
    public Target getTarget(double x, double y, double z) {
        return new Target((int) x, (int) y, (int) z);
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node current) {
        this.expansions++;
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                int x = current.x + dx;
                int z = current.z + dz;
                if (!this.isOpen(x, z)
                    || (dx != 0 && dz != 0 && (!this.isOpen(x, current.z) || !this.isOpen(current.x, z)))) {
                    continue;
                }
                Node node = this.getNode(x, 0, z);
                if (!node.closed) {
                    node.costMalus = this.costs[x + z * this.size];
                    neighbors[count++] = node;
                }
            }
        }
        return count;
    }

    private boolean isOpen(int x, int z) {
        return x >= 0 && z >= 0 && x < this.size && z < this.size && !this.blocked[x + z * this.size];
    }

    public int expansions() {
        return this.expansions;
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        return this.getPathType(context, x, y, z);
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        return this.isOpen(x, z) ? PathType.WALKABLE : PathType.BLOCKED;
    }
}

package com.caved_in.commons.game.world;

import com.caved_in.commons.location.Locations;
import com.caved_in.commons.yml.Path;
import com.caved_in.commons.yml.YamlConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionArena extends YamlConfig implements GameArena {
    @Path("id")
    private int id;

    @Path("name")
    private String name;

    @Path("enabled")
    private boolean enabled;

    @Path("upper-bound")
    private Location upperBound;

    @Path("lower-bound")
    private Location lowerBound;

    @Path("spawn-points")
    private List<Location> spawns = new ArrayList<>();

    @Path("breakable-blocks")
    private List<Integer> breakableBlockIds = new ArrayList<>();

    @Path("placeable-blocks")
    private List<Integer> placeableBlockIds = new ArrayList<>();

    public RegionArena(File file) {
        super(file);
    }

    public RegionArena(Location upperBound, Location lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public int id() {
        return id;
    }

    @Override
    public String getArenaName() {
        return name;
    }

    @Override
    public String getWorldName() {
        return upperBound.getWorld().getName();
    }

    @Override
    public World getWorld() {
        return upperBound.getWorld();
    }

    @Override
    public List<Location> getSpawnLocations() {
        return spawns;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isLobby() {
        return false;
    }

    @Override
    public boolean isBreakable(Block block) {
        return breakableBlockIds.contains(block.getTypeId());
    }

    @Override
    public boolean isPlaceable(Block block) {
        return placeableBlockIds.contains(block.getTypeId());
    }

    @Override
    public boolean isRegion() {
        return true;
    }

    public Location getUpperBound() {
        return upperBound;
    }

    public Location getLowerBound() {
        return lowerBound;
    }

    public boolean isInside(Location location) {
        return Locations.isInsideArea(location,upperBound,lowerBound,true);
    }
}
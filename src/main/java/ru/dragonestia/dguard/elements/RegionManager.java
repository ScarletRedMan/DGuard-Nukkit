package ru.dragonestia.dguard.elements;

import cn.nukkit.Player;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegionManager {

    private final String playerName;

    private final DGuard main;

    public RegionManager(Player player, DGuard main){
        this.playerName = player.getName().toLowerCase();
        this.main = main;
    }

    public RegionManager(String playerName, DGuard main){
        this.playerName = playerName.toLowerCase();
        this.main = main;
    }

    public Player getPlayer() {
        return main.getServer().getPlayer(playerName);
    }

    public List<Region> getRegions(){
        List<Region> list = new ArrayList<>();

        for(Region region: DGuard.regions.values()){
            if(playerName.equals(region.getOwner())){
                list.add(region);
            }
        }

        return list;
    }

    public int getCount(){
        int count = 0;
        for(Region region: DGuard.regions.values()){
            if(playerName.equals(region.getOwner())) ++count;
        }
        return count;
    }

    public void createRegion(String id, String level, Point point1, Point point2) throws RegionLimitCountException, RegionLimitSizeException, InvalidRegionIdException, RegionAlreadyExistException, RegionIsCharacterizedByOtherRegionsException, PointsInDifferentLevelsException {
        Player player = getPlayer();
        CustomMethods customMethods = main.getCustomMethods();

        if(!customMethods.regionCountChecker.check(player, getCount())) throw new RegionLimitCountException();

        if(!point1.level.equals(point2.level)) throw new PointsInDifferentLevelsException();

        Point min, max;
        min = Point.getMin(point1, point2);
        max = Point.getMax(point1, point2);

        min.level = max.level = point1.level;

        if(!customMethods.regionSizeChecker.check(player, (max.x - min.x) * (max.z - min.z))) throw new RegionLimitSizeException();

        if(Point.isPrivateArea(min, max)) throw new RegionIsCharacterizedByOtherRegionsException();

        id = id.trim().toLowerCase();

        Pattern pattern = Pattern.compile("^[aA-zZ\\d]+");
        if(!pattern.matcher(id).matches()) throw new InvalidRegionIdException();

        if(DGuard.regions.containsKey(id)) throw new RegionAlreadyExistException();

        Region region = new Region(id, min.x, max.x, min.z, max.z, main);
        region.owner = player.getName().toLowerCase();
        region.levelName = level.toLowerCase();

        DGuard.regions.put(id, region);
        region.save();
    }

}

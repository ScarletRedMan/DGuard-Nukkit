package ru.dragonestia.dguard.region;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import lombok.Getter;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.exceptions.RegionException;
import ru.dragonestia.dguard.util.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerRegionManager {

    private final DGuard main;
    @Getter private final Player player;

    public PlayerRegionManager(Player player, DGuard main){
        this.player = player;
        this.main = main;
    }

    public List<Region> getRegions(){
        List<Region> list = new ArrayList<>();

        String lowerName = player.getName().toLowerCase();
        for(HashMap<Integer, Region> levelRegions: DGuard.regions.values()){
            for(Region region: levelRegions.values()){
                if(lowerName.equals(region.getOwner())){
                    list.add(region);
                }
            }
        }

        return list;
    }

    public int getCount(){
        int count = 0;

        String lowerName = player.getName().toLowerCase();
        for(HashMap<Integer, Region> levelRegions: DGuard.regions.values()){
            for(Region region: levelRegions.values()){
                if(lowerName.equals(region.getOwner())){
                    count++;
                }
            }
        }

        return count;
    }

    public void createRegion(String name, Area area, Level level) throws RegionException {
        Player player = getPlayer();
        CustomMethods customMethods = main.getCustomMethods();

        if(!customMethods.regionCountChecker.check(player, getCount())) throw new RegionException("Вы сейчас владеете максимальным количеством регионов");
        if(!customMethods.regionSizeChecker.check(player, area.getSpace(main.getSettings().is_3d()))) throw new RegionException("Вы выделили слишком большую территорию для региона");
        if(area.isPrivateArea(level)) throw new RegionException("Регион пересекает чужие регионы");

        Region region = new Region(name, area, level.getName(), player.getName());
        region.init(main);
        region.save(true);

        main.getFirstPoints().remove(player.getId());
        main.getSecondPoints().remove(player.getId());
    }

}

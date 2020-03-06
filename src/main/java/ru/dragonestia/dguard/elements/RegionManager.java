package ru.dragonestia.dguard.elements;

import cn.nukkit.Player;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.exceptions.RegionNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class RegionManager {

    private Player player;

    public RegionManager(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Region> getRegions(){
        List<Region> list = new ArrayList<>();

        for(String id: DGuard.areas.getAll().keySet()){
            if(DGuard.areas.getString(id + ".owner").equals(player.getName().toLowerCase())){
                try{
                    list.add(new Region(id));
                } catch (RegionNotFoundException ignore){

                }
            }
        }

        return list;
    }

    public int getCount(){
        return getRegions().size();
    }

}

package ru.dragonestia.dguard.custom;

import lombok.Getter;
import ru.dragonestia.dguard.DGuard;

public class CustomMethods {

    @Getter private final DGuard main;

    public CanDoAllCondition canDoAllCondition;
    public RegionCountChecker regionCountChecker;
    public RegionSizeChecker regionSizeChecker;

    public CustomMethods(DGuard main){
        this.main = main;
    }

    public void init(){
        canDoAllCondition = player -> player.hasPermission("dguard.admin");
        regionCountChecker = (player, count) -> main.getSettings().getRegionMaxCount() > count || player.hasPermission("dguard.count") ||canDoAllCondition.check(player);
        regionSizeChecker = (player, size) -> main.getSettings().getRegionMaxSize() > size || player.hasPermission("dguard.size") || canDoAllCondition.check(player);
    }

}

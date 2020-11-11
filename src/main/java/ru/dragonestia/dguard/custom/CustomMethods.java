package ru.dragonestia.dguard.custom;

import cn.nukkit.Player;
import ru.dragonestia.dguard.DGuard;

public class CustomMethods {

    private final DGuard main;

    public CanDoAllCondition canDoAllCondition;

    public RegionCommandCondition regionCommandCondition;

    public RegionCountChecker regionCountChecker;

    public RegionSizeChecker regionSizeChecker;

    public CustomMethods(DGuard main){
        this.main = main;

        init();
    }

    private void init(){
        canDoAllCondition = Player::isOp;
        regionCountChecker = (player, count) -> main.getSettingsConfig().getInt("max-count") > count || canDoAllCondition.check(player);
        regionSizeChecker = (player, size) -> main.getSettingsConfig().getLong("max-size") > size || canDoAllCondition.check(player);
    }

}

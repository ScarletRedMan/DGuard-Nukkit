package ru.dragonestia.dguard;

import cn.nukkit.utils.Config;
import lombok.Getter;

public class Settings {

    @Getter private final DGuard main;

    @Getter private boolean _3d;
    @Getter private int regionMaxCount;
    @Getter private int regionMaxSize;
    @Getter private boolean canBuildOutRegion;

    public Settings(DGuard main){
        this.main = main;
    }

    public void init(){
        Config config = new Config("plugins/DGuard/config.yml", Config.YAML);

        if(!config.exists("is-3d")) config.set("is-3d", false);
        if(!config.exists("max-count")) config.set("max-count", 2);
        if(!config.exists("max-size")) config.set("max-size", 10000);
        if(!config.exists("can-build-out-region")) config.set("can-build-out-region", true);
        config.save();

        _3d = config.getBoolean("is-3d");
        regionMaxCount = config.getInt("max-count");
        regionMaxSize = config.getInt("max-size");
        canBuildOutRegion = config.getBoolean("can-build-out-region");
    }

}

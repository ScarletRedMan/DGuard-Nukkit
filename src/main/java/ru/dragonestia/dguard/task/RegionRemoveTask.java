package ru.dragonestia.dguard.task;

import cn.nukkit.scheduler.AsyncTask;
import ru.dragonestia.dguard.DGuard;

public class RegionRemoveTask extends AsyncTask {

    private final String regionName;

    private final DGuard main;

    public RegionRemoveTask(String regionName, DGuard main){
        this.regionName = regionName;
        this.main = main;
    }

    @Override
    public void onRun() {
        main.getRegionsTag().remove(regionName);
    }

}

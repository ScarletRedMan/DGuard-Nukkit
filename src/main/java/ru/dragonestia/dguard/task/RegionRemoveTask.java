package ru.dragonestia.dguard.task;

import cn.nukkit.scheduler.AsyncTask;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.region.Region;

import java.io.File;

public class RegionRemoveTask extends AsyncTask {

    private final Region region;
    private final DGuard main;

    public RegionRemoveTask(Region region, DGuard main){
        this.region = region;
        this.main = main;
    }

    @Override
    public void onRun() {
        File file = new File("plugins/DGuard/regions/"+region.getId()+".json");

        file.delete();
    }

}

package ru.dragonestia.dguard.task;

import cn.nukkit.scheduler.AsyncTask;
import lombok.SneakyThrows;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.region.Region;

import java.io.File;
import java.io.FileWriter;

public class RegionSaveTask extends AsyncTask {

    private final Region region;
    private final DGuard main;

    public RegionSaveTask(Region region, DGuard main){
        this.region = region;
        this.main = main;
    }

    @SneakyThrows
    @Override
    public void onRun() {
        String json = DGuard.gson.toJson(region);
        File file = new File("plugins/DGuard/regions/"+region.getId()+".json");

        if(!file.exists()) file.createNewFile();

        try(FileWriter writer = new FileWriter(file)){
            writer.write(json);
        }
    }

}

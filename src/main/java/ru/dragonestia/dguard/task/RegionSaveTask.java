package ru.dragonestia.dguard.task;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.RegionsTag;

import java.io.IOException;

public class RegionSaveTask extends AsyncTask {

    private final CompoundTag compoundTag;

    private final DGuard main;

    public RegionSaveTask(CompoundTag compoundTag, DGuard main){
        this.compoundTag = compoundTag;
        this.main = main;
    }

    @Override
    public void onRun() {
        RegionsTag regionsTag = main.getRegionsTag();

        try {
            regionsTag.set(compoundTag.getName(), compoundTag);
        } catch (IOException ex){
            main.getLogger().error("Не удалось сохранить регион " + compoundTag.getName());
            ex.printStackTrace();
        }
    }

}

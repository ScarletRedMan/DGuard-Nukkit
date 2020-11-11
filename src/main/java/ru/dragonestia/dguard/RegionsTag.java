package ru.dragonestia.dguard;

import ru.dragonestia.ironlib.data.PluginTags;

public class RegionsTag extends PluginTags<DGuard> {

    public RegionsTag(DGuard owner) {
        super(owner);
    }

    @Override
    public String getDirName() {
        return "DGuardRegions";
    }

}

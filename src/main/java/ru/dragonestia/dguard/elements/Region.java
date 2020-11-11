package ru.dragonestia.dguard.elements;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.task.RegionRemoveTask;
import ru.dragonestia.dguard.task.RegionSaveTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Region {

    private final DGuard main;

    private final String id;

    public final int xMin, xMax, zMin, zMax;

    String owner;

    String levelName;

    private final ArrayList<String> members = new ArrayList<>();

    private final ArrayList<String> guests = new ArrayList<>();

    private CompoundTag flags = new CompoundTag();

    private boolean exist;

    Region(String regionName, int xMin, int xMax, int zMin, int zMax, DGuard main) {
        id = regionName.toLowerCase();

        exist = true;
        this.main = main;
        this.xMax = xMax;
        this.xMin = xMin;
        this.zMax = zMax;
        this.zMin = zMin;
    }

    public boolean isExist(){
        return exist;
    }

    public String getId() {
        return id;
    }

    public String getLevel(){
        return levelName;
    }

    public Point getMinPos(){
        return new Point(xMin, zMin);
    }

    public Point getMaxPos(){
        return new Point(xMax, zMax);
    }

    public long getLength(){
        return getMaxPos().x - getMinPos().x;
    }

    public long getWeight(){
        return getMaxPos().z - getMinPos().z;
    }

    public long getSize(){
        return getLength() * getWeight();
    }

    public String getOwner(){
        return owner;
    }

    public List<String> getMembers(){
        return members;
    }

    public List<String> getGuests(){
        return guests;
    }

    public Role getRole(String player){
        player = player.toLowerCase();

        if(getOwner().equals(player)) return Role.Owner;
        if(getMembers().contains(player)) return Role.Member;
        if(getGuests().contains(player)) return Role.Guest;
        return Role.Nobody;
    }

    public void setRole(String player, Role role){
        player = player.toLowerCase();

        switch (getRole(player)){
            case Owner:
                return;

            case Member:
                members.remove(player);
                break;

            case Guest:
                guests.remove(player);
                break;
        }

        switch (role){
            case Owner:
                members.add(getOwner());
                owner = player;
                break;

            case Member:
                if(!members.contains(player)){
                    members.add(player);
                }
                break;

            case Guest:
                if(!guests.contains(player)){
                    guests.add(player);
                }
                break;
        }
    }

    public void remove(){
        exist = false;
        DGuard.regions.remove(id);
        main.getServer().getScheduler().scheduleAsyncTask(main, new RegionRemoveTask(id, main));
    }

    CompoundTag getFlags(){
        return flags;
    }

    public boolean getFlag(Flag flag){
        return flag.getValue(this);
    }

    public void setFlag(Flag flag, boolean value){
        flags.putBoolean(flag.getId(), value);
    }

    public void save(){
        CompoundTag compoundTag = new CompoundTag(id);

        compoundTag.putInt("xMin", xMin);
        compoundTag.putInt("xMax", xMax);
        compoundTag.putInt("zMin", zMin);
        compoundTag.putInt("zMax", zMax);
        compoundTag.putString("levelName", levelName);
        compoundTag.putString("owner", owner);

        ListTag<StringTag> temp = new ListTag<>("members");
        for(String member: members){
            temp.add(new StringTag(member));
        }
        compoundTag.putList(temp);

        temp = new ListTag<>("guests");
        for(String guest: guests){
            temp.add(new StringTag(guest));
        }
        compoundTag.putList(temp);

        CompoundTag flags = new CompoundTag();
        HashMap<String, Flag> serverFlags = main.getFlags();
        for(String flagKey: serverFlags.keySet()){
            flags.putBoolean(flagKey, this.flags.getBoolean(flagKey));
        }

        compoundTag.putCompound("flags", flags);

        main.getServer().getScheduler().scheduleAsyncTask(main, new RegionSaveTask(compoundTag, main));
    }

    public static Region read(CompoundTag compoundTag, DGuard main){
        Region region = new Region(compoundTag.getName(), compoundTag.getInt("xMin"), compoundTag.getInt("xMax"), compoundTag.getInt("zMin"), compoundTag.getInt("zMax"), main);
        region.levelName = compoundTag.getString("levelName");
        region.owner = compoundTag.getString("owner");

        for(StringTag tag: compoundTag.getList("members", StringTag.class).getAll()){
            region.members.add(tag.parseValue());
        }

        for(StringTag tag: compoundTag.getList("guests", StringTag.class).getAll()){
            region.guests.add(tag.parseValue());
        }

        region.flags = compoundTag.getCompound("flags");

        return region;
    }

}

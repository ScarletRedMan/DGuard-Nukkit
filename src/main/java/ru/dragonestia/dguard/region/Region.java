package ru.dragonestia.dguard.region;

import cn.nukkit.Player;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.task.RegionRemoveTask;
import ru.dragonestia.dguard.task.RegionSaveTask;
import ru.dragonestia.dguard.util.Area;

import java.util.HashMap;
import java.util.HashSet;

@EqualsAndHashCode
public class Region {

    public transient static int freeId = 0;

    private transient DGuard main;
    @Getter @SerializedName("uid") private final int id;
    @Getter @Setter @SerializedName("name") private String name;
    @Getter @SerializedName("area") private final Area area;
    @Getter @SerializedName("level") private final String levelName;
    @Getter @SerializedName("owner") private String owner;
    @Getter @SerializedName("members") private final HashSet<String> members = new HashSet<>();
    @Getter @SerializedName("guests") private final HashSet<String> guests = new HashSet<>();
    @Getter @SerializedName("flags") private final HashMap<String, Boolean> flags = new HashMap<>();
    private transient boolean closed = false;

    public Region(String name, Area area, String levelName, String owner) {
        this.id = freeId++;
        this.name = name;
        this.area = area;
        this.levelName = levelName;
        this.owner = owner.toLowerCase();
    }

    public void init(DGuard main){
        this.main = main;
        if(freeId <= id) freeId = id + 1;

        if(!DGuard.regions.containsKey(levelName)) DGuard.regions.put(levelName, new HashMap<>());
        DGuard.regions.get(levelName).put(id, this);
    }

    public boolean isClosed(){
        return closed;
    }

    public Role getRole(String player){
        player = player.toLowerCase();

        if(getOwner().equals(player)) return Role.Owner;
        if(getMembers().contains(player)) return Role.Member;
        if(getGuests().contains(player)) return Role.Guest;
        return Role.Nobody;
    }

    public void setRole(Player player, Role role){
        setRole(player.getName(), role);
    }

    public void setRole(String playerName, Role role){
        playerName = playerName.toLowerCase();

        switch (getRole(playerName)){
            case Owner:
                return;

            case Member:
                members.remove(playerName);
                break;

            case Guest:
                guests.remove(playerName);
                break;
        }

        switch (role){
            case Owner:
                members.add(owner);
                owner = playerName;
                break;

            case Member:
                members.add(playerName);
                break;

            case Guest:
                guests.add(playerName);
                break;
        }
    }

    public synchronized void remove(){
        closed = true;
        DGuard.regions.remove(levelName).remove(id);
        main.getServer().getScheduler().scheduleAsyncTask(main, new RegionRemoveTask(this, main));
    }

    public boolean getFlag(Flag flag){
        return flag.getValue(this);
    }

    public void setFlag(Flag flag, boolean value){
        flags.put(flag.getId(), value);
    }

    public synchronized void save(boolean async){
        RegionSaveTask task = new RegionSaveTask(this, main);
        if(async) main.getServer().getScheduler().scheduleAsyncTask(main, task);
        else task.onRun();
    }

}

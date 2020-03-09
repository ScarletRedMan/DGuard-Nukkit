package ru.dragonestia.dguard.elements;

import cn.nukkit.Player;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Region {

    private String id;

    public Region(String id) throws RegionNotFoundException {
        this.id = id = id.toLowerCase();

        if(!isExist()) throw new RegionNotFoundException();
    }

    public boolean isExist(){
        return DGuard.areas.exists(id);
    }

    public String getId() {
        return id;
    }

    public String getLevel(){
        return DGuard.areas.getString(id + ".level");
    }

    public Point getMinPos(){
        return new Point(DGuard.areas.getInt(id + ".xMin"), DGuard.areas.getInt(id + ".zMin"));
    }

    public Point getMaxPos(){
        return new Point(DGuard.areas.getInt(id + ".xMax"), DGuard.areas.getInt(id + ".zMax"));
    }

    public long getSize(){
        return (getMaxPos().x - getMinPos().x) * (getMaxPos().z - getMinPos().z);
    }

    public String getOwner(){
        return DGuard.areas.getString(id + ".owner");
    }

    public List<String> getMembers(){
        return new ArrayList<>(DGuard.areas.getStringList(id + ".members"));
    }

    public List<String> getGuests(){
        return new ArrayList<>(DGuard.areas.getStringList(id + ".guests"));
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
        List<String> list;

        switch (getRole(player)){
            case Owner:
                return;

            case Member:
                list = getMembers();
                list.remove(player);

                DGuard.areas.set(id + ".members", list);
                DGuard.areas.save(true);
                break;

            case Guest:
                list = getGuests();
                list.remove(player);

                DGuard.areas.set(id + ".guests", list);
                DGuard.areas.save(true);
                break;
        }

        switch (role){
            case Owner:
                list = getMembers();
                list.add(getOwner());

                DGuard.areas.set(id + ".owner", player);
                DGuard.areas.set(id + ".members", list);
                DGuard.areas.save(true);
                break;

            case Member:
                list = getMembers();
                if(!list.contains(player)){
                    list.add(player);

                    DGuard.areas.set(id + ".members", list);
                    DGuard.areas.save(true);
                }
                break;

            case Guest:
                list = getGuests();
                if(!list.contains(player)){
                    list.add(player);

                    DGuard.areas.set(id + ".guests", list);
                    DGuard.areas.save(true);
                }
                break;
        }
    }

    public void remove(){
        DGuard.areas.remove(id);
        DGuard.areas.save(true);
    }

    public boolean getFlag(Flag flag){
        return flag.getValue(this);
    }

    public void setFlag(Flag flag, boolean value){
        DGuard.areas.set(id + ".flags." + flag.getId(), value);
        DGuard.areas.save(true);
    }

    public static void register(Player player, String id, String level, Point point1, Point point2) throws RegionLimitCountException, RegionLimitSizeException, InvalidRegionIdException, RegionAlreadyExistException, RegionIsCharacterizedByOtherRegionsException, PointsInDifferentLevelsException {
        RegionManager regionManager = new RegionManager(player);

        if(!DGuard.regionCountChecker.check(player, regionManager.getCount())) throw new RegionLimitCountException();

        if(!point1.level.equals(point2.level)) throw new PointsInDifferentLevelsException();

        Point min, max;
        min = Point.getMin(point1, point2);
        max = Point.getMax(point1, point2);

        min.level = max.level = point1.level;

        if(!DGuard.regionSizeChecker.check(player, (max.x - min.x) * (max.z - min.z))) throw new RegionLimitSizeException();

        if(Point.isPrivateArea(min, max)) throw new RegionIsCharacterizedByOtherRegionsException();

        id = id.trim().toLowerCase();

        Pattern pattern = Pattern.compile("^[aA-zZ\\d]+"); //Регулярка: Присутствуют только символы от a до z(двух регистров) + цифры
        if(!pattern.matcher(id).matches()) throw new InvalidRegionIdException();

        if(DGuard.areas.exists(id)) throw new RegionAlreadyExistException();

        //Создание региона
        DGuard.areas.set(id + ".owner", player.getName().toLowerCase());
        DGuard.areas.set(id + ".members", new ArrayList<String>());
        DGuard.areas.set(id + ".guests", new ArrayList<String>());
        DGuard.areas.set(id + ".level", level);
        DGuard.areas.set(id + ".xMin", min.x);
        DGuard.areas.set(id + ".zMin", min.z);
        DGuard.areas.set(id + ".xMax", max.x);
        DGuard.areas.set(id + ".zMax", max.z);

        DGuard.areas.save(true);
    }

}

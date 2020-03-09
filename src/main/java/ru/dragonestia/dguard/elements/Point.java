package ru.dragonestia.dguard.elements;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.exceptions.RegionNotFoundException;

import java.util.HashMap;

public class Point {

    public static HashMap<Player, Point> firstPoints = new HashMap<>();
    public static HashMap<Player, Point> secondPoints = new HashMap<>();

    public int x, z;
    public Level level;

    public Point(int x, int z){
        this.x = x;
        this.z = z;
    }

    public Point(int x, int z, Level level){
        this.x = x;
        this.z = z;
        this.level = level;
    }

    public Region getRegion(){
        Region region;
        for(String id: DGuard.areas.getAll().keySet()){
            try{
                region = new Region(id);
                if(!region.getLevel().equals(level.getName())) continue;

                if(region.getMinPos().x <= x && region.getMinPos().z <= z && x <= region.getMaxPos().x && z <= region.getMaxPos().z) return region;
            } catch (RegionNotFoundException ignored){

            }
        }
        return null;
    }

    public boolean isPrivate(){
        Region region;
        for(String id: DGuard.areas.getAll().keySet()){
            try{
                region = new Region(id);
                if(!region.getLevel().equals(level.getName())) continue;

                if(region.getMinPos().x <= x && region.getMinPos().z <= z && x <= region.getMaxPos().x && z <= region.getMaxPos().z) return true;
            } catch (RegionNotFoundException ignored){

            }
        }
        return false;
    }

    public static Point getMin(Point point1, Point point2){
        return new Point(Math.min(point1.x, point2.x), Math.min(point1.z, point2.z));
    }

    public static Point getMax(Point point1, Point point2){
        return new Point(Math.max(point1.x, point2.x), Math.max(point1.z, point2.z));
    }

    public static boolean isPrivateArea(Point p1, Point p2){
        Point min, max;
        min = getMin(p1, p2);
        max = getMax(p1, p2);

        for(String id: DGuard.areas.getAll().keySet()){
            if(!DGuard.areas.getString(id + ".level").equals(p1.level.getName())) continue;
            if(!(DGuard.areas.getInt(id + ".xMin") > max.x || DGuard.areas.getInt(id + ".xMax") < min.x || DGuard.areas.getInt(id + ".zMin") > max.z || DGuard.areas.getInt(id + ".zMax") < min.z)) return true;
        }
        return false;
    }

    public static void makeFirstPos(Player player, Position pos){
        firstPoints.put(player, new Point((int) pos.x, (int) pos.z, pos.level));
    }

    public static void makeSecondPos(Player player, Position pos){
        secondPoints.put(player, new Point((int) pos.x, (int) pos.z, pos.level));
    }

}

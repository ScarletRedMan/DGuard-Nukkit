package ru.dragonestia.dguard.elements;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import ru.dragonestia.dguard.DGuard;

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
        for(Region region: DGuard.regions.values()){
            if(!region.getLevel().equals(level.getName()) || region.xMax < x || region.zMax < z || z < region.zMin || x < region.zMin) continue;
            return region;
        }
        return null;
    }

    public boolean isPrivate(){
        for(Region region: DGuard.regions.values()){
            if(!region.getLevel().equals(level.getName()) || region.xMax < x || region.zMax < z || z < region.zMin || x < region.zMin) continue;
            return true;
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
        String levelName = p1.level.getName();
        min = getMin(p1, p2);
        max = getMax(p1, p2);

        for(Region region: DGuard.regions.values()){
            if(!levelName.equals(region.getLevel())) continue;
            if(!(region.xMin > max.x || region.xMax < min.x || region.zMin > max.z || region.zMax < min.z)) return true;
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

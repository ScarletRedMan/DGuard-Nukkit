package ru.dragonestia.dguard.util;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.google.gson.annotations.SerializedName;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.region.Region;

public class Point {

    @SerializedName("x") public int x;
    @SerializedName("y") public int y;
    @SerializedName("z") public int z;

    public Point(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Vector3 pos){
        this(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    public Region getRegion(Level level){
        String levelName = level.getName();

        if(!DGuard.regions.containsKey(levelName)) return null;
        for(Region region: DGuard.regions.get(levelName).values()){
            if(region.getArea().isInside(this)) return region;
        }
        return null;
    }

    public static Point min(Point p1, Point p2){
        return new Point(
                Math.min(p1.x, p2.x),
                Math.min(p1.y, p2.y),
                Math.min(p1.z, p2.z)
        );
    }

    public static Point max(Point p1, Point p2){
        return new Point(
                Math.max(p1.x, p2.x),
                Math.max(p1.y, p2.y),
                Math.max(p1.z, p2.z)
        );
    }

}

package ru.dragonestia.dguard.util;

import cn.nukkit.level.Level;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.region.Region;

public class Area {

    @Getter @SerializedName("minPos") private final Point minPos;
    @Getter @SerializedName("maxPos") private final Point maxPos;

    public Area(Point p1, Point p2){
        minPos = Point.min(p1, p2);
        maxPos = Point.max(p1, p2);
    }

    public int deltaX(){
        return maxPos.x - minPos.x;
    }

    public int deltaY(){
        return maxPos.y - minPos.y;
    }

    public int deltaZ(){
        return maxPos.z - minPos.z;
    }

    public long getSpace(boolean _3d){
        return (long) deltaX() * deltaZ() * (_3d? deltaY() : 1);
    }

    public boolean isInside(Point point){
        return minPos.x <= point.x && point.x <= maxPos.x &&
                minPos.y <= point.y && point.y <= maxPos.y &&
                minPos.z <= point.z && point.z <= maxPos.z;
    }

    public boolean isNotCollided(Area area){
        if(area.minPos.x > maxPos.x || area.maxPos.x < minPos.x) return true;
        if(area.minPos.y > maxPos.y || area.maxPos.y < minPos.y) return true;
        return area.minPos.z > maxPos.z || area.maxPos.z < minPos.z;
    }

    public boolean isCollided(Area area){
        return !isNotCollided(area);
    }

    public boolean isPrivateArea(Level level){
        String levelName = level.getName();

        if(!DGuard.regions.containsKey(levelName)) return false;

        for(Region region: DGuard.regions.get(levelName).values()){
            if(isCollided(region.getArea())) return true;
        }
        return false;
    }

}

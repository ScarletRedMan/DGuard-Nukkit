package ru.dragonestia.dguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import ru.dragonestia.dguard.elements.Flag;
import ru.dragonestia.dguard.elements.Point;
import ru.dragonestia.dguard.elements.Region;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPvp(EntityDamageEvent event){
        if(!(event instanceof EntityDamageByEntityEvent)) return;

        if(event.getEntity() instanceof Player && ((EntityDamageByEntityEvent)event).getDamager() instanceof Player){
            Player player, damager;
            player = (Player) event.getEntity();
            damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();

            Point to, from;
            to = new Point((int) player.x, (int) player.z, player.level);
            from = new Point((int) damager.x, (int) damager.z, damager.level);

            Region region;

            region = to.getRegion();
            if(region != null){
                if(!region.getFlag(Flag.flags.get("pvp"))){
                    damager.sendTip("§cВ данном месте PvP отключено");
                    event.setCancelled();
                }
            }

            region = from.getRegion();
            if(region != null){
                if(!region.getFlag(Flag.flags.get("pvp"))){
                    damager.sendTip("§cВ данном месте PvP отключено");
                    event.setCancelled();
                }
            }
        }
    }

}

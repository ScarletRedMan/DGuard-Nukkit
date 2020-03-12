package ru.dragonestia.dguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.Forms;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTap(PlayerInteractEvent event){
        if(!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();

        if(player.getInventory().getItemInHand().getId() != Item.STICK) return;
        if(!DGuard.regionCommandCondition.check(player)) return;
        event.setCancelled();

        Point point = new Point((int) event.getBlock().x, (int) event.getBlock().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            player.sendTip("§6Здесь нет регионов");
            return;
        }

        Forms.getInstance().f_region_info(player, region);
    }

}

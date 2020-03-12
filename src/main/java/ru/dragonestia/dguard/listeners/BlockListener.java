package ru.dragonestia.dguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.player.PlayerBedEnterEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.elements.Flag;
import ru.dragonestia.dguard.elements.Point;
import ru.dragonestia.dguard.elements.Region;
import ru.dragonestia.dguard.elements.Role;

import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlock().x, (int)event.getBlock().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region")){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
        }else{
            if(!DGuard.canDoAllCondition.check(player))
            switch (region.getRole(player.getName())){
                case Nobody:
                    event.setCancelled();
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    break;

                case Guest:
                    event.setCancelled();
                    player.sendTip("§cВам не разрешено строить в данном регионе");
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlock().x, (int)event.getBlock().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region")){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
        }else{
            if(!DGuard.canDoAllCondition.check(player))
            switch (region.getRole(player.getName())){
                case Nobody:
                    event.setCancelled();
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    break;

                case Guest:
                    event.setCancelled();
                    player.sendTip("§cВам не разрешено строить в данном регионе");
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlockClicked().x, (int)event.getBlockClicked().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region")){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
        }else{
            if(!DGuard.canDoAllCondition.check(player))
            switch (region.getRole(player.getName())){
                case Nobody:
                    event.setCancelled();
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    break;

                case Guest:
                    event.setCancelled();
                    player.sendTip("§cВам не разрешено строить в данном регионе");
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlockClicked().x, (int)event.getBlockClicked().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region")){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
        }else{
            if(!DGuard.canDoAllCondition.check(player))
            switch (region.getRole(player.getName())){
                case Nobody:
                    event.setCancelled();
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    break;

                case Guest:
                    event.setCancelled();
                    player.sendTip("§cВам не разрешено строить в данном регионе");
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedEnter(PlayerBedEnterEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getBed().x, (int)event.getBed().z, player.getLevel());
        Region region = point.getRegion();

        if(region != null){
            if (region.getRole(player.getName()) == Role.Nobody && !DGuard.canDoAllCondition.check(player)) {
                event.setCancelled();
                player.sendTip("§cУ вас нет доступа к данному региону");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFrameDropItem(ItemFrameDropItemEvent event){
        Player player = event.getPlayer();
        Point point = new Point((int)event.getItemFrame().x, (int)event.getItemFrame().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region")){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
        }else{
            if(!DGuard.canDoAllCondition.check(player))
            switch (region.getRole(player.getName())){
                case Nobody:
                    event.setCancelled();
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    break;

                case Guest:
                    event.setCancelled();
                    player.sendTip("§cВам не разрешено строить в данном регионе");
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBurn(BlockBurnEvent event){
        Point point = new Point((int)event.getBlock().x, (int)event.getBlock().z, event.getBlock().getLevel());
        Region region = point.getRegion();

        if(region != null){
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTap(PlayerInteractEvent event){
        if(!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlock().x, (int)event.getBlock().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            if(!DGuard.config.getBoolean("can-build-out-region") && !DGuard.canDoAllCondition.check(player)){
                event.setCancelled();
                player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            }
            return;
        }

        List<Integer> items = Arrays.asList(259, 325, 269, 273, 256, 284, 277, 290, 291, 292, 294, 293);
        List<Integer> doors = Arrays.asList(64, 193, 195, 196, 197, 194, 71, 96, 167, 107, 183, 184, 185, 187, 186);
        List<Integer> furnaces = Arrays.asList(61, 62);
        List<Integer> chests = Arrays.asList(54, 146, 218);

        if(items.contains(player.getInventory().getItemInHand().getId()) && region.getRole(player.getName()).getId() < Role.Member.getId() && !DGuard.canDoAllCondition.check(player)){
            event.setCancelled();
            player.sendTip("§cУ вас не доступа к данному региону");
            return;
        }

        if(doors.contains(event.getBlock().getId()) && region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(Flag.flags.get("doors")) && !DGuard.canDoAllCondition.check(player)){
            event.setCancelled();
            player.sendTip("§cУ вас не доступа к данному региону");
            return;
        }

        if(furnaces.contains(event.getBlock().getId()) && region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(Flag.flags.get("furnace")) && !DGuard.canDoAllCondition.check(player)){
            event.setCancelled();
            player.sendTip("§cУ вас не доступа к данному региону");
            return;
        }

        if(chests.contains(event.getBlock().getId()) && region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(Flag.flags.get("chests")) && !DGuard.canDoAllCondition.check(player)){
            event.setCancelled();
            player.sendTip("§cУ вас не доступа к данному региону");
            return;
        }
    }

}

package ru.dragonestia.dguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
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
import cn.nukkit.math.Vector3;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.elements.Point;
import ru.dragonestia.dguard.elements.Region;
import ru.dragonestia.dguard.elements.Role;

public class BlockListener implements Listener {

    private final DGuard main;

    private final CustomMethods customMethods;

    private final int[] blockedItems, doors, chests, furnaces, redstone, other;

    public BlockListener(DGuard main){
        this.main = main;
        customMethods = main.getCustomMethods();

        blockedItems = new int[]{259, 325, 269, 273, 256, 284, 277, 290, 291, 292, 294, 293};
        doors = new int[]{64, 193, 194, 195, 196, 197, 404, 401, 403, 402, 400, 96, 107, 183, 184, 185, 187, 186};
        chests = new int[]{458, 205, 54, 146};
        furnaces = new int[]{453, 451, 61};
        redstone = new int[]{77, 143, 399, 396, 398, 395, 397, 69};
        other = new int[]{138, 449, 117, 125, 23, 468, 154, 84, 83, 149};
    }

    private boolean checkBuild(Event event, Player player, Vector3 pos){
        if(event.isCancelled()) return true;

        Region region = new Point((int) pos.x, (int) pos.z, player.getLevel()).getRegion();

        if (region == null) {
            if (main.isCanBuildOutRegion()) return false;

            player.sendTip("§cЧтобы строить здесь вам нужно создать регион");
            return true;
        }

        if (customMethods.canDoAllCondition.check(player)) return false;

        switch (region.getRole(player.getName())) {
            case Nobody:
            case Guest:
                player.sendTip("§cУ вас нет доступа чтобы делать это в данном регионе.");
                return true;

        }
        return false;
    }

    private boolean checkTap(PlayerInteractEvent event, Region region, int[] securedId, String flagName){
        if(event.isCancelled()) return true;
        Player player = event.getPlayer();

        for(int id: securedId){
            if(id == event.getBlock().getId()){
                if(region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(main.getFlags().get(flagName)) && !customMethods.canDoAllCondition.check(player)){
                    player.sendTip("§cУ вас не доступа к данному региону");
                    return true;
                }else break;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(checkBuild(event, event.getPlayer(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(checkBuild(event, event.getPlayer(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event){
        event.setCancelled(checkBuild(event, event.getPlayer(), event.getBlockClicked()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent event){
        event.setCancelled(checkBuild(event, event.getPlayer(), event.getBlockClicked()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Point point = new Point((int) event.getBed().x, (int) event.getBed().z, player.getLevel());
        Region region = point.getRegion();

        if (region == null) return;

        if (region.getRole(player.getName()) == Role.Nobody && !customMethods.canDoAllCondition.check(player)) {
            event.setCancelled(true);
            player.sendTip("§cУ вас нет доступа к данному региону");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFrameDropItem(ItemFrameDropItemEvent event){
        event.setCancelled(checkBuild(event, event.getPlayer(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBurn(BlockBurnEvent event){
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTap(PlayerInteractEvent event){
        if(!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        Point point = new Point((int)event.getBlock().x, (int)event.getBlock().z, player.getLevel());
        Region region = point.getRegion();

        if(region == null){
            return;
        }

        for(int id: blockedItems){
            if(id == player.getInventory().getItemInHand().getId()){
                if(region.getRole(player.getName()).getId() < Role.Member.getId() && !customMethods.canDoAllCondition.check(player)){
                    player.sendTip("§cУ вас не доступа к данному региону");
                    event.setCancelled(true);
                    return;
                }else break;
            }
        }

        for(int id: doors){
            if(id == event.getBlock().getId()){
                if(region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(main.getFlags().get("doors")) && !customMethods.canDoAllCondition.check(player)){
                    player.sendTip("§cУ вас не доступа к данному региону");
                    event.setCancelled(true);
                    return;
                }else break;
            }
        }

        event.setCancelled(checkTap(event, region, chests, "chests") || checkTap(event, region, furnaces, "furnace") || checkTap(event, region, redstone, "redstone"));
    }

}

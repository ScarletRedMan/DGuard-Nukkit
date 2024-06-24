package ru.dragonestia.dguard.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerBedEnterEvent;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.inventory.ChestInventory;
import cn.nukkit.inventory.FurnaceTypeInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.region.Region;
import ru.dragonestia.dguard.region.Role;
import ru.dragonestia.dguard.util.Point;

import java.util.Arrays;
import java.util.HashSet;

public class BlockListener implements Listener {

    private final DGuard main;

    private final CustomMethods customMethods;

    private final HashSet<Integer> blockedItems, doors, chests, furnaces, redstone, other;

    public BlockListener(DGuard main){
        this.main = main;
        customMethods = main.getCustomMethods();

        blockedItems = new HashSet<>(Arrays.asList(259, 325, 269, 273, 256, 284, 277, 290, 291, 292, 294, 293));
        doors = new HashSet<>(Arrays.asList(64, 193, 194, 195, 196, 197, 404, 401, 403, 402, 400, 96, 107, 183, 184, 185, 187, 186));
        chests = new HashSet<>(Arrays.asList(458, 205, 54, 146));
        furnaces = new HashSet<>(Arrays.asList(453, 451, 61));
        redstone = new HashSet<>(Arrays.asList(77, 143, 399, 396, 398, 395, 397, 69));
        other = new HashSet<>(Arrays.asList(138, 449, 117, 125, 23, 468, 154, 84, 83, 149));
    }

    private boolean checkBuild(Event event, Player player, Vector3 pos){
        if(event.isCancelled()) return true;

        Region region = new Point(pos).getCacheRegion(player);

        if (region == null) {
            if (main.getSettings().isCanBuildOutRegion()) return false;

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

    private boolean checkTap(PlayerInteractEvent event, Region region, HashSet<Integer> securedId, String flagName){
        if(event.isCancelled()) return true;
        Player player = event.getPlayer();

        if(securedId.contains(event.getBlock().getId())){
            if(region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(main.getFlags().get(flagName)) && !customMethods.canDoAllCondition.check(player)){
                player.sendTip("§cУ вас нет доступа к данному региону");
                return true;
            } else return false;
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
        Point point = new Point(event.getBed());
        Region region = point.getCacheRegion(player);

        if (region == null) return;

        if (region.getRole(player.getName()) == Role.Nobody && !customMethods.canDoAllCondition.check(player)) {
            event.setCancelled(true);
            player.sendTip("§cУ вас нет доступа к данному региону");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBurn(BlockBurnEvent event){
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTap(PlayerInteractEvent event) {
        if (!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getBlock();

        // Проверяем, является ли кликнутый блок деревянными воротами (BlockFenceGate)
        if (clickedBlock instanceof BlockFenceGate) {
            Point point = new Point(clickedBlock);
            Region region = point.getCacheRegion(player);

            if (region == null) {
                return;
            }

            // Проверяем разрешения региона
            if (region.getRole(player.getName()).getId() < Role.Member.getId() && !customMethods.canDoAllCondition.check(player)) {
                player.sendTip("§cУ вас нет доступа к данному региону");
                event.setCancelled(true);
                return;
            }
        }

        if(doors.contains(event.getBlock().getId())){
            Point point = new Point(clickedBlock);
            Region region = point.getCacheRegion(player);
            if(region.getRole(player.getName()).equals(Role.Nobody) && !region.getFlag(main.getFlags().get("doors")) && !customMethods.canDoAllCondition.check(player)){
                player.sendTip("§cУ вас нет доступа к данному региону");
                event.setCancelled(true);
                return;
            }
        }
        Point point = new Point(clickedBlock);
        Region region = point.getCacheRegion(player);
        event.setCancelled(checkTap(event, region, chests, "chests") || checkTap(event, region, furnaces, "furnace") || checkTap(event, region, redstone, "redstone"));
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onFlintAndSteelUse(PlayerInteractEvent event) {
        if (!event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        if (player.getInventory().getItemInHand().getId() == Item.FLINT_AND_STEEL) {
            Point point = new Point(event.getBlock());
            Region region = point.getCacheRegion(player);

            if (region != null && region.getRole(player.getName()) == Role.Nobody && !customMethods.canDoAllCondition.check(player)) {
                player.sendTip("§cУ вас нет доступа к данному региону");
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onChestInventoryOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockEntity blockEntity = block.getLevel().getBlockEntity(block);

        if (blockEntity instanceof BlockEntityChest) {
            BlockEntityChest chestBlockEntity = (BlockEntityChest) blockEntity;
            ChestInventory chestInventory = new ChestInventory(chestBlockEntity);
            Point point = new Point(event.getBlock());
            Region region = point.getCacheRegion(player);

            if (region != null && region.getRole(player.getName()) == Role.Nobody && !customMethods.canDoAllCondition.check(player)) {
                if (!region.getFlag(main.getFlags().get("chests"))) {
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    event.setCancelled(true);

                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceInventoryOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockEntity blockEntity = block.getLevel().getBlockEntity(block);

        if (blockEntity instanceof BlockEntityFurnace) {
            BlockEntityFurnace furnaceBlockEntity = (BlockEntityFurnace) blockEntity;
            FurnaceTypeInventory furnaceInventory = new FurnaceTypeInventory(furnaceBlockEntity);
            Point point = new Point(event.getBlock());
            Region region = point.getCacheRegion(player);

            if (region != null && region.getRole(player.getName()) == Role.Nobody && !customMethods.canDoAllCondition.check(player)) {
                if (!region.getFlag(main.getFlags().get("furnace"))) {
                    player.sendTip("§cУ вас нет доступа к данному региону");
                    event.setCancelled(true);

                }
            }
        }
    }
}


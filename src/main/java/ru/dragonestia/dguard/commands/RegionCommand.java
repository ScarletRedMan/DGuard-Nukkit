package ru.dragonestia.dguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.elements.Point;
import ru.dragonestia.dguard.elements.Region;

public class RegionCommand extends Command {

    private final DGuard main;

    public RegionCommand(DGuard main){
        super("region", "Управление регионами", "/region", new String[]{"rg"});

        getCommandParameters().clear();
        addCommandParameters("dguard", new CommandParameter[]{
                new CommandParameter("Агрумент", new String[]{"pos1", "pos2", "info", "edit"})
        });

        this.main = main;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Данную команду можно использовать только в игре!");
            return false;
        }

        Player player = (Player) sender;
        if(args.length == 0){
            main.getForms().f_menu(player);
            return true;
        }

        Region region;

        switch (args[0].toLowerCase()){
            case "pos1":
                Point.makeFirstPos(player, new Position(player.x, 0, player.z, player.level));
                sender.sendMessage("§6§lПервая точка§e была установлена успешно!");
                break;

            case "pos2":
                Point.makeSecondPos(player, new Position(player.x, 0, player.z, player.level));
                sender.sendMessage("§6§lВторая точка§e была установлена успешно!");
                break;

            case "info":
                region = new Point(player.getFloorX(), player.getFloorZ(), player.getLevel()).getRegion();

                if(region == null){
                    player.sendMessage("§eВ данной местности нет регионов.");
                    return true;
                }

                main.getForms().f_region_info(player, region);
                break;

            case "edit":
                region = new Point(player.getFloorX(), player.getFloorZ(), player.getLevel()).getRegion();

                if(region == null){
                    player.sendMessage("§eВ данной местности нет регионов.");
                    return true;
                }

                if(region.getOwner().equals(player.getName().toLowerCase())){
                    main.getForms().f_edit_menu(player, region);
                    return true;
                }
                player.sendMessage("§cВы не являетесь владельцем данного региона.");
                break;

            default:
                sender.sendMessage("§2/rg §f- §7Меню");
                sender.sendMessage("§2/rg pos1 §f- §7Установить 1 точку");
                sender.sendMessage("§2/rg pos2§f- §7Установить 2 точку");
                sender.sendMessage("§2/rg info§f- §7Информация о текущем регионе");
                sender.sendMessage("§2/rg edit§f- §7Управление текущим регионом");
        }
        return true;
    }

}

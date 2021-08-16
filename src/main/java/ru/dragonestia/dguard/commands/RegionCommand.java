package ru.dragonestia.dguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.util.Point;

public class RegionCommand extends Command {

    private final DGuard main;

    public RegionCommand(DGuard main){
        super("region", "Управление регионами", "/region", new String[]{"rg"});

        setPermission("dguard.use");

        addCommandParameters("dguard", new CommandParameter[]{
                new CommandParameter("Агрумент", new String[]{"pos1", "pos2"})
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
            main.getForms().sendMainForm(player);
            return true;
        }

        Point point;
        switch (args[0].toLowerCase()){
            case "pos1":
                point = new Point(player);
                if(!main.getSettings().is_3d()) point.y = -1;
                main.getFirstPoints().put(player.getId(), point);
                sender.sendMessage("§6§lПервая точка§e была установлена успешно!");
                break;

            case "pos2":
                point = new Point(player);
                if(!main.getSettings().is_3d()) point.y = 256;
                main.getSecondPoints().put(player.getId(), point);
                sender.sendMessage("§6§lВторая точка§e была установлена успешно!");
                break;

            default:
                sender.sendMessage("§2/rg §f- §7Меню");
                sender.sendMessage("§2/rg pos1 §f- §7Установить 1 точку");
                sender.sendMessage("§2/rg pos2 §f- §7Установить 2 точку");
        }
        return true;
    }

}

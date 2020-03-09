package ru.dragonestia.dguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.Forms;
import ru.dragonestia.dguard.elements.Point;

public class RegionCommand extends Command {

    public RegionCommand(){
        super("region", "Управление регионами", "/region", new String[]{"rg"});

        getCommandParameters().clear();
        addCommandParameters("pos", new CommandParameter[]{
                new CommandParameter("Агрумент", new String[]{"pos1", "pos2"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Данную команду можно использовать только в игре!");
            return false;
        }

        Player player = (Player) sender;

        if(!DGuard.regionCommandCondition.check(player)) return false;

        if(args.length == 0){
            Forms.getInstance().f_menu(player);
            return true;
        }

        switch (args[0].toLowerCase()){
            case "pos1":
                Point.makeFirstPos(player, new Position(player.x, 0, player.z, player.level));
                sender.sendMessage("§6§lПервая точка§e была установлена успешно!");
                break;

            case "pos2":
                Point.makeSecondPos(player, new Position(player.x, 0, player.z, player.level));
                sender.sendMessage("§6§lВторая точка§e была установлена успешно!");
                break;
        }
        return true;
    }

}

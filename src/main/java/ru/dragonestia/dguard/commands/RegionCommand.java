package ru.dragonestia.dguard.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.dragonestia.dguard.DGuard;

public class RegionCommand extends Command {

    public RegionCommand(){
        super("region", "Управление регионами", "/region", new String[]{"rg"});

        getCommandParameters().clear();
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

            return true;
        }

        return true;
    }
}

package me.cuft.portalcodes;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{
    private final PortalCodes main;
    public Commands(PortalCodes main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {return true;}

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("codes"))
        {
            if(player.isOp())
            {
                GUI gui = new GUI(main);
                gui.openInventory(player);
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You do not have the required permissions");
            }

        }

        return true;
    }
}


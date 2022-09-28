package me.cuft.portalcodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Orientable;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PortalCodes extends JavaPlugin implements Listener {

    private final ConsoleCommandSender console = getServer().getConsoleSender();
    public ArrayList<Code> codes = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults(true);
        saveConfig();

        Commands cmds = new Commands(this);
        getCommand("codes").setExecutor(cmds);

        initializeConfig();
        getServer().getPluginManager().registerEvents(new GUI(this), this);
        getServer().getPluginManager().registerEvents(new SearchGUI(this), this);
        getServer().getPluginManager().registerEvents(new AddGUI(this), this);
        getServer().getPluginManager().registerEvents(new CodeInfoGUI(this, null), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void initializeConfig()
    {
        for(String key : getConfig().getConfigurationSection("codes").getKeys(false))
        {
            List<String> blocks = new ArrayList<>(Arrays.asList(key.replaceAll("^\\[|]$", "").replace(" ", "").split(",")));
            String color = getConfig().getString( "codes." + key + ".color");
            String title = getConfig().getString("codes." + key + ".title");
            String subtitle = getConfig().getString("codes." + key + ".subtitle");
            List<String> commands = (List<String>) getConfig().getList("codes." + key + ".commands");

            codes.add(new Code(blocks, color, title, subtitle, commands));
        }
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event)
    {
        Player player = event.getPlayer();

        Location location = event.getFrom();

        ArrayList<String> cornerBlocks = new ArrayList<>();

        if(!(location.getBlock().getType() == Material.NETHER_PORTAL))
        {
            if(location.clone().add(1,0,0).getBlock().getType() == Material.NETHER_PORTAL)
            {
                location.add(1,0,0);
            }
            else if(location.clone().add(-1,0,0).getBlock().getType() == Material.NETHER_PORTAL)
            {
                location.add(-1,0,0);
            }
            else if(location.clone().add(0,0,1).getBlock().getType() == Material.NETHER_PORTAL)
            {
                location.add(0,0,1);
            }
            else if(location.clone().add(0,0,-1).getBlock().getType() == Material.NETHER_PORTAL)
            {
                location.add(0,0,-1);
            }
        }

        String axis = ((Orientable) location.getBlock().getBlockData()).getAxis().name();

        cornerBlocks.add(upperBlock(rightBlock(location.clone(), axis)).getBlock().getType().name());
        cornerBlocks.add(upperBlock(leftBlock(location.clone(), axis)).getBlock().getType().name());

        Code outcome = checkCombination(cornerBlocks);

        player.sendTitle(ChatColor.valueOf(outcome.getColor()) + outcome.getTitle(), ChatColor.GRAY + outcome.getSubtitle(), 20, 50, 20);

        for(String command : outcome.getCommands())
        {
            Bukkit.dispatchCommand(console, command.replace("{PLAYER}", player.getName()));
        }
        
        event.setCancelled(true);
    }

    public Location rightBlock(Location loc, String axis)
    {
        if(axis == "Z")
        {
            do
            {
                loc.add(0,0,1);
            } while (!(loc.getBlock().getType() == Material.OBSIDIAN));
        }
        else
        {
            do
            {
                loc.add(1,0,0);
            } while (!(loc.getBlock().getType() == Material.OBSIDIAN));
        }

        return loc;
    }

    public Location leftBlock(Location loc, String axis)
    {
        if(axis == "Z")
        {
            do
            {
                loc.subtract(0,0,1);
            } while (!(loc.getBlock().getType() == Material.OBSIDIAN));
        }
        else
        {
            do
            {
                loc.subtract(1,0,0);
            } while(!(loc.getBlock().getType() == Material.OBSIDIAN));
        }

        return loc;
    }

    public Location upperBlock(Location loc)
    {
        do
        {
            loc.add(0,1,0);
        } while((loc.getBlock().getType() == Material.OBSIDIAN) && (loc.clone().add(1,0,0).getBlock().getType() == Material.NETHER_PORTAL || loc.clone().add(-1,0,0).getBlock().getType() == Material.NETHER_PORTAL || loc.clone().add(0,0,1).getBlock().getType() == Material.NETHER_PORTAL || loc.clone().add(0,0,-1).getBlock().getType() == Material.NETHER_PORTAL));

        return loc;
    }

    public Code checkCombination(List<String> cornerBlocks)
    {
        Collections.sort(cornerBlocks);

        for(Code code : codes){
            List<String> combinationCode = code.getBlocks();
            Collections.sort(combinationCode);

            if(combinationCode.equals(cornerBlocks))
            {
                return code;
            }
        }

        return new Code(null, getConfig().getString("default.color"), getConfig().getString("default.title"), getConfig().getString("default.subtitle"), (List<String>) getConfig().getList("default.commands"));
    }
}

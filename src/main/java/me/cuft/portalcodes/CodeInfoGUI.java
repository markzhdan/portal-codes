package me.cuft.portalcodes;

import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CodeInfoGUI implements Listener {
    private final Inventory inv;
    private final PortalCodes main;
    Code code;
    int timesClicked = 0;

    public CodeInfoGUI(PortalCodes main, Code code) {
        this.main = main;
        this.code = code;
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "§fCode Info");

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        if(code != null)
        {
            inv.addItem(new ItemStack(Material.getMaterial(code.getBlocks().get(0)), 1));
            inv.addItem(new ItemStack(Material.getMaterial(code.getBlocks().get(1)), 1));
            inv.addItem(createGuiItem(Material.PAPER, "§fColor:", ChatColor.valueOf(code.getColor()) + code.getColor(), "", "§7Click to edit"));
            inv.addItem(createGuiItem(Material.PAPER, "§fTitle:", ChatColor.GREEN + code.getTitle(), "", "§7Click to edit"));
            inv.addItem(createGuiItem(Material.PAPER, "§fSubtitle:", ChatColor.GREEN + code.getSubtitle(), "", "§7Click to edit"));
            inv.addItem(createGuiItem(Material.PAPER, "§fCommands:", ChatColor.GREEN + code.getCommands().toString(), "", "§7Click to edit"));
            inv.setItem(8, createGuiItem(Material.BARRIER, "§4Delete"));
        }
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        assert meta != null;
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if(!e.getView().getTitle().equalsIgnoreCase("§fCode Info")) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        switch(e.getRawSlot()){
            case 0:
                AddGUI addGUI = new AddGUI(main);
                addGUI.openInventory(p);
                break;
            case 8:
                if(timesClicked < 1)
                {
                    p.sendMessage("§cClick again to delete");
                    timesClicked++;
                }
                else
                {
                    main.getConfig().set(code.getBlocks().toString(), null);
                    main.codes.remove(code);
                    p.closeInventory();
                }
                break;
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
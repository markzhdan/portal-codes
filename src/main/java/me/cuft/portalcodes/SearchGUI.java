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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchGUI implements Listener {
    private final Inventory inv;
    private final PortalCodes main;

    public SearchGUI(PortalCodes main) {
        this.main = main;
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "§fSearch");

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.setItem(8, createGuiItem(Material.GREEN_WOOL, "§aConfirm", "§9Press after inputting blocks"));
        inv.addItem(createGuiItem(Material.QUARTZ_BLOCK, "§aBlock One", "§9Drag a block over to set search filter"));
        inv.addItem(createGuiItem(Material.QUARTZ_BLOCK, "§aBlock Two", "§9Drag a block over to set search filter"));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
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
        if(!e.getView().getTitle().equalsIgnoreCase("§fSearch")) return;

        if(e.getRawSlot() <= 8)
        {
            e.setCancelled(true);
        }

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        switch(e.getRawSlot()){
            case 0:
                if(p.getItemOnCursor().getType() != Material.AIR)
                {
                    ItemStack tempItem = p.getItemOnCursor();
                    tempItem.setAmount(1);
                    p.setItemOnCursor(null);
                    p.getOpenInventory().setItem(0, tempItem);
                }
                else
                {
                    p.sendMessage("§cMust have a block selected");
                }
                break;
            case 1:
                if(p.getItemOnCursor().getType() != Material.AIR)
                {
                    ItemStack tempItem = p.getItemOnCursor();
                    tempItem.setAmount(1);
                    p.setItemOnCursor(null);
                    p.getOpenInventory().setItem(1, tempItem);
                }
                else
                {
                    p.sendMessage("§cMust have a block selected");
                }
                break;
            case 8:
                if(!p.getOpenInventory().getItem(0).getItemMeta().getDisplayName().contains("§aBlock") && !p.getOpenInventory().getItem(1).getItemMeta().getDisplayName().contains("§aBlock"))
                {
                    //search function
                    List<String> searchBlocks = new ArrayList<>();
                    searchBlocks.add(p.getOpenInventory().getItem(0).getType().toString());
                    searchBlocks.add(p.getOpenInventory().getItem(1).getType().toString());
                    Code code = findCode(searchBlocks);

                    if(code == null)
                    {
                        p.getOpenInventory().setItem(0, createGuiItem(Material.QUARTZ_BLOCK, "§aBlock One", "§9Drag a block over to set search filter"));
                        p.getOpenInventory().setItem(1, createGuiItem(Material.QUARTZ_BLOCK, "§aBlock Two", "§9Drag a block over to set search filter"));
                        p.sendMessage("§cCould not find code");
                    }
                    else
                    {
                        CodeInfoGUI codeInfoGUI = new CodeInfoGUI(main, code);
                        codeInfoGUI.openInventory(p);
                    }
                }
                else
                {
                    p.sendMessage("§cEnter both blocks to begin search");
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

    public Code findCode(List<String> searchBlocks)
    {
        Collections.sort(searchBlocks);

        for(Code code : main.codes){
            List<String> codeBlocks = code.getBlocks();
            Collections.sort(codeBlocks);

            if(codeBlocks.equals(searchBlocks))
            {
                return code;
            }
        }

        return null;
    }
}
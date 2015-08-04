package net.samongi.CraftingMenu.Listeners;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Menu.Menu;
import net.samongi.CraftingMenu.Menu.MenuManager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ClickListener implements Listener
{
  private static void debugLog(String msg){CraftingMenu.debugLog("[ClickListener] " + msg);}
  
  @SuppressWarnings("deprecation")
  @EventHandler
  public void onPlayerClick(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    Block block = event.getClickedBlock();
    Material block_type = block.getType();
    short block_data = block.getData();
    String block_mat_str = "" + block_type.toString() + ":" + block_data;
    ClickListener.debugLog("Found hand material string: '" + block_mat_str + "'");
    
    ItemStack hand = event.getItem();
    Material hand_type = null;
    if(hand != null) hand_type = hand.getType();
    short hand_data = 0;
    if(hand != null) hand_data = hand.getDurability();
    String hand_mat_str = "";
    if(hand_type != null) hand_mat_str += hand_type.toString() + ":" + hand_data;
    ClickListener.debugLog("Found hand material string: '" + hand_mat_str + "'");
    
    MenuManager manager = MenuManager.getManager();
    Menu open_menu = null;
    if(player.isSneaking())
    {
      open_menu = manager.getShiftRightClickBlock(block_mat_str);
      if(open_menu == null) open_menu = manager.getShiftRightClick(hand_mat_str);
    }
    else
    {
      open_menu = manager.getRightClickBlock(block_mat_str);
      if(open_menu == null) open_menu = manager.getRightClick(hand_mat_str);
    }
    if(open_menu == null) return;
    ClickListener.debugLog("Found menu to be opened '" + open_menu.getName() + "'");
    event.setCancelled(true);
    open_menu.getInventoryMenu(player).openMenu();
    
  }
}

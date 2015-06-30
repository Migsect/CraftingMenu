package net.samongi.CraftingMenu.Menu;

import java.util.HashMap;
import java.util.Map;

import net.samongi.CraftingMenu.CraftingMenu;

public class MenuManager
{
  static private MenuManager manager;
  public static MenuManager getManager(){return MenuManager.manager;}
  
  static private void log(String message){CraftingMenu.log("[MenuManager] " + message);}
  static private void logDebug(String message){CraftingMenu.debugLog("[MenuManager] " + message);}
  
  private final Map<String, Menu> menus = new HashMap<>();
  
  // These menus happen when the associated string materials are clicked or used.
  //   Left clicking is reserved for obvious reasons.
  private final Map<String, Menu> right_click_menus = new HashMap<>();
  private final Map<String, Menu> shift_right_click_menus = new HashMap<>();
  private final Map<String, Menu> right_click_block_menus = new HashMap<>();
  private final Map<String, Menu> shift_right_click_block_menus = new HashMap<>();
  
  
  
  public Menu getMenu(String name){return menus.get(name);}
  public boolean containsMenu(String name){return menus.containsKey(name);}
  
  public boolean containsRightClick(String material){return this.right_click_menus.containsKey(material);}
  public Menu getRightClick(String material){return this.right_click_menus.get(material);}
  public boolean containsShiftRightClick(String material){return this.shift_right_click_menus.containsKey(material);}
  public Menu getShiftRightClick(String material){return this.shift_right_click_menus.get(material);}
  public boolean containsRightClickBlock(String material){return this.right_click_block_menus.containsKey(material);}
  public Menu getRightClickBlock(String material){return this.right_click_block_menus.get(material);}
  public boolean containsShiftRightClickBlock(String material){return this.shift_right_click_block_menus.containsKey(material);}
  public Menu getShiftRightClickBlock(String material){return this.shift_right_click_block_menus.get(material);}
  
  public boolean addMenu(Menu menu)
  {
    if(!this.menus.containsKey(menu.getName()))
    {
      MenuManager.log("Error registering new menu:");
      MenuManager.log("  Menu with name '" + menu.getName() + "' already exists.");
      MenuManager.log("  This means there is a menu using the same name as this one.");
      return false;
    }
    MenuManager.logDebug("Registered menu with name '" + menu.getName() + "'");
    this.menus.put(menu.getName(), menu);
    return true;
  }
  
  public boolean addRightClickAllMenu(String material, Menu menu)
  {
    if(!this.right_click_menus.containsKey(material) || !this.shift_right_click_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK_ALL menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is either a menu registered in Right Click,");
      MenuManager.log("  Shift Right Click, or Both");
      return false;
    }
    MenuManager.logDebug("Registered menu for RIGHT_CLICK_ALL with name '" + menu.getName() + "'");
    this.right_click_menus.put(material, menu);
    this.shift_right_click_menus.put(material, menu);
    return true;
  }
  public boolean addRightClickMenu(String material, Menu menu)
  {
    if(!this.right_click_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Right Click.");
      return false;
    }
    MenuManager.logDebug("Registered menu for RIGHT_CLICK with name '" + menu.getName() + "'");
    this.right_click_menus.put(material, menu);
    return true;
  }
  public boolean addShiftRightClickMenu(String material, Menu menu)
  {
    if(!this.shift_right_click_menus.containsKey(material))
    {
      MenuManager.log("Error registering new SHIFT_RIGHT_CLICK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Shift Right Click.");
      return false;
    }
    MenuManager.logDebug("Registered menu for SHIFT_RIGHT_CLICK with name '" + menu.getName() + "'");
    this.shift_right_click_menus.put(material, menu);
    return true;
  }
  public boolean addRightClickBlockAllMenu(String material, Menu menu)
  {
    if(!this.right_click_block_menus.containsKey(material) || !this.shift_right_click_block_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK_BLOCK_ALL menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is either a menu registered in Right Click Block,");
      MenuManager.log("  Shift Right Click Block, or Both");
      return false;
    }
    MenuManager.logDebug("Registered menu for RIGHT_CLICK_BLOCK_ALL with name '" + menu.getName() + "'");
    this.right_click_block_menus.put(material, menu);
    this.shift_right_click_block_menus.put(material, menu);
    return true;
  }
  public boolean addRightClickBlockMenu(String material, Menu menu)
  {
    if(!this.right_click_block_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK_BLOCK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Right Click Block.");
      return false;
    }
    MenuManager.logDebug("Registered menu for RIGHT_CLICK_BLOCK with name '" + menu.getName() + "'");
    this.right_click_block_menus.put(material, menu);
    return true;
  }
  public boolean addShiftRightClickBlockMenu(String material, Menu menu)
  {
    if(!this.shift_right_click_block_menus.containsKey(material))
    {
      MenuManager.log("Error registering new SHIFT_RIGHT_CLICK_BLOCK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Shift Right Click Block.");
      return false;
    }
    MenuManager.logDebug("Registered menu for SHIFT_RIGHT_CLICK_BLOCK with name '" + menu.getName() + "'");
    this.shift_right_click_block_menus.put(material, menu);
    return true;
  }
}

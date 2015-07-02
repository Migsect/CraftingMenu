package net.samongi.CraftingMenu.Menu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.SamongiLib.Configuration.ConfigFile;

public class MenuManager
{
  static private MenuManager manager;
  public static MenuManager getManager(){return MenuManager.manager;}
  
  static private void log(String message){CraftingMenu.log("[MenuManager] " + message);}
  static private void debugLog(String message){CraftingMenu.debugLog("[MenuManager] " + message);}
  
  private final Map<String, Menu> menus = new HashMap<>();
  
  // These menus happen when the associated string materials are clicked or used.
  //   Left clicking is reserved for obvious reasons.
  private final Map<String, Menu> right_click_menus = new HashMap<>();
  private final Map<String, Menu> shift_right_click_menus = new HashMap<>();
  private final Map<String, Menu> right_click_block_menus = new HashMap<>();
  private final Map<String, Menu> shift_right_click_block_menus = new HashMap<>();
  
  private final File menu_folder;
  
  public MenuManager(File menu_folder)
  {
    this.menu_folder = menu_folder;
    this.folderParseMenus(this.menu_folder);
  }
  
  private void folderParseMenus(File file){this.folderParseMenus(file, true);}
  private void folderParseMenus(File file, boolean recursive)
  {
    MenuManager.debugLog("Parsing all files in directory: " + file.getAbsolutePath());
    File[] files = file.listFiles();
    for(File f : files)
    {
      if(f.isDirectory() && recursive) this.folderParseMenus(f);
      else if(f.isFile() && (f.getAbsolutePath().endsWith(".yml") || f.getAbsolutePath().endsWith(".yaml"))) this.parseMenus(f);
      else MenuManager.debugLog("  Passed over file: '" + f.getAbsolutePath() + "'");
    }
  }
  private void parseMenus(File file)
  {
    MenuManager.debugLog("  Parsing file: " + file.getAbsolutePath());
    ConfigFile config = new ConfigFile(file);
    if(!config.getConfig().getKeys(false).contains("menus")) 
    {
      MenuManager.debugLog("  File did not contain any menus, returning.");
      return;
    }
    ConfigurationSection menus = config.getConfig().getConfigurationSection("menus");
    Set<String> menu_keys = menus.getKeys(false);
    for(String k : menu_keys)
    {
      ConfigurationSection m = menus.getConfigurationSection(k);
      Menu menu = new Menu(this, m);
      this.addMenu(menu);
    }
  }
  
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
    if(this.menus.containsKey(menu.getName()))
    {
      MenuManager.log("Error registering new menu:");
      MenuManager.log("  Menu with name '" + menu.getName() + "' already exists.");
      MenuManager.log("  This means there is a menu using the same name as this one.");
      return false;
    }
    MenuManager.debugLog("Registered menu with name '" + menu.getName() + "'");
    this.menus.put(menu.getName(), menu);
    
    if(menu.hasClickMaterial() && menu.hasClickMaterialType())
    {
      String click_type = menu.getClickMaterialType();
      if(click_type.toUpperCase().equals("RIGHT_CLICK_ALL")) this.addRightClickAllMenu(menu.getClickMaterial(), menu);
      else if(click_type.toUpperCase().equals("RIGHT_CLICK")) this.addRightClickMenu(menu.getClickMaterial(), menu);
      else if(click_type.toUpperCase().equals("SHIFT_RIGHT_CLICK_ALL")) this.addShiftRightClickMenu(menu.getClickMaterial(), menu);
    }
    if(menu.hasBlockMaterial() && menu.hasBlockMaterialType())
    {
      String click_type = menu.getClickBlockType();
      if(click_type.toUpperCase().equals("RIGHT_CLICK_ALL")) this.addRightClickBlockAllMenu(menu.getClickBlock(), menu);
      else if(click_type.toUpperCase().equals("RIGHT_CLICK")) this.addRightClickBlockMenu(menu.getClickBlock(), menu);
      else if(click_type.toUpperCase().equals("SHIFT_RIGHT_CLICK_ALL")) this.addShiftRightClickBlockMenu(menu.getClickBlock(), menu);
    }
    
    return true;
  }
  
  public boolean addClickAllMenu(String material, Menu menu)
  {
    if(!this.addRightClickAllMenu(material, menu)) return false;
    if(!this.addRightClickBlockAllMenu(material, menu)) return false;
    return true;
  }
  public boolean addRightClickAllMenu(String material, Menu menu)
  {
    if(!this.addRightClickMenu(material, menu)) return false;
    if(!this.addShiftRightClickMenu(material, menu)) return false;
    return true;
  }
  public boolean addRightClickMenu(String material, Menu menu)
  {
    if(this.right_click_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Right Click.");
      return false;
    }
    MenuManager.debugLog("Registered menu for RIGHT_CLICK with name '" + menu.getName() + "'");
    this.right_click_menus.put(material, menu);
    return true;
  }
  public boolean addShiftRightClickMenu(String material, Menu menu)
  {
    if(this.shift_right_click_menus.containsKey(material))
    {
      MenuManager.log("Error registering new SHIFT_RIGHT_CLICK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Shift Right Click.");
      return false;
    }
    MenuManager.debugLog("Registered menu for SHIFT_RIGHT_CLICK with name '" + menu.getName() + "'");
    this.shift_right_click_menus.put(material, menu);
    return true;
  }
  public boolean addRightClickBlockAllMenu(String material, Menu menu)
  {
    if(!this.addRightClickBlockMenu(material, menu)) return false;
    if(!this.addShiftRightClickBlockMenu(material, menu)) return false;
    return true;
  }
  public boolean addRightClickBlockMenu(String material, Menu menu)
  {
    if(this.right_click_block_menus.containsKey(material))
    {
      MenuManager.log("Error registering new RIGHT_CLICK_BLOCK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Right Click Block.");
      return false;
    }
    MenuManager.debugLog("Registered menu for RIGHT_CLICK_BLOCK with name '" + menu.getName() + "'");
    this.right_click_block_menus.put(material, menu);
    return true;
  }
  public boolean addShiftRightClickBlockMenu(String material, Menu menu)
  {
    if(this.shift_right_click_block_menus.containsKey(material))
    {
      MenuManager.log("Error registering new SHIFT_RIGHT_CLICK_BLOCK menu:");
      MenuManager.log("  Material with string '" + material + "' is already taken");
      MenuManager.log("  This means there is a menu registered in Shift Right Click Block.");
      return false;
    }
    MenuManager.debugLog("Registered menu for SHIFT_RIGHT_CLICK_BLOCK with name '" + menu.getName() + "'");
    this.shift_right_click_block_menus.put(material, menu);
    return true;
  }
}
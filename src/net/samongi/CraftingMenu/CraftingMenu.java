package net.samongi.CraftingMenu;

import java.io.File;
import java.util.logging.Logger;

import net.samongi.CraftingMenu.Menu.MenuManager;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.Configuration.ConfigFile;

import org.bukkit.plugin.java.JavaPlugin;

public class CraftingMenu extends JavaPlugin
{
  static private Logger logger;
  static private boolean debug;
  
  static final public void log(String to_log){logger.info(to_log);}
  static final public void debugLog(String to_log){if(debug == true) logger.info(to_log);}
  static final public boolean debug(){return debug;}
  
  @SuppressWarnings("unused")
  private PlayerManager player_manager;
  @SuppressWarnings("unused")
  private RecipeManager recipe_manager;
  @SuppressWarnings("unused")
  private MenuManager menu_manager;
  
  public void onEnable()
  {
    logger = this.getLogger();
    
    // config handling.
    File config_file = new File(this.getDataFolder(),"config.yml");
    if(!config_file.exists())
    {
      CraftingMenu.log("Found no config file, copying over defaults...");
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
    }
    debug = this.getConfig().getBoolean("debug", true);
    CraftingMenu.log("Debug set to: " + debug);
    
    // Copying resources
    ConfigFile example_menus = new ConfigFile(this, "example_menus.yml");
    example_menus.getConfig().options().copyDefaults(true);
    example_menus.saveConfig();
    ConfigFile example_recipes = new ConfigFile(this, "example_recipes.yml");
    example_recipes.getConfig().options().copyDefaults(true);
    example_recipes.saveConfig();
    
    // Setting up the player manager.
    File player_files = new File(this.getDataFolder(), "players");
    if(!player_files.exists() || !player_files.isDirectory()) player_files.mkdirs(); // TODO error checking if the directory was not made.
    this.player_manager = new PlayerManager(player_files);
    
    // Setting up the recipe manager.
    File recipe_files = new File(this.getDataFolder(), "recipes");
    if(!recipe_files.exists() || !recipe_files.isDirectory()) recipe_files.mkdirs();
    this.recipe_manager = new RecipeManager(recipe_files);
    
    // Setting up the recipe manager.
    File menu_files = new File(this.getDataFolder(), "menus");
    if(!menu_files.exists() || !menu_files.isDirectory()) menu_files.mkdirs();
    this.menu_manager = new MenuManager(menu_files);
  }
}
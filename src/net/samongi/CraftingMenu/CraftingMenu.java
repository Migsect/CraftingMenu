package net.samongi.CraftingMenu;

import java.io.File;
import java.util.logging.Logger;

import net.samongi.CraftingMenu.Commands.CommandHelp;
import net.samongi.CraftingMenu.Commands.CommandLearn;
import net.samongi.CraftingMenu.Commands.CommandLearned;
import net.samongi.CraftingMenu.Commands.CommandMenu;
import net.samongi.CraftingMenu.Commands.CommandRecipes;
import net.samongi.CraftingMenu.Commands.CommandUnlearn;
import net.samongi.CraftingMenu.Listeners.PlayerListener;
import net.samongi.CraftingMenu.Menu.MenuManager;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.CommandHandling.CommandHandler;
import net.samongi.SamongiLib.Configuration.ConfigFile;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftingMenu extends JavaPlugin
{
  static private Logger logger;
  static private boolean debug;
  static private CraftingMenu plugin = null;
  
  static public CraftingMenu getMainClass(){return CraftingMenu.plugin;}
  
  static final public void log(String to_log){logger.info(to_log);}
  static final public void debugLog(String to_log){if(debug == true) logger.info(to_log);}
  static final public boolean debug(){return debug;}
  
  private PlayerManager player_manager;
  @SuppressWarnings("unused")
  private RecipeManager recipe_manager;
  @SuppressWarnings("unused")
  private MenuManager menu_manager;
  
  private CommandHandler command_handler;
  
  public void onEnable()
  {
  	CraftingMenu.plugin = this;
  	CraftingMenu.logger = this.getLogger();
    
    // config handling.
    File config_file = new File(this.getDataFolder(),"config.yml");
    if(!config_file.exists())
    {
      CraftingMenu.log("Found no config file, copying over defaults...");
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
    }
    CraftingMenu.debug = this.getConfig().getBoolean("debug", true);
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
    
    // Setting up the menu manager.
    File menu_files = new File(this.getDataFolder(), "menus");
    if(!menu_files.exists() || !menu_files.isDirectory()) menu_files.mkdirs();
    this.menu_manager = new MenuManager(menu_files);
    
    // Setting up the command handler
    this.command_handler = new CommandHandler(this);
    this.command_handler.registerCommand(new CommandMenu("craftmenu menu"));
    this.command_handler.registerCommand(new CommandHelp("craftmenu", this.command_handler));
    this.command_handler.registerCommand(new CommandLearned("craftmenu learned"));
    this.command_handler.registerCommand(new CommandRecipes("craftmenu recipes"));
    this.command_handler.registerCommand(new CommandLearn("craftmenu learn"));
    this.command_handler.registerCommand(new CommandUnlearn("craftmenu unlearn"));
    
    // Listeners
    PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new PlayerListener(), this);
    
    // Reloading all player profiles.
    for(Player p : this.getServer().getOnlinePlayers()) this.player_manager.loadProfile(p.getUniqueId());
    
  }
  
  public void onDisable()
  {
  	for(Player p : this.getServer().getOnlinePlayers()) this.player_manager.unloadProfile(p.getUniqueId());
  }
}

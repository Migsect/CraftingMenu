package net.samongi.CraftingMenu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.Items.ItemUtil;
import net.samongi.SamongiLib.Menu.InventoryMenu;
import net.samongi.SamongiLib.Menu.ButtomAction.ButtonAction;
import net.samongi.SamongiLib.Menu.ButtomAction.ButtonOpenMenu;

/** Menus consist of a list of recipes to display to a player
 *  However the menu also handls the hiding of recipes.
 *  Menus will generate the inventory gui neccessary for the menu and then
 *  display it to the player.
 *  
 *  Menus can have multiple pages to list recipes as well as have 
 *  submenus specified within them.
 */
public class Menu
{
  static private void log(String message){CraftingMenu.log("[Menu] " + message);}
  static private void debugLog(String message){CraftingMenu.debugLog("[Menu] " + message);}
  
  private final MenuManager manager;
  private final String menu_name;
  private final Map<Integer, String> sub_menus = new HashMap<>();
  private final Set<String> recipes = new HashSet<>();
  
  private final Material display_material;
  
  // Filled with a material string to a set of integers
  private final Map<String, Set<Integer>> filled_slots = new HashMap<>();
  
  private final String click_material;
  private final String click_material_type;
  
  private final String block_material;
  private final String block_material_type;
  
  private final int rows = 6;
  
  public Menu(MenuManager manager, ConfigurationSection section)
  {
    this.manager = manager;
    
    Menu.debugLog("Parsing Menu with path: " + section.getCurrentPath());
    // Getting the menu name
    this.menu_name = section.getString("name");
    if(menu_name == null)
    {
      Menu.log("Menu Name was not set for: " + section.getCurrentPath());
      Menu.log("  This menu will not be registered until it is given a name.");
    }
    Menu.debugLog("Found menu name to be: " + this.menu_name);
    
    // Getting all display material
    this.display_material = Material.getMaterial(section.getString("display", "GRASS"));
    
    // Getting all the recipes in this menu
    List<String> recipes = section.getStringList("recipes");
    if(recipes != null) this.recipes.addAll(recipes);
    Menu.debugLog("Found recipes to be: ");
    if(CraftingMenu.debug()) for(String s : recipes) Menu.debugLog(" - " + s);
    
    // Getting all the submenus
    Menu.debugLog("Found sub-menus to be: ");
    ConfigurationSection sub_menu_section = section.getConfigurationSection("sub-menus");
    if(sub_menu_section != null)
    {
      // Now we get all the keys in this section.
      Set<String> keys = sub_menu_section.getKeys(false);
      for(String k : keys)
      {
        int i = -1;
        try{Integer.parseInt(k);}catch(NumberFormatException e){;}
        if(i < 0) continue;
        Menu.debugLog(" - " + k + i);
        sub_menus.put(i, sub_menu_section.getString(k));
      }
    }

    Menu.debugLog("Found filled slots to be: ");
    ConfigurationSection filled_slots = section.getConfigurationSection("filled-slots");
    if(filled_slots != null)
    {
      Set<String> keys = filled_slots.getKeys(false);
      for(String k : keys)
      {
        List<Integer> slots = filled_slots.getIntegerList(k);
        if(slots == null) continue;
        Set<Integer> slots_set = new HashSet<>(slots);
        if(CraftingMenu.debug())
        {
          String slot_str = "[ ";
          for(int i : slots_set) slot_str += i + " ";
          slot_str += "]";
          Menu.debugLog(" - " + k + " : " + slot_str);
        }
        this.filled_slots.put(k, slots_set);
      }
    }
    
    // Getting where the item can be used.
    this.click_material = section.getString("click-material", null);
    this.click_material_type = section.getString("click-material-type", null);
    this.block_material = section.getString("block-material", null);
    this.block_material_type = section.getString("block-material-type", null);

    Menu.debugLog("Found Click Material to be: " + this.click_material);
    Menu.debugLog("Found Click Material Type to be: " + this.click_material_type);
    Menu.debugLog("Found Block Material to be: " + this.block_material);
    Menu.debugLog("Found Block Material Type to be: " + this.block_material_type);
    
  }
  
  public String getName(){return this.menu_name;}
  public String getSimpleName(){return this.menu_name.replace(" ", "").toLowerCase();}
  public Material getDisplayMaterial(){return this.display_material;}
  
  public boolean hasClickMaterial(){return this.click_material != null;}
  public boolean hasClickMaterialType(){return this.click_material_type != null;}
  public String getClickMaterial(){return this.click_material;}
  public String getClickMaterialType(){return this.click_material_type;}
  
  public boolean hasBlockMaterial(){return this.block_material != null;}
  public boolean hasBlockMaterialType(){return this.block_material_type != null;}
  public String getClickBlock(){return this.block_material;}
  public String getClickBlockType(){return this.block_material_type;}
  
  /**Will generate an inventory menu based off this menu.
   * 
   * @param player The player to base the menu for.
   * @return The inventory menu that is a crafting menu.
   */
  public InventoryMenu getInventoryMenu(Player player)
  {
    Menu.debugLog("Generating Inventory Menu for '"+ this.getName() +"' for '" + player.getName() + "'");
    InventoryMenu menu = new InventoryMenu(player, this.rows, this.getName());

    // Grabbing all the menus that are shown to the player
    Set<Menu> menus = this.getShownMenus(player.getUniqueId());
    Menu.debugLog("Found " + menus.size() + " shown menus.");
    // Gets all the slots that will be filled
    Set<Integer> filled_slots = this.getAllFilledSlots();
    Menu.debugLog("Found " + filled_slots.size() + " filled slots.");
    // Gets all the slots that there will be a submenu in.
    Set<Integer> menu_slots = this.sub_menus.keySet();
    // Grabbning all the recipes that are shown to the player in this menu.
    List<Recipe> recipes = this.getShownRecipes(player.getUniqueId());
    Menu.debugLog("Found " + recipes.size() + " shown recipes.");
    
    // creating all the sub menus.
    Menu.debugLog("Creating Menu Buttons");
    for(Menu m : menus)
    {
      String display_name = m.getName();
      int slot = -1;
      for(Integer i : this.sub_menus.keySet()) if(this.sub_menus.get(i).equals(display_name)) slot = i;
      if(slot < 0) continue;
      Material display = m.getDisplayMaterial();
      // Making the button that will open the menu.
      ButtonAction button = new ButtonAction()
      {
        @Override
        public void onButtonPress()
        {
          InventoryMenu current_m = m.getInventoryMenu(player);
          int size = current_m.getInventory().getSize();

          ItemStack display_return = new ItemStack(Material.BOOK);
          ItemMeta im = display_return.getItemMeta();
          im.setDisplayName(ChatColor.AQUA + "Return to Crafting");
          display_return.setItemMeta(im);
          ButtonOpenMenu menu_button = new ButtonOpenMenu(menu, display_return, CraftingMenu.getMainClass());
          menu_button.register(current_m, size - 1); // putting it in the bottom right of the menu
          
        }
      };
      
      ItemStack button_item = new ItemStack(display);
      ItemMeta im = button_item.getItemMeta();
      im.setDisplayName(ChatColor.GREEN + display_name);
      button_item.setItemMeta(im);
      
      menu.setItem(slot, button_item);
      menu.addLeftClickAction(slot, button);
    }
    // creating all the filled slots.
    Menu.debugLog("Creating Filled Slots");
    for(String s : this.filled_slots.keySet())
    {
      ItemStack fill_item = ItemUtil.getItemStack(s);
      ItemMeta im = fill_item.getItemMeta();
      im.setDisplayName("");
      fill_item.setItemMeta(im);
      Set<Integer> slots = this.filled_slots.get(s);
      for(int i : slots)
      {
        menu.setItem(i, fill_item.clone());
      }
    }
    
    // filling all the slots with the recipes.
    int index = 0;
    Menu.debugLog("Creating Recipe Buttons");
    for(Recipe r : recipes)
    {
      Menu.debugLog("Generating Menu Button for Recipe '" + r.getName() + "'");
      // finding a slot to place it in.
      while(filled_slots.contains(index) || menu_slots.contains(index)) index ++;
      Menu.debugLog("  Setting item in index " + index);
      // if we surpass the number of item slots in the menu.
      if(index >= (this.rows * 9)) break;
      // The recipe handles whether or not to display the recipes
      menu.setItem(index, r.getMenuItem(player));
      
      // clicking the recipe will craft it if the player has the recipe, 
      // else it will learn the recipe when the player clicks it.
      Menu.debugLog("  Does player know the recipe: " + r.hasRecipe(player));
      if(r.hasRecipe(player))
      {
        // Setting the current menu (this) to a variable so we can pass it to the button.
        Menu this_menu = this;
        // creating the button for crafting the recipe.
        ButtonAction craft_button = new ButtonAction()
        {
          Recipe recipe = r;
          Player p = player;
          Menu m = this_menu;
          @Override
          public void onButtonPress()
          {
            boolean ret = recipe.craftRecipe(p);
            if(ret) recipe.playCraftSound(p);
            else recipe.playFailSound(p);
            // Refreshing the inventory
            // task to refresh the inventory.
            BukkitRunnable task = new BukkitRunnable()
            {
              @Override
              public void run()
              {
                m.getInventoryMenu(p).openMenu();;
              }
              
            };
            task.runTaskLater(CraftingMenu.getMainClass(), 1);
          }
        };
        menu.addLeftClickAction(index, craft_button);
      }
      else // Learn the recipe
      {
        // creating the button for crafting the recipe.
        Menu this_menu = this;
        ButtonAction learn_button = new ButtonAction()
        {
          Recipe recipe = r;
          Player p = player;
          Menu m = this_menu;
          @Override
          public void onButtonPress()
          {
            boolean ret = recipe.learnRecipe(p);
            if(ret) recipe.playLearnSound(p);
            else recipe.playFailSound(p);
            // Refreshing the inventory
            // task to refresh the inventory.
            BukkitRunnable task = new BukkitRunnable()
            {
              @Override
              public void run()
              {
                m.getInventoryMenu(p).openMenu();
              }
              
            };
            task.runTaskLater(CraftingMenu.getMainClass(), 1);
          }
        };
        menu.addLeftClickAction(index, learn_button);
      }
      // On right click this will open up the components listing.
      Menu this_menu = this;
      ButtonAction components_button = new ButtonAction(){
        Recipe recipe = r;
        Menu m = this_menu;
        @Override
        public void onButtonPress()
        {
          recipe.getComponentDetails(player, m).openMenu();;
        }
        
      };
      // On shift right click this will open up the results listing
      ButtonAction results_button = new ButtonAction(){
        Recipe recipe = r;
        Menu m = this_menu;
        @Override
        public void onButtonPress()
        {
          recipe.getResultDetails(player, m).openMenu();;
        }
      };
      // Setting all the click states.
      menu.addRightClickAction(index, components_button, false);
      menu.addRightClickAction(index, results_button, true);
      index++;
    }
    
    return menu;
  }
  
  /**Returns all the recipes that would be "shown" to the player.
   * 
   * @param player The player that is being checked for.
   * @return A set of all the recipes from this menu that can be shown to the player.
   */
  public List<Recipe> getShownRecipes(UUID player)
  {
    List<Recipe> seen_recipes = new ArrayList<>();
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return seen_recipes;
    Set<String> sorted_recipes = new TreeSet<>();
    sorted_recipes.addAll(recipes);
    for(String s : sorted_recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
      // If the player has the recipe, or the recipe is not hidden, then add it.
      if(profile.hasRecipe(r) || !r.isHidden()) seen_recipes.add(r);
    }
    return seen_recipes;
  }
  
  /**Returns the number of recipes that will be shown to the
   *   specified player. Even if the recipe is not known, some recipes will be shown as they are not hidden.
   * @param player The UUID of the player to check with.
   * @return The number of recipes the player will be shown in this menu.  -1 if the UUID has no profile.
   */
  public int getShownRecipesNumber(UUID player)
  {
    int count = 0;
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return -1;
    for(String s : recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
      if(profile.hasRecipe(r) || !r.isHidden()) count++;
    }
    return count;
  }
  
  public Set<Menu> getShownMenus(UUID player)
  {
    Set<Menu> seen_menus = new HashSet<>();
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return seen_menus;
    for(String s : this.sub_menus.values())
    {
      Menu m = this.manager.getMenu(s);
      if(m == null) continue;
      if(m.getShownRecipesNumber(player) + m.getShownMenusNumber(player) > 0) seen_menus.add(m);
    }
    return seen_menus;
  }
  /**This will go through all submenus to see if they have recipes to show to the player
   *   The number of menus shown to the player will always be 1 as it includes the current menu.
   * @param player The player to check against
   * @return The number of all sub-menus that player will be able to reach.
   */
  public int getShownMenusNumber(UUID player)
  {
    int count = 0;
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return -1;
    for(String s : sub_menus.values())
    {
      Menu m = this.manager.getMenu(s);
      if(m == null) continue;
      count += m.getShownMenusNumber(player); 
    }
    if(this.getShownMenusNumber(player) > 0) count++;
    return count;
  }
  
  public Set<Integer> getAllFilledSlots()
  {
    Set<Integer> slots = new HashSet<>();
    for(String k : this.filled_slots.keySet())
    {
      slots.addAll(this.filled_slots.get(k));
    }
    return slots;
  }
}

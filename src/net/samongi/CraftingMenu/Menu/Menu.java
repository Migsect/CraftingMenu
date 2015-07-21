package net.samongi.CraftingMenu.Menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
  
  // Filled with a material.
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
        sub_menus.put(i, sub_menu_section.getString(k));
      }
    }
    
    ConfigurationSection filled_slots = section.getConfigurationSection("filled-slots");
    if(filled_slots != null)
    {
      Set<String> keys = filled_slots.getKeys(false);
      for(String k : keys)
      {
        List<Integer> slots = filled_slots.getIntegerList(k);
        if(slots == null) continue;
        Set<Integer> slots_set = new HashSet<>(slots);
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
  public boolean hasBlockMaterialType(){return this.click_material_type != null;}
  public String getClickBlock(){return this.block_material;}
  public String getClickBlockType(){return this.block_material_type;}
  
  public InventoryMenu getInventoryMenu(Player player)
  {
    InventoryMenu menu = new InventoryMenu(player, this.rows, this.getName());

    Set<Menu> menus = this.getShownMenus(player.getUniqueId());
    Set<Integer> filled_slots = this.getAllFilledSlots();
    Set<Integer> menu_slots = this.sub_menus.keySet();
    Set<Recipe> recipes = this.getShownRecipes(player.getUniqueId());
    
    // creating all the sub menus.
    for(Menu m : menus)
    {
      String display_name = m.getName();
      int slot = -1;
      for(Integer i : this.sub_menus.keySet()) if(this.sub_menus.get(i).equals(display_name)) slot = i;
      if(slot < 0) continue;
      Material display = m.getDisplayMaterial();
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
          ButtonOpenMenu menu_button = new ButtonOpenMenu(menu, display_return);
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
    for(Recipe r : recipes)
    {
      // finding a slot to place it in.
      while(filled_slots.contains(index) || menu_slots.contains(index)) index ++;
      if(index >= this.rows * 9) break;
      menu.setItem(index, r.getMenuItem(player));
      
      // creating the button for crafting the recipe.
      ButtonAction craft_button = new ButtonAction(){
        Recipe recipe = r;
        Player p = player;
        @Override
        public void onButtonPress()
        {
          recipe.craftRecipe(p);
        }
      };
      // On right click this will open up the components listing.
      ButtonAction components_button = new ButtonAction(){
        Recipe recipe = r;
        InventoryMenu m = menu;
        @Override
        public void onButtonPress()
        {
          recipe.getComponentDetails(m).openMenu();;
        }
        
      };
      // On shift right click this will open up the results listing
      ButtonAction results_button = new ButtonAction(){
        Recipe recipe = r;
        InventoryMenu m = menu;
        @Override
        public void onButtonPress()
        {
          recipe.getResultDetails(m).openMenu();;
        }
      };
      // Setting all the click states.
      menu.addLeftClickAction(index, craft_button);
      menu.addRightClickAction(index, components_button, false);
      menu.addRightClickAction(index, results_button, true);
    }
    
    return menu;
  }
  
  
  public Set<Recipe> getShownRecipes(UUID player)
  {
    Set<Recipe> seen_recipes = new HashSet<>();
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return seen_recipes;
    for(String s : recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
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

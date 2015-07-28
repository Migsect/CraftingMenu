package net.samongi.CraftingMenu.Recipe.Component;

import java.util.HashMap;
import java.util.Map;

import net.samongi.CraftingMenu.Recipe.Component.Type.ComponentType;

public class ComponentManager
{
  static private ComponentManager manager;
  public static ComponentManager getManager(){return ComponentManager.manager;}

  private Map<String, ComponentType> type = new HashMap<>();
  
  public ComponentManager()
  {
  	ComponentManager.manager = this;
  }
  
  /**Registers the type with the manager. The key is defined by the types name and as a
   * result, when desiging types, their name should be different from other types.
   * 
   * @param type
   */
  public void registerType(ComponentType type){this.type.put(type.getName().toLowerCase(), type);}
  
  /**Gets the component based on the string, not based on the key the component type has
   * This is how components are constructed in which they are dynamically found as opposed
   * to having hard types.
   * 
   * @param str
   */
  public void getComponent(String str)
  {
  }
}

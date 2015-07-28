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
  
  public void registerType(ComponentType type){this.type.put(type.getName(), type);}
  /**Gets the component based on the string, not based on the key the component type has
   * This is how components are constructed.
   * 
   * @param str
   */
  public void getComponent(String str)
  {
  }
}

package net.samongi.CraftingMenu.Recipe.Result;

import java.util.HashMap;
import java.util.Map;

import net.samongi.CraftingMenu.Recipe.Result.Type.ResultType;

public class ResultManager
{
	static private ResultManager manager;
  public static ResultManager getManager(){return ResultManager.manager;}
  
  private Map<String, ResultType> type = new HashMap<>();
  
  public ResultManager()
  {
  	ResultManager.manager = this;
  }
  
  /**Registers the type with the manager. The key is defined by the types name and as a
   * result, when desiging types, their name should be different from other types.
   * 
   * @param type
   */
  public void registerType(ResultType type){this.type.put(type.getName(), type);}
  
  public Result getResult(String str)
  {
  	for(ResultType t : type.values())
  	{
  		Result r = t.getResult(str);
  		if(r != null) return r;
  	}
  	return null;
  }
}

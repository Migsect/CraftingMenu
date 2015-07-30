package net.samongi.CraftingMenu.Recipe.Result.Type;

import net.samongi.CraftingMenu.Recipe.Result.RandomResult;
import net.samongi.CraftingMenu.Recipe.Result.Result;
import net.samongi.CraftingMenu.Recipe.Result.ResultManager;

public class RandomResultType implements ResultType
{
  private final String name = "RANDOM";
	
	@Override
	public Result getResult(String str)
	{
		RandomResult result = new RandomResult();
		String[] split_str = str.split("|");
		if(split_str.length < 2) return null;
		for(String s : split_str)
		{
			String[] split_s = s.split("%");
			if(split_s.length != 2) return null;
			String weight_str = split_s[0];
			String result_str = split_s[1];
			int weight = 0;
			try{weight = Integer.parseInt(weight_str);}catch(NumberFormatException e){return null;}
			Result r = ResultManager.getManager().getResult(result_str);
			if(r == null) return null;
			result.insertResult(result, weight);
		}
		return result;
	}

	@Override
	public String getName(){return this.name;}
  
}

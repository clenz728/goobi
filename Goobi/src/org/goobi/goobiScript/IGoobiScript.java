package org.goobi.goobiScript;

import java.util.HashMap;
import java.util.List;

public interface IGoobiScript {
	
	public abstract void prepare(List<Integer> processes, String command, HashMap<String, String> parameters);
    
	public abstract void execute();

}
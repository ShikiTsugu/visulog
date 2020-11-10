package up.visulog.config;
import java.util.Map;
import java.util.HashMap;

public class PluginConfig{
	private Map<String, String> plugins=new HashMap<String, String>();
	
	public PluginConfig(Map<String, String> p) {
		plugins = p;
	}
	
	public Map<String, String> getMap(){
		return plugins;
	}
	
	public String[] getKeys() {
		String[]keys=new String[plugins.size()];
		for (String s: plugins.keySet()) {
			for (int i=0; i<keys.length; i++) {
				keys[i]=s;
			}
		}
		return keys;
	}
}

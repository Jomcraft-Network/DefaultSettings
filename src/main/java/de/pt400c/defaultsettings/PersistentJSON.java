package de.pt400c.defaultsettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class PersistentJSON {
	
	public static transient final long serialVersionUID = 72964L;
	public HashMap<String, String> check = new HashMap<String, String>();
	
	public void save(File persistentLocation) {
		try (FileWriter writer = new FileWriter(persistentLocation)) {
            FileUtil.gson.toJson(this, writer);
        } catch (IOException e) {
        	DefaultSettings.log.log(Level.SEVERE, "Exception at processing configs: ", e);
        }
	}
}
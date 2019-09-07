package de.pt400c.defaultsettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.Level;
import lombok.Data;
import lombok.experimental.Accessors;

@Data()
@Accessors(fluent = true)
public class MainJSON {
	
	public static transient final long serialVersionUID = 32371L;
	private String version;
	private String identifier;
	private String prevVersion;
	protected boolean initPopup = false;
	private boolean exportMode = false;
	public List<String> activeConfigs = new ArrayList<String>();
	public HashMap<String, String> overrides = new HashMap<String, String>();
	public HashMap<String, String> check = new HashMap<String, String>();
	public String created_for;
	private String initially_created;
	
	public void save(File persistentLocation) {
		try (FileWriter writer = new FileWriter(persistentLocation)) {
            FileUtil.gson.toJson(this, writer);
        } catch (IOException e) {
        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
        }
	}
}
package de.pt400c.defaultsettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.Level;

public class MainJSON {
	
	public static transient final long serialVersionUID = 32371L;
	private String version;
	private String identifier;
	private String prevVersion;
	protected boolean initPopup = false;
	private boolean exportMode = false;
	public List<String> activeConfigs = new ArrayList<String>();
	public HashMap<String, String> overrides = new HashMap<String, String>();
	
	@SuppressWarnings("unused")
	private String initially_created;
	
	public MainJSON setVersion(String version) {
		this.version = version;
		return this;
	}
	
	public void setExportMode(boolean exportMode) {
		this.exportMode = exportMode;
	}
	
	public MainJSON setCreated(String initially_created) {
		this.initially_created = initially_created;
		return this;
	}
	
	public MainJSON setPrevVersion(String prevVersion) {
		this.prevVersion = prevVersion;
		return this;
	}
	
	public MainJSON setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public boolean getExportMode() {
		return this.exportMode;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public String getPrevVersion() {
		return this.prevVersion;
	}
	
	public void save(File persistentLocation) {
		try (FileWriter writer = new FileWriter(persistentLocation)) {
            FileUtil.gson.toJson(this, writer);
        } catch (IOException e) {
        	DefaultSettings.log.log(Level.ERROR, "Exception at processing configs: ", e);
        }
	}
	
}
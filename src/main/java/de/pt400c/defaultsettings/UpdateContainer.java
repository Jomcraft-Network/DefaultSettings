package de.pt400c.defaultsettings;

import static net.minecraftforge.common.ForgeVersion.Status.AHEAD;
import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.PENDING;
import static net.minecraftforge.common.ForgeVersion.Status.UP_TO_DATE;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.Level;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ComparableVersion;
import net.minecraftforge.common.MinecraftForge;

public class UpdateContainer {

	private ExecutorService tpe = Executors.newFixedThreadPool(1);
	private Status status = Status.UNKNOWN;
	private String onlineVersion = null;

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setOnlineVersion(String version){
		this.onlineVersion = version;
	}
	
	public String getOnlineVersion(){
		return this.onlineVersion;
	}

	public Status getStatus() {
		return this.status;
	}

	public void update() {
		this.setStatus(Status.CHECKING);
		this.tpe.submit(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				ModContainer mc = FMLCommonHandler.instance().findContainerFor(DefaultSettings.getInstance());
				net.minecraftforge.common.ForgeVersion.Status status = PENDING;
				ComparableVersion target = null;
				try {
					InputStream con = openUrlStream(new URL("https://gist.githubusercontent.com/PT400C/be22046792a7859688f655f1a5f83975/raw/976f2796b2f145c75cba258fe40259b5ca5555ac/ds-updates.json"));
					String data = new String(ByteStreams.toByteArray(con), "UTF-8");
					con.close();
					Map<String, Object> json = new Gson().fromJson(data, Map.class);

					Map<String, String> promos = (Map<String, String>) json.get("promos");
					//String display_url = (String) json.get("homepage");

					String rec = promos.get(MinecraftForge.MC_VERSION + "-recommended");
					String lat = promos.get(MinecraftForge.MC_VERSION + "-latest");
					ComparableVersion current = new ComparableVersion(mc.getVersion());

					if (rec != null) {
						ComparableVersion recommended = new ComparableVersion(rec);
						int diff = recommended.compareTo(current);

						if (diff == 0)
							status = UP_TO_DATE;
						else if (diff < 0) {
							status = AHEAD;
							if (lat != null) {
								ComparableVersion latest = new ComparableVersion(lat);
								if (current.compareTo(latest) < 0) {
									status = OUTDATED;
									target = latest;
								}
							}
						} else {
							status = OUTDATED;
							target = recommended;
						}
					} else if (lat != null) {
						ComparableVersion latest = new ComparableVersion(lat);
						if (current.compareTo(latest) < 0) {
							status = BETA_OUTDATED;
							target = latest;
						} else
							status = BETA;
					} else
						status = BETA;
				} catch (IOException e) {
					DefaultSettings.log.log(Level.ERROR, "Error while checking for updates: ", e);
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.ERROR);
					return;
				}
				if(target != null)
					DefaultSettings.getUpdater().setOnlineVersion(target.toString());
				
				switch (status) {
				case BETA: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case BETA_OUTDATED: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.OUTDATED);
					break;
				case UP_TO_DATE: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case AHEAD: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.UP_TO_DATE);
					break;
				case OUTDATED: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.OUTDATED);
					break;
				case PENDING: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.CHECKING);
					break;
				case FAILED: 

				default: 
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.ERROR);
				
				}
			}
		});
		

	}

	public static InputStream openUrlStream(URL url) throws IOException {
		InputStream stream = null;
		URLConnection con = url.openConnection();
		con.setConnectTimeout(5000);
		if (con instanceof HttpURLConnection) {
			HttpURLConnection huc = (HttpURLConnection) con;
			huc.setInstanceFollowRedirects(false);
			int responseCode = huc.getResponseCode();
			if (responseCode == 200) {
				stream = con.getInputStream();
				return stream;
			}
		}
		throw new IOException("Couldn't create a proper connection to the remote host");
	}
	
	public static enum Status {
		UNKNOWN, CHECKING, UP_TO_DATE, OUTDATED, ERROR;
	}

}
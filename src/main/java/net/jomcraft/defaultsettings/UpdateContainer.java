package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.Level;
import org.apache.maven.artifact.versioning.ComparableVersion;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.versions.mcp.MCPVersion;
import static net.minecraftforge.fml.VersionChecker.Status.*;

public class UpdateContainer {

	private ExecutorService tpe = Executors.newFixedThreadPool(1);
	private Status status = Status.UNKNOWN;
	private String onlineVersion = null;

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setOnlineVersion(String version) {
		this.onlineVersion = version;
	}

	public String getOnlineVersion() {
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
				ModContainer mc = ModList.get().getModContainerByObject(DefaultSettings.getInstance()).get();
				VersionChecker.Status status = PENDING;
				ComparableVersion target = null;
				try {
					InputStream con = openUrlStream(mc.getModInfo().getUpdateURL());
					String data = new String(ByteStreams.toByteArray(con), "UTF-8");
					con.close();
					Map<String, Object> json = new Gson().fromJson(data, Map.class);

					Map<String, String> promos = (Map<String, String>) json.get("promos");
					// String display_url = (String) json.get("homepage");
					String mcVersion = MCPVersion.getMCVersion();
					String rec = promos.get(mcVersion + "-recommended");
					String lat = promos.get(mcVersion + "-latest");
					ComparableVersion current = new ComparableVersion(mc.getModInfo().getVersion().toString());

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
				if (target != null)
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
					DefaultSettings.getUpdater().setStatus(UpdateContainer.Status.AHEAD_OF_TIME);
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
		UNKNOWN, CHECKING, UP_TO_DATE, OUTDATED, ERROR, AHEAD_OF_TIME;
	}
}
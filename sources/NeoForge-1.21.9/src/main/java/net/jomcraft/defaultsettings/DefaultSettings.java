package net.jomcraft.defaultsettings;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;

import net.jomcraft.defaultsettings.commands.ConfigArguments;
import net.jomcraft.defaultsettings.commands.OperationArguments;
import net.jomcraft.defaultsettings.commands.TypeArguments;
import net.jomcraft.jcplugin.JCLogger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.jomcraft.jcplugin.FileUtilNoMC;

@Mod(DefaultSettings.MODID)
public class DefaultSettings {

    public static final String MODID = "defaultsettings";
    public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
    public static String VERSION = "none";
    public static Map<String, KeyContainer> keyRebinds = new HashMap<String, KeyContainer>();
    public static boolean setUp = false;
    public static DefaultSettings instance;
    public static boolean shutDown = false;
    public static String shutdownReason = null;

    public String getVersion() throws IOException, URISyntaxException {
        Manifest manifest = readManifest(this.getClass());
        Attributes attr = manifest.getMainAttributes();
        return attr.getValue("Implementation-Version");
    }

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, DefaultSettings.MODID);

    @SuppressWarnings({"deprecation"})
    public DefaultSettings(IEventBus modEventBus) {
        instance = this;
        NeoForgeCoreHook core = new NeoForgeCoreHook();
        Core.setInstance(core);

        try {
            VERSION = getVersion();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (FMLLoader.getCurrent().getDist().isClient()) {
            if (setUp) return;

            try {
                Field pluginClass = Class.forName("net.jomcraft.jcplugin.JCPlugin").getDeclaredField("checksSuccessful");

                if (!pluginClass.getBoolean(null)) {
                    shutDown = true;
                    shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Something is hella broken! Shutting down...");
                } else {

                    final Path location = FMLLoader.getCurrent().getGameDir();

                    File mods = new File(location.toFile(), "mods");

                    boolean foundDefaultSettings = false;
                    String wantedVersion = null;

                    for (File mod : mods.listFiles()) {
                        if (mod.getName().toLowerCase().contains("defaultsettings")) {

                            JarFile jar = new JarFile(mod);

                            ZipEntry toml = jar.getEntry("META-INF/MANIFEST.MF");
                            if (toml != null) {

                                BufferedReader result = new BufferedReader(new InputStreamReader(jar.getInputStream(toml)));

                                String readerLine;

                                while ((readerLine = result.readLine()) != null) {
                                    if (readerLine.contains("Implementation-Title: DefaultSettings")) {
                                        foundDefaultSettings = true;
                                    } else if (readerLine.startsWith("JCPluginVersion")) {
                                        wantedVersion = readerLine.split(": ")[1];
                                    }
                                }

                                result.close();
                            }

                            jar.close();

                            if (foundDefaultSettings && wantedVersion != null) {

                                if (wantedVersion.equals(JCLogger.class.getPackage().getImplementationVersion())) {
                                    DefaultSettings.log.log(Level.INFO, "DefaultSettings found correct version of JCPlugin, starting up...");
                                    break;
                                } else {
                                    shutDown = true;
                                    shutdownReason = "The correct JCPlugin mod version couldn't be found! Please install version " + wantedVersion;
                                    DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! JCPlugin version must be " + wantedVersion + "!");
                                }

                            }
                        }
                    }

                    if (FMLLoader.getCurrent().isProduction() && (!foundDefaultSettings || wantedVersion == null)) {
                        shutDown = true;
                        shutdownReason = "Strange! We can't find the DefaultSettings mod, eventhough you're currently using it!";
                        DefaultSettings.log.log(Level.ERROR, "DefaultSettings can't start up! Couldn't get requested version of JCPlugin!");
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException |
                     IllegalAccessException | IOException e) {
                shutDown = true;
                shutdownReason = "The JCPlugin mod couldn't be found! Please make sure that the correct version (probably " + VERSION + ") is installed!";
                DefaultSettings.log.log(Level.ERROR, "DefaultSettings is missing the JCPlugin mod! Shutting down...");
            }

            modEventBus.addListener(this::postInit);

            COMMAND_ARGUMENT_TYPES.register("ds_config", () -> ArgumentTypeInfos.registerByClass(ConfigArguments.class, new ConfigArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_operation", () -> ArgumentTypeInfos.registerByClass(OperationArguments.class, new OperationArguments.Info()));
            COMMAND_ARGUMENT_TYPES.register("ds_type", () -> ArgumentTypeInfos.registerByClass(TypeArguments.class, new TypeArguments.Info()));

            COMMAND_ARGUMENT_TYPES.register(modEventBus);

            //ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));

            NeoForge.EVENT_BUS.register(new EventHandlers());

            if (shutDown) return;

            try {
                FileUtil.restoreContents();

            } catch (Exception e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
            }
            setUp = true;

        }

        if (FMLLoader.getCurrent().getDist().isDedicatedServer()) {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    @SuppressWarnings("deprecation")
    public void postInit(FMLLoadCompleteEvent event) {
        if (FMLLoader.getCurrent().getDist().isClient()) {
            try {
                if (!shutDown) FileUtil.restoreKeys(true, FileUtilNoMC.privateJson.firstBootUp);
            } catch (IOException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            } catch (NullPointerException e) {
                DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
            }
        }

        if (FMLLoader.getCurrent().getDist().isDedicatedServer()) {
            DefaultSettings.log.log(Level.WARN, "DefaultSettings is a client-side mod only! It won't do anything on servers!");
        }
    }

    public static DefaultSettings getInstance() {
        return instance;
    }

    public static Manifest readManifest(Class<?> cls) throws IOException, URISyntaxException {
        CodeSource cs = cls.getProtectionDomain().getCodeSource();
        if (cs == null) return null;

        URL url = cs.getLocation();
        if (url == null) return null;

        return readManifest(url);
    }

    public static Path asPath(URL url) {
        try {
            return Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static Manifest readManifest(URL codeSourceUrl) throws IOException, URISyntaxException {
        Path path = asPath(codeSourceUrl);

        if (Files.isDirectory(path)) {
            return readManifest(path);
        } else {
            URLConnection connection = new URL("jar:" + codeSourceUrl.toString() + "!/").openConnection();

            if (connection instanceof JarURLConnection) {
                return ((JarURLConnection) connection).getManifest();
            }

            try (FileSystemDelegate jarFs = getJarFileSystem(path.toUri(), false)) {
                return readManifest(jarFs.get().getRootDirectories().iterator().next());
            }
        }
    }

    private static final Map<String, String> jfsArgsCreate = Collections.singletonMap("create", "true");
    private static final Map<String, String> jfsArgsEmpty = Collections.emptyMap();

    public static FileSystemDelegate getJarFileSystem(URI uri, boolean create) throws IOException {
        URI jarUri;

        try {
            jarUri = new URI("jar:" + uri.getScheme(), uri.getHost(), uri.getPath(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        boolean opened = false;
        FileSystem ret = null;

        try {
            ret = FileSystems.getFileSystem(jarUri);
        } catch (FileSystemNotFoundException ignore) {
            try {
                ret = FileSystems.newFileSystem(jarUri, create ? jfsArgsCreate : jfsArgsEmpty);
                opened = true;
            } catch (FileSystemAlreadyExistsException ignore2) {
                ret = FileSystems.getFileSystem(jarUri);
            } catch (IOException | ZipError e) {
                throw new IOException("Error accessing "+uri+": "+e, e);
            }
        }

        return new FileSystemDelegate(ret, opened);
    }

    public static Manifest readManifest(Path basePath) throws IOException {
        Path path = basePath.resolve("META-INF").resolve("MANIFEST.MF");
        if (!Files.exists(path)) return null;

        try (InputStream stream = Files.newInputStream(path)) {
            return new Manifest(stream);
        }
    }

    public static class FileSystemDelegate implements AutoCloseable {
        private final FileSystem fileSystem;
        private final boolean owner;

        public FileSystemDelegate(FileSystem fileSystem, boolean owner) {
            this.fileSystem = fileSystem;
            this.owner = owner;
        }

        public FileSystem get() {
            return fileSystem;
        }

        @Override
        public void close() throws IOException {
            if (owner) {
                fileSystem.close();
            }
        }
    }
}
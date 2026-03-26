//Part of fabric-loader (net.fabricmc.loader.impl.util.ManifestUtil)
package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.Collections;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipError;

public class ManifestUtility {

    private static final Map<String, String> jfsArgsCreate = Collections.singletonMap("create", "true");
    private static final Map<String, String> jfsArgsEmpty = Collections.emptyMap();

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

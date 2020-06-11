[![Version](https://badgen.net/https/apiv1.jomcraft.net/stats/defaultsettings/endpoint)](https://gitlab.com/jomcraft-sources/defaultsettings) [![Build Status](https://gitlab.com/jomcraft-sources/defaultsettings/badges/1.15.2/pipeline.svg)](https://gitlab.com/jomcraft-sources/defaultsettings/tree/1.15.2)

### DefaultSettings

---

Adding DefaultSettings as a dependency for your mod (use DS as an API)

##### Add this to your build.gradle

```md
repositories {
    maven {
        url "https://maven.jomcraft.net/repository/release"
    }
}

dependencies {
   deobfCompile 'de.pt400c.defaultsettings:DefaultSettings-1.14.x:[VERSION]'
}
```

Edition & version could be grabbed from [here](https://maven.jomcraft.net/repository/release/de/pt400c/defaultsettings/).
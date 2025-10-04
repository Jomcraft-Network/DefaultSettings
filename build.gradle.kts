import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("dev.isxander.modstitch.base") version "0.7.1-unstable"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val minecraft = property("deps.minecraft") as String;
val DSversion = "4.0.8"
val qualifiedModName = "DefaultSettings"
val jcpluginVersion = "4.0.4"

modstitch {
    minecraftVersion = minecraft

    javaVersion = when (minecraft) {
	    "1.17.1" -> 16
        "1.18.2" -> 17
        "1.19.4" -> 17
        "1.20" -> 17
        "1.20.1" -> 17
        "1.20.4" -> 17
        "1.20.6" -> 17
        "1.21" -> 21
        "1.21.4" -> 21
        "1.21.9" -> 21
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }

    metadata {
        modId = "defaultsettings"
        modName = qualifiedModName
        modVersion = DSversion
        modGroup = "net.jomcraft"
        modAuthor = "x.x.x"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {

            if (hasProperty("mc_version")) {
                put("mc_version", property("mc_version").toString())
            }

            if (hasProperty("forge_version")) {
                put("forge_version", property("forge_version").toString())
            }

            put("game_version", minecraft)

            put("JCPluginVersion", jcpluginVersion)

            put("mod_issue_tracker", "https://github.com/Jomcraft-Network/DefaultSettings/issues")

            put("pack_format", when (property("deps.minecraft")) {
			    "1.17.1" -> 8
                "1.18.2" -> 8
                "1.19.4" -> 8
                "1.20" -> 15
                "1.20.1" -> 15
                "1.20.4" -> 15
                "1.20.6" -> 15
                "1.21" -> 46
                "1.21.4" -> 46
                "1.21.9" -> 46
                else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
            }.toString())
        }
    }

    loom {
        fabricLoaderVersion = "0.16.10"
        configureLoom {

        }

    }

    moddevgradle {
        forgeVersion = findProperty("deps.forge") as String?
        mcpVersion = findProperty("deps.mcp") as String?
        neoFormVersion = findProperty("deps.neoform") as String?
        neoForgeVersion = findProperty("deps.neoforge") as String?

        defaultRuns()

        configureNeoForge {
            runs.all {
                disableIdeRun()
            }
        }

    }

    mixin {
        addMixinsToModManifest = true
        configs.register("defaultsettings") { side = CLIENT }
    }

    modstitch.onEnable {
        var sting = property("version_range")
        var loadString = property("modstitch.platform")
        var loader = "ERROR";
        if(loadString != null && loadString.equals("loom")){
            loader = "Fabric"
        } else if(loadString != null && loadString.equals("moddevgradle-legacy")){
            loader = "Forge"
        } else if(loadString != null && loadString.equals("moddevgradle")){
            loader = "NeoForge"
        }

        modstitch.finalJarTask {
            archiveBaseName = qualifiedModName
            archiveVersion = "${sting}-${DSversion}-${loader}"
        }
    }
}

tasks {

    jar {
        manifest {
            attributes("Specification-Title" to qualifiedModName)
            attributes("Specification-Vendor" to "Jomcraft Network")
            attributes("Specification-Version" to DSversion)
            attributes("Implementation-Title" to qualifiedModName)
            attributes("Implementation-Version" to DSversion)
            attributes("Implementation-Vendor" to "Jomcraft Network")
            attributes("Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Date()))
            attributes("JCPluginVersion" to jcpluginVersion)
        }
    }
}

var constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge"),
        "vanilla" to constraint.equals("vanilla")
    )
}

dependencies {
    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    }

    if(modstitch.platform.isLoom) {
        modstitchModImplementation("curse.maven:jcp-659192:4430988");
    } else if (modstitch.platform.isModDevGradleRegular) {
        modstitchModImplementation("curse.maven:jcp-659192:4920580");
    } else if (modstitch.platform.isModDevGradleLegacy) {
        modstitchModImplementation("curse.maven:jcp-659192:4573148");
    }
}
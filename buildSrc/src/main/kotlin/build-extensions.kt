import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*

val Project.mod: ModData get() = ModData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

val Project.loader: String? get() = prop("loader")
val Project.supported_loaders: List<String>? get() = prop("supported_loaders")?.split(",")

@JvmInline
value class ModData(private val project: Project) {
    val archivesBaseName: String get() = prop("archives_base_name")
    val version: String get() = prop("mod_version")
    val mc: String get() = prop("minecraft_version")

    fun propOrNull(key: String): String? = project.prop(key)
    fun prop(key: String): String = requireNotNull(propOrNull(key)) { "Missing '$key'" }

    fun getReleaseType(): String {
        val semverExt = version.indexOf("-")

        if (semverExt == -1)
            return "STABLE"

        val extType: String
        val extVer = version.indexOf(".")
        if (extVer != -1) {
            extType = version.substring(semverExt + 1, extVer)
        } else {
            extType = version.substring(semverExt + 1)
        }

        if (extType == "indev")
            return "ALPHA"

        if (extType == "rc")
            return "BETA"

        return extType.uppercase()
    }
}
package co.there4.hexagon.configuration

import co.there4.hexagon.util.camelToSnake
import java.io.File
import java.lang.ClassLoader.getSystemClassLoader
import java.lang.System.getenv
import java.lang.Thread.currentThread
import java.util.*

enum class Environment { PRODUCTION, INTEGRATION, DEVELOPMENT }

/**
 * TODO Use JSON
 *
 * Reads:
 * - <project>.properties (resource)
 * - <project>_<environment>.properties (resource)
 * - <project>.properties (file)
 * - <project>_<environment>.properties (file)
 */
object ConfigManager {
    val mainClass = getServiceClass()
    val jarPath = File (mainClass.protectionDomain.codeSource.location.toURI())
    val serviceDir = if (jarPath.isFile) jarPath.parentFile.parent else jarPath.parent
    val servicePackage = mainClass.`package`.name
    val serviceName = mainClass.simpleName.camelToSnake().removeSuffix("_kt")

    private val parameters: Map<String, *> = loadParameters()

    val environment: String? = getenv("ENVIRONMENT")

    private fun loadParameters (): Map<String, *> {
        var params = loadProps("${serviceName}.properties")

        if (environment != null)
            params = params + loadProps("${serviceName}_${environment}.properties")

        return params
    }

    private fun getServiceClass (): Class<*> {
        val first = currentThread().stackTrace.firstOrNull { it.methodName == "main" }
        return if (first != null) Class.forName (first.className) else ConfigManager.javaClass
    }

    private fun loadProps (resName: String): Map<String, *> {
        val resource = getSystemClassLoader().getResourceAsStream(resName)
        return if (resource == null) {
            mapOf<String, Any>()
        }
        else {
            val v = Properties()
            v.load(resource)
            v
                .filter { it.key != null && it.key is String }
                .map { it.key as String to it.value }
                .toMap()
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get (key: String): T = parameters[key] as T
}

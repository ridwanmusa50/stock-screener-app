import com.android.build.api.dsl.ApplicationVariantDimension
import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties
import java.io.File

fun Project.getProperties(filename: String): Properties {
    val properties = Properties()
    properties.load(FileInputStream(File(rootDir, "${filename}.properties")))
    return properties
}

fun ApplicationVariantDimension.buildConfigStringField(name: String, value: String) = buildConfigField("String", name, "\"$value\"")

fun ApplicationVariantDimension.buildConfigFieldWithProperty(name: String, properties: Properties) =
    buildConfigStringField(name, properties.getProperty(name))
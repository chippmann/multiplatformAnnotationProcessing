package ch.hippmann.annotationprocessor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import org.reflections.Reflections
import org.reflections.scanners.*
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.typeOf

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("ch.hippmann.annotation.Register")
@SupportedOptions(RegisterAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class RegisterAnnotationProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "Processing")

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        annotations?.forEach {
            roundEnv?.getElementsAnnotatedWith(it)?.forEach {
                if (it.kind == ElementKind.CLASS) {
                    val file = FileSpec.builder((it.enclosingElement as PackageElement).qualifiedName.toString(), "${it.simpleName}Script")
                            .addType(TypeSpec.classBuilder("${it.simpleName}Script")
                                    .addFunction(
                                            FunSpec.builder("someBridge")
                                                    .addParameter("name", String::class)
                                                    .addStatement("println(\"Hello from \$name\")")
                                                    .build()
                                    )
                                    .build()
                            )

                            .build()

                    file.writeTo(File(kaptKotlinGeneratedDir))
                }
                processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, it.simpleName)
            }
        }

        return true
    }


}
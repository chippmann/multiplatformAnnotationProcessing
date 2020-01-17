package ch.hippmann.annotationprocessor

import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

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
            generateClassBindings(roundEnv, it, kaptKotlinGeneratedDir)
        }

        return true
    }

    private fun generateClassBindings(roundEnv: RoundEnvironment?, typeElement: TypeElement, kaptKotlinGeneratedDir: String) {
        roundEnv?.getElementsAnnotatedWith(typeElement)?.forEach { element ->
            if (element.kind == ElementKind.CLASS) {
                val fileSpec = FileSpec.builder((element.enclosingElement as PackageElement).qualifiedName.toString(), "${element.simpleName}Script")
                val classSpec = TypeSpec.classBuilder("${element.simpleName}Script")

                (element as TypeElement).enclosedElements.forEach { enclosedElement ->
                    if (enclosedElement.kind == ElementKind.METHOD) {
                        generateMethodBinding(classSpec, enclosedElement)
                    } else if (enclosedElement.kind == ElementKind.FIELD) {
                        generateFieldBinding(classSpec, enclosedElement)
                    }
                }

                fileSpec.addType(classSpec.build())
                        .build()
                        .writeTo(File(kaptKotlinGeneratedDir))
            }
            processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, element.simpleName)
        }
    }

    private fun generateMethodBinding(classSpec: TypeSpec.Builder, enclosedElement: Element) {
        classSpec.addFunction(
                FunSpec.builder("${enclosedElement.simpleName}Bind")
                        .addStatement("println(\"I'm a binding :-)\")")
                        .build()
        ).build()
    }

    private fun generateFieldBinding(classSpec: TypeSpec.Builder, enclosedElement: Element) {
        classSpec.addProperty(
                PropertySpec.builder("${enclosedElement.simpleName}Bind", String::class)
                        .initializer("\"blubb\"")
                        .build()
        )
    }
}
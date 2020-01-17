package ch.hippmann.annotationprocessor

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Register

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("ch.hippmann.annotationprocessor.Register")
@SupportedOptions(RegisterAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class RegisterAnnotationProcessor : AbstractProcessor(){

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, "Processing")
        return true
    }


}
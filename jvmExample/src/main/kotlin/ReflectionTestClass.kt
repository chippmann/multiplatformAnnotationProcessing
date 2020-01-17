import ch.hippmann.annotationprocessor.Register

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Fancy

@Fancy
@Register
class ReflectionTestClass {

    @Fancy
    @Register
    fun reflectionTestClassFunction() {

    }
}
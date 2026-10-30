/*
 * Archivo de compilación raíz: declara los plugins (sin aplicarlos) para que
 * los módulos los usen con la versión definida en gradle/libs.versions.toml.
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

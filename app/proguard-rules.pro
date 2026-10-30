# Reglas de ProGuard/R8 para Academic Mobile.
# La minificación está desactivada por defecto; estas reglas quedan listas por si se activa.

# Conservar los DTO serializados con Gson (los nombres de campo se mapean por reflexión).
-keep class com.academic.mobile.data.models.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-keepattributes Exceptions, InnerClasses, EnclosingMethod
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

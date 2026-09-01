# Vexono Proguard Rules for Release Builds
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.vexono.app.data.local.entity.** { *; }
-keep class com.vexono.app.domain.model.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

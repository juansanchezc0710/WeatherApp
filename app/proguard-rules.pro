# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep BuildConfig and all its fields
-keep class com.example.weatherapp.BuildConfig {
    public static final ** *;
}
-keepclassmembers class com.example.weatherapp.BuildConfig {
    public static final ** *;
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.logging.** { *; }
-keepclassmembers class okhttp3.logging.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.Unsafe
-keep class com.google.gson.Gson { *; }
-keep class com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapter
-dontwarn com.google.gson.TypeToken
-dontwarn com.google.gson.internal.*

# Keep data models for Gson serialization
-keep class com.example.weatherapp.data.model.** { *; }
-keep class com.example.weatherapp.domain.model.** { *; }

# Koin
-dontwarn org.koin.core.annotation.*
-keepnames class kotlin.Metadata

# Keep ViewModels
-keep class com.example.weatherapp.ui.viewmodel.** { *; }

# Keep Repository interfaces and implementations
-keep interface com.example.weatherapp.domain.repository.** { *; }
-keep class com.example.weatherapp.data.repository.** { *; }

# Keep Use Cases
-keep class com.example.weatherapp.domain.usecase.** { *; }

# Keep API services
-keep interface com.example.weatherapp.data.api.** { *; }
-keep class com.example.weatherapp.data.api.NetworkModule {
    *;
}
-keepclassmembers class com.example.weatherapp.data.api.NetworkModule {
    *;
}

# Keep Mappers
-keep class com.example.weatherapp.data.mapper.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# Keep custom Application class
-keep class com.example.weatherapp.WeatherApp { *; }

# Keep Logger utility
-keep class com.example.weatherapp.util.Logger { *; }

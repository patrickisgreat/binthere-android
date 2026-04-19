# Keep Compose-generated classes when R8 is enabled.
-keep class androidx.compose.** { *; }

# Keep Hilt-generated classes.
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManagerHolder { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# Keep kotlinx.serialization-generated serializers.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room generates code; keep model classes referenced by annotations.
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }

# Supabase (Ktor) uses reflection for a few codecs.
-keepnames class io.ktor.** { *; }

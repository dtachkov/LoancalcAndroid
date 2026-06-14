# Gson / Retrofit
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, Exceptions

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class ru.kredit.calculator.data.model.** { *; }
-keep class ru.kredit.calculator.data.network.** { *; }
-keep class ru.kredit.calculator.database.entity.** { *; }

# Loan calculation engine
-keep class com.zoom.loancalc.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# AppMetrica
-keep class io.appmetrica.analytics.** { *; }
-dontwarn io.appmetrica.analytics.**

# RuStore SDK
-keep class ru.rustore.sdk.** { *; }
-dontwarn ru.rustore.sdk.**

# Android components referenced from manifest
-keep class com.example.loancalcandroid.MainActivity { *; }
-keep class com.example.loancalcandroid.LoanCalcApplication { *; }
-keep class com.example.loancalcandroid.notification.** { *; }
-keep class com.example.loancalcandroid.widget.** { *; }

# Parcelable / enums
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# OkHttp / Okio
-dontwarn okhttp3.internal.platform.*
-dontwarn okio.**
-dontwarn org.conscrypt.**

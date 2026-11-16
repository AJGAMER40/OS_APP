# Proguard rules for ZeroVault Security App

# Preserve AndroidX Security Crypto classes
-keep class androidx.security.crypto.** { *; }

# Preserve AndroidX Biometric classes
-keep class androidx.biometric.** { *; }

# WorkManager rules
-keep class androidx.work.** { *; }

# ZeroVault threat and crypto models
-keepclassmembers class com.zerovault.app.data.model.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

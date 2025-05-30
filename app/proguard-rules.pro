# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#Preserve all annotations

-keepattributes Annotation

-keep class com.main.proyek_salez.data.model.FoodItemEntity { <init>(...); *; }
-keep class com.main.proyek_salez.data.model.CategoryEntity { <init>(...); *; }
-keep class com.main.proyek_salez.data.model.OrderEntity { <init>(...); *; }
-keep class com.main.proyek_salez.data.model.CartItemEntity { <init>(...); *; }
-keep class com.main.proyek_salez.data.model.DailySummaryEntity { <init>(...); *; }

#Keep Firestore classes

-keep class com.google.firebase.firestore.** { *; }

#Keep Room classes and entities

-keep class androidx.room.** { *; } -keep @androidx.room.Entity class * { *; }
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Proguard Exception for play services
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# suppress warnings
-dontwarn com.google.android.gms.**

# solve: (SearchView) MenuItemCompat.getActionView -> returns null
-keep class android.support.v7.widget.SearchView { *; }

# for com.squareup.picasso
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

-keep class com.google.ads.** # Don't proguard AdMob classes

-dontwarn com.google.ads.** # Temporary workaround for v6.2.1. It gives a warning that you can ignore

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.**
-keep class ru.besttuts.stockwidget.sync.model.** { *; }
-keep class ru.besttuts.stockwidget.io.model.** { *; }
-keep class ru.besttuts.stockwidget.model.** { *; }
-keep class ru.besttuts.stockwidget.ui.EconomicWidget;

#-keep class ru.besttuts.stockwidget.** # Don't proguard classes for Debug


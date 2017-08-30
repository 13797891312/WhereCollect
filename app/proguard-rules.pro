# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\application\sdk/tools/proguard/proguard-android.txt
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


-optimizationpasses 5
 -dontusemixedcaseclassnames
 -dontskipnonpubliclibraryclasses
 -dontoptimize
 -dontpreverify
 -verbose
 -optimizations !code/simplification/arithmetic,!field/,!class/merging/
 -keepattributes Annotation
 -keep public class * extends android.app.Fragment
 -keep public class * extends android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference
 -keep public class com.android.vending.licensing.ILicensingService
 -keep public class * extends android.support.v4.app.Fragment
 -ignorewarning
 -dump class_files.txt
 -printseeds seeds.txt
 -printusage unused.txt
 -printmapping mapping.txt


-keep class com.UCMobile.**{*;}

-keep class android.supportv7.** { *; }
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}

-keep class com.android.volley.**{*;}
-keep class com.github.mikephil.charting.**{*;}
-keep class android.support.design.**{*;}
-keep class android.net.**{*;}
-keep class com.nostra13.**{*;}
-keep class com.nineoldandroids.**{*;}
-keep class com.zhaojin.**{*;}
-keep class uk.co.senab.photoview.**{*;}
-keep class com.handmark.**{*;}
-dontwarn org.apache.**
-keep class org.apache.**{*;}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable {*;}
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }
-keep class com.gongwu.wherecollect.entity.**{*;}

-keep class butterknife.** { *; }
-keep class butterknife.internal { *; }
-dontwarn butterknife.internal.**
-dontwarn butterknife.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}


-keep class com.umeng.qq.handler.** {*;}
-keep class com.umeng.qq.tencent.** {*;}
-keep class com.umeng.socialize.umengqq.** {*;}
-keep class com.umeng.weixin.callback.** {*;}
-keep class com.umeng.weixin.handler.** {*;}
-keep class com.umeng.weixin.umengwx.** {*;}
-keep class com.umeng.socialize.controller.** {*;}
-keep class com.umeng.socialize.handler.** {*;}
-keep class com.umeng.socialize.** {*;}
-keep class com.umeng.** {*;}
-keep class android.app.enterprise.** {*;}
-keep public class com.gongwu.wherecollect.R$*{
public static final int *;
}

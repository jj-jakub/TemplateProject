# R8 keep rules for the release build.
#
# Consumer rules bundled by the libraries themselves (Firebase, Retrofit/OkHttp, kotlinx-
# serialization, Compose) are applied automatically, so this file only covers what is specific to
# this app.

# ---- Readable crash reports ----
# Without these two the line numbers in a release stack trace are gone and every frame points at an
# obfuscated file name, which makes a production crash report almost unreadable even when the
# mapping file is available. SourceFile is kept but renamed away, so the attribute exists for the
# line table to hang off without leaking the original file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- App enums ----
# Analytics events and screen names are frequently logged via an enum's `.name`. R8 renames enum
# constants, so without this the events arrive in the dashboard garbled and cannot be grouped.
# Cheap, since an app's own enums are few.
-keepclassmembers enum com.jj.templateproject.** {
    *;
}

# ---- Dangling compile-time annotations ----
# R8 treats a reference to a class that is not on the runtime classpath as a hard error, not a
# warning, and some Play/Firebase artifacts carry annotations from a sibling artifact that is not a
# transitive dependency. They are compile-time only, so ignoring them is safe; without a -dontwarn
# the release build fails outright at minify time. Add one line per annotation R8 names, e.g.:
# -dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite

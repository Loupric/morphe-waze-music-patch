package app.loupric.patches.ytmusic

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.Compatibility

private val COMPATIBILITY_YOUTUBE_MUSIC = Compatibility(
    name = "YouTube Music",
    packageName = "com.google.android.apps.youtube.music",
    apkFileType = ApkFileType.APK,
    targets = listOf(AppTarget(version = null))
)

// Fingerprint for swe.c()V — loads GoogleCertificatesImpl via Dynamite, crashes with MicroG
private object SweCFingerprint : Fingerprint(
    custom = { methodDef, classDef ->
        methodDef.name == "c" &&
        methodDef.returnType == "V" &&
        methodDef.parameters.isEmpty() &&
        classDef.type == "Lswe;"
    }
)

// Fingerprint for swe.a(String, swa, Z, Z)swm — verifies Google certificates
private object SweAFingerprint : Fingerprint(
    custom = { methodDef, classDef ->
        methodDef.name == "a" &&
        methodDef.returnType == "Lswm;" &&
        methodDef.parameters.size == 4 &&
        classDef.type == "Lswe;"
    }
)

// Fingerprint for sws.f(String)swm — checks if caller package is authorized
private object SwsFFingerprint : Fingerprint(
    custom = { methodDef, classDef ->
        methodDef.name == "f" &&
        methodDef.returnType == "Lswm;" &&
        methodDef.parameters.size == 1 &&
        classDef.type == "Lsws;"
    }
)

@Suppress("unused")
val wazeCompatibilityPatch = bytecodePatch(
    name = "Waze compatibility (MicroG)",
    description = "Fixes crash when Waze tries to connect via MediaBrowserService on MicroG devices.",
    default = true
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        // Patch 1: swe.c() → return-void immediately
        // Prevents loading GoogleCertificatesImpl via Dynamite (not supported by MicroG)
        SweCFingerprint.method.addInstruction(0, "return-void")

        // Patch 2: swe.a() → always return swm.a (the "authorized" singleton)
        // swm.a is new swm(true) — bypasses GMS certificate check
        SweAFingerprint.method.addInstructions(
            0,
            """
            sget-object v0, Lswm;->a:Lswm;
            return-object v0
            """
        )

        // Patch 3: sws.f() → always return swm.a (bypass signature verification)
        SwsFFingerprint.method.addInstructions(
            0,
            """
            sget-object v0, Lswm;->a:Lswm;
            return-object v0
            """
        )
    }
}

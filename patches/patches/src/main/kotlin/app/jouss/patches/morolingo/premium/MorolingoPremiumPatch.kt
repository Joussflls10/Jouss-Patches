package app.jouss.patches.morolingo.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.jouss.patches.morolingo.shared.Constants.COMPATIBILITY_MOROLINGO

@Suppress("unused")
val morolingoPremiumPatch = bytecodePatch(
    name = "Morolingo Premium",
    description = "Unlocks premium features in the Morolingo Darija & Amazigh learning app."
) {
    compatibleWith(COMPATIBILITY_MOROLINGO)

    execute {
        // 1. Bypass Pairip license check — force responseCode to 0 (LICENSED)
        //    Without this, the app shows a paywall or exits when it detects it is not licensed.
        PairipProcessResponseFingerprint.method.addInstructions(0, """
            const/4 p1, 0x0
            return-void
        """)

        // 2. Skip Pairip RSA signature validation
        //    Prevents LicenseCheckException from being thrown on re-signed APK.
        PairipValidateResponseFingerprint.method.addInstructions(0, """
            return-void
        """)

        // 3. Force RevenueCat EntitlementInfo.getIsActive() to always return true
        //    This is the deepest point in the entitlement pipeline — patching here
        //    causes EntitlementInfos.getActive() to include all entitlements, which
        //    the React Native JS bridge then receives as active subscriptions.
        EntitlementInfoIsActiveFingerprint.method.addInstructions(0, """
            const/4 v0, 0x1
            return v0
        """)
    }
}

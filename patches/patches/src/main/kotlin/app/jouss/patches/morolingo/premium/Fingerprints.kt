package app.jouss.patches.morolingo.premium

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

// LicenseClient.processResponse — Pairip license check callback
// responseCode p1: 0=LICENSED, 2=NOT_LICENSED (shows paywall/kills app), 3=ERROR
object PairipProcessResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseClient;",
    name = "processResponse",
    returnType = "V",
)

// LicenseResponseHelper.validateResponse — Pairip RSA signature validation
// Throws LicenseCheckException if signature doesn't match; bypassing makes any response valid
object PairipValidateResponseFingerprint : Fingerprint(
    definingClass = "Lcom/pairip/licensecheck/LicenseResponseHelper;",
    name = "validateResponse",
    returnType = "V",
)

// EntitlementInfo.getIsActive() — RevenueCat deepest entitlement check
// Compiled as component2()Z in smali (Kotlin data class property getter)
// Patching to return true cascades through entire pipeline: EntitlementInfos.getActive(),
// EntitlementInfosMapper, and the JS bridge — JS receives all entitlements as active
object EntitlementInfoIsActiveFingerprint : Fingerprint(
    definingClass = "Lcom/revenuecat/purchases/EntitlementInfo;",
    returnType = "Z",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    parameters = listOf(),
    filters = listOf(
        fieldAccess(
            opcode = Opcode.IGET_BOOLEAN,
            definingClass = "Lcom/revenuecat/purchases/EntitlementInfo;",
            name = "isActive"
        )
    )
)

package com.jj.templateproject.data.app

import android.content.Context
import android.provider.Settings
import com.google.android.vending.licensing.AESObfuscator
import com.google.android.vending.licensing.LicenseChecker
import com.google.android.vending.licensing.LicenseCheckerCallback
import com.google.android.vending.licensing.ServerManagedPolicy
import com.jj.templateproject.BuildConfig
import com.jj.templateproject.domain.app.AppInfoRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DefaultAppInfoRepository(
    context: Context,
) : AppInfoRepository {

    private val salt = byteArrayOf(
        -44, 61, 32, -118, -13, -5, 41, -4, 21, 48, -75, -65, 47, -117, -31, -111, -121, 22, -65,
        81,
    )

    // TODO Insert proper key into build.gradle when implementing usage of this library
    private val base64PublicKey = BuildConfig.licensingBase64PublicKey

    private val serverManagedPolicy = ServerManagedPolicy(
        context,
        AESObfuscator(
            /* salt = */
            salt,
            /* applicationId = */
            context.packageName,
            /* deviceId = */
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID),
        )
    )

    // Construct the LicenseChecker with a policy.
    private val checker = LicenseChecker(
        /* context = */ context,
        /* policy = */ serverManagedPolicy,
        /* encodedPublicKey = */ base64PublicKey,
    )

    override suspend fun installedFromValidSource(): Boolean = suspendCoroutine {
        if (base64PublicKey.isBlank()) {
            it.resume(true)
        } else {
            checker.checkAccess(
                getCallback(
                    onVerificationSuccess = { it.resume(true) },
                    onVerificationFail = { it.resume(false) },
                )
            )
        }
    }

    private fun getCallback(
        onVerificationSuccess: () -> Unit,
        onVerificationFail: () -> Unit,
    ) = object : LicenseCheckerCallback {
        override fun allow(reason: Int) = onVerificationSuccess()
        override fun dontAllow(reason: Int) = onVerificationFail()
        override fun applicationError(errorCode: Int) = onVerificationFail()
    }
}
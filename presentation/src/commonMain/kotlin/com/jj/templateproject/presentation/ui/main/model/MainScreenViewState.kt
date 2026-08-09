package com.jj.templateproject.presentation.ui.main.model

data class MainScreenViewState(
    val loading: Boolean = false,
    val versionText: String = "",
    val apiCallData: String = "",
    val apiCallStatus: String = "",
    val installedFromValidSource: Boolean? = null,
    /** `null` when cross-promotion is off — see `GetCrossPromoConfigUseCase`. */
    val crossPromoLabel: String? = null,
)

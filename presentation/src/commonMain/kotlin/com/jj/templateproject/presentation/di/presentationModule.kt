package com.jj.templateproject.presentation.di

import com.jj.templateproject.domain.ad.GetInterstitialAdUnitId
import com.jj.templateproject.domain.ad.GetMainAdUnitId
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.presentation.MainRootViewModel
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import com.jj.templateproject.presentation.ui.settings.VersionTextProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Everything `:presentation` owns: its ViewModels (registered with Koin's multiplatform
 * `viewModel { }` builder, the counterpart of `koinViewModel()` on the reading side in
 * `MainNavGraph`) and the thin use cases that only exist to give [MainRootViewModel] and
 * [VersionTextProvider] something platform-agnostic to depend on ([GetMainAdUnitId],
 * [GetInterstitialAdUnitId] read through `AdUnitIds`; `VersionTextProvider` reads
 * `AppVersionInfo`; [GetIsInstalledFromValidSource] reads `AppInfoRepository`). The interfaces
 * themselves live in `:domain`; a real implementation of each is bound wherever an application
 * module has a concrete `BuildConfig`/platform API to read one from — today that is only `:app`'s
 * own `mainModule`.
 */
val presentationModule = module {
    single { GetMainAdUnitId(adUnitIds = get()) }
    single { GetInterstitialAdUnitId(adUnitIds = get()) }
    single { VersionTextProvider(appVersionInfo = get()) }
    single { GetIsInstalledFromValidSource(appInfoRepository = get()) }

    viewModel {
        MainScreenViewModel(adManager = get())
    }
    viewModel {
        SettingsScreenViewModel(
            versionTextProvider = get(),
            getGoogleStatusUseCase = get(),
            getGoogleDataUseCase = get(),
            getIsInstalledFromValidSource = get(),
            getThemeModeUseCase = get(),
            setThemeModeUseCase = get(),
        )
    }
    viewModel {
        MainRootViewModel(
            getMainAdUnitId = get(),
            getThemeModeUseCase = get(),
            analyticsLogger = get(),
        )
    }
    viewModel {
        SecondaryScreenViewModel(savedStateHandle = get())
    }
}

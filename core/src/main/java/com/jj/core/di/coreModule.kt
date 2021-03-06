package com.jj.core.di

import androidx.compose.ui.text.AnnotatedString
import androidx.room.Room
import com.jj.core.data.AccelerometerSampleAnalyzer
import com.jj.core.data.api.DefaultAccelerometerAPI
import com.jj.core.data.csv.DefaultCSVFileCreator
import com.jj.core.data.database.AnalysedSamplesDatabase
import com.jj.core.data.database.SamplesDatabase
import com.jj.core.data.repository.DefaultAccelerometerRepository
import com.jj.core.data.repository.DefaultGPSRepository
import com.jj.core.data.repository.DefaultGlobalEventRepository
import com.jj.core.data.repository.DefaultGyroscopeRepository
import com.jj.core.data.repository.DefaultMagneticFieldRepository
import com.jj.core.data.repository.DefaultPathRepository
import com.jj.core.data.repository.DefaultSamplesRepository
import com.jj.core.data.repository.DefaultSensorsRepository
import com.jj.core.data.repository.GlobalEventsCollector
import com.jj.core.data.samples.accelerometer.AccelerometerThresholdAnalyzer
import com.jj.core.data.samples.gps.DefaultGPSPathAnalyser
import com.jj.core.data.samples.gps.DefaultGPSSampleAnalyzer
import com.jj.core.data.samples.gps.DefaultGPSVelocityCalculator
import com.jj.core.data.sensors.AccelerometerDataCollector
import com.jj.core.data.sensors.GPSDataCollector
import com.jj.core.data.sensors.GlobalSensorCollector
import com.jj.core.data.sensors.GlobalSensorManager
import com.jj.core.data.text.VersionTextProvider
import com.jj.core.data.time.DefaultTimeProvider
import com.jj.core.domain.api.AccelerometerAPI
import com.jj.core.domain.coroutines.CoroutineScopeProvider
import com.jj.core.domain.csv.CSVFileCreator
import com.jj.core.domain.events.EventsCollector
import com.jj.core.domain.events.GlobalEventsRepository
import com.jj.core.domain.gps.GPSPathAnalyser
import com.jj.core.domain.gps.GPSVelocityCalculator
import com.jj.core.domain.managers.AnalyzerStarter
import com.jj.core.domain.repository.AccelerometerRepository
import com.jj.core.domain.repository.GPSRepository
import com.jj.core.domain.repository.GyroscopeRepository
import com.jj.core.domain.repository.MagneticFieldRepository
import com.jj.core.domain.repository.PathRepository
import com.jj.core.domain.repository.SensorsRepository
import com.jj.core.domain.samples.accelerometer.AccThresholdAnalyzer
import com.jj.core.domain.samples.samples.gps.GPSSampleAnalyzer
import com.jj.core.domain.sensors.IGlobalSensorManager
import com.jj.core.domain.sensors.SamplesRepository
import com.jj.core.domain.time.TimeProvider
import com.jj.core.domain.ui.text.TextCreator
import com.jj.core.framework.domain.managers.AndroidAnalyzerStarter
import com.jj.core.framework.notification.NotificationManagerBuilder
import com.jj.core.framework.presentation.SensorsDataViewModel
import com.jj.core.framework.text.ComposeTextCreator
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coreModule = module {

    single { Room.databaseBuilder(androidContext(), SamplesDatabase::class.java, "samples_database.db").build() }
    single { Room.databaseBuilder(androidContext(), AnalysedSamplesDatabase::class.java, "analysed_samples_database.db").build() }

    single<SamplesRepository> { DefaultSamplesRepository(get<SamplesDatabase>().gpsDataDao, get<SamplesDatabase>().accelerationDataDao) }
    single<GlobalEventsRepository> { DefaultGlobalEventRepository(get<SamplesDatabase>().globalEventDataDao) }

    single { VersionTextProvider() }
    single<TimeProvider> { DefaultTimeProvider() }
    single<AccThresholdAnalyzer> { AccelerometerThresholdAnalyzer(get()) }
    single<AccelerometerRepository> {
        DefaultAccelerometerRepository(
            accelerometerManager = get(),
            accelerometerAPI = get(),
            analysedAccelerometerSampleDao = get<AnalysedSamplesDatabase>().analysedAccelerometerSampleDao
        )
    }
    single<GyroscopeRepository> { DefaultGyroscopeRepository(get()) }
    single<MagneticFieldRepository> { DefaultMagneticFieldRepository(get()) }

    single<GPSRepository> {
        DefaultGPSRepository(
            gpsManager = get(),
            analysedGPSSampleDao = get<AnalysedSamplesDatabase>().analysedGPSSampleDao
        )
    }

    single<SensorsRepository> { DefaultSensorsRepository(get(), get(), get()) }
    single<PathRepository> { DefaultPathRepository() }

    single { AccelerometerSampleAnalyzer(get(), get(), get(), get()) }
    single<GPSSampleAnalyzer> { DefaultGPSSampleAnalyzer(get(), get(), get()) }
    single<AnalyzerStarter> { AndroidAnalyzerStarter(get()) }

    single<AccelerometerAPI> { DefaultAccelerometerAPI() }

    single { AccelerometerDataCollector() }
    single { GPSDataCollector() }

    single { GlobalSensorCollector(get(), get(), get(), get()) }
    single { NotificationManagerBuilder() }

    single<CoroutineScopeProvider> { com.jj.core.data.coroutines.DefaultCoroutineScopeProvider() }

    single<EventsCollector> { GlobalEventsCollector(get(), get()) }

    single<IGlobalSensorManager> { GlobalSensorManager(get(), get(), get()) }

//    single<TextCreator<Spannable>> { AndroidTextCreator() }
    single<TextCreator<AnnotatedString>> { ComposeTextCreator() }
    viewModel { SensorsDataViewModel(get(), get(), get(), get(), get(), get()) }


    single<GPSPathAnalyser> {
        DefaultGPSPathAnalyser(
            gpsRepository = get(),
            pathRepository = get(),
            gpsVelocityCalculator = get(),
            coroutineScopeProvider = get()
        )
    }

    single<CSVFileCreator> { DefaultCSVFileCreator(androidContext()) }

    single<GPSVelocityCalculator> { DefaultGPSVelocityCalculator() }
}

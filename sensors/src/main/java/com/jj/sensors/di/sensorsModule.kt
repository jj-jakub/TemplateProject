package com.jj.sensors.di

import com.jj.core.domain.managers.GyroscopeManager
import com.jj.core.domain.managers.MagneticFieldManager
import com.jj.core.domain.monitors.SystemStateMonitor
import com.jj.core.domain.sensors.interfaces.AccelerometerManager
import com.jj.core.domain.sensors.interfaces.GPSManager
import com.jj.sensors.data.monitors.DefaultAccelerometerStateMonitor
import com.jj.sensors.data.monitors.DefaultGPSStateMonitor
import com.jj.sensors.data.monitors.DefaultGyroscopeStateMonitor
import com.jj.sensors.data.monitors.DefaultMagneticFieldStateMonitor
import com.jj.sensors.data.monitors.DefaultSystemStateMonitor
import com.jj.sensors.domain.monitors.markers.AccelerometerStateMonitor
import com.jj.sensors.domain.monitors.markers.GPSStateMonitor
import com.jj.sensors.domain.monitors.markers.GyroscopeStateMonitor
import com.jj.sensors.domain.monitors.markers.MagneticFieldStateMonitor
import com.jj.sensors.framework.managers.AndroidAccelerometerManager
import com.jj.sensors.framework.managers.AndroidGPSManager
import com.jj.sensors.framework.managers.AndroidMagneticFieldManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sensorsModule = module {

    single<AccelerometerManager> {
        AndroidAccelerometerManager(
            context = androidContext(),
            coroutineScopeProvider = get()
        )
    }
    single<GPSManager> {
        AndroidGPSManager(
            context = androidContext(),
            coroutineScopeProvider = get()
        )
    }

    single<GyroscopeManager> {
        com.jj.sensors.framework.managers.AndroidGyroscopeManager(
            context = androidContext(),
            coroutineScopeProvider = get()
        )
    }

    single<MagneticFieldManager> {
        AndroidMagneticFieldManager(
            context = androidContext(),
            coroutineScopeProvider = get()
        )
    }

    single<AccelerometerStateMonitor> { DefaultAccelerometerStateMonitor(get(), get(), get(), get()) }
    single<GyroscopeStateMonitor> { DefaultGyroscopeStateMonitor(get(), get(), get(), get()) }
    single<MagneticFieldStateMonitor> { DefaultMagneticFieldStateMonitor(get(), get(), get(), get()) }
    single<GPSStateMonitor> { DefaultGPSStateMonitor(get(), get(), get(), get()) }
    single<SystemStateMonitor> { DefaultSystemStateMonitor(get(), get(), get(), get()) }
}
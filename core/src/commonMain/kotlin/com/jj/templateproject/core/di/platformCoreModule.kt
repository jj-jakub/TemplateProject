package com.jj.templateproject.core.di

import org.koin.core.module.Module

/**
 * The half of `:core`'s wiring that only a platform can answer: the clock, the device read, the
 * share sheet, the foreground signal, the launch-attempt store and the notification surface.
 *
 * A function rather than a `val` because Koin's `module { }` builder runs its definitions eagerly,
 * and an Android `actual` reaches for `androidContext()` inside those definitions; keeping it lazy
 * means the module is built when the graph is started, by which point a context has been supplied.
 *
 * Each `actual` binds the same set of domain interfaces, with two deliberate asymmetries documented
 * where they are: iOS has no `InitializeBack4App` to bind, and its `NotificationManager` is the
 * domain no-op rather than a real implementation.
 */
expect fun platformCoreModule(): Module

package com.jj.templateproject.framework.navigation.model

import androidx.lifecycle.SavedStateHandle
import kotlin.reflect.KProperty1

inline fun <reified T : Route, R> SavedStateHandle.getArgument(property: KProperty1<T, R>): R? {
    return this[property.name]
}

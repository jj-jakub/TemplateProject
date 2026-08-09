package com.jj.templateproject.data.google

import com.jj.templateproject.data.google.network.TemplateNetworkApi
import com.jj.templateproject.domain.coroutines.DispatcherProvider
import com.jj.templateproject.domain.google.TemplateRepository
import kotlinx.coroutines.withContext

class DefaultTemplateRepository(
    private val templateNetwork: TemplateNetworkApi,
    private val dispatcherProvider: DispatcherProvider,
) : TemplateRepository {

    override suspend fun getGoogleData() = withContext(dispatcherProvider.io) {
        templateNetwork.getGoogleData()
    }

    override suspend fun getGoogleStatus() = withContext(dispatcherProvider.io) {
        templateNetwork.getGoogleStatus()
    }
}

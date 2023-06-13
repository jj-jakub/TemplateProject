package com.jj.templateproject.data.google

import com.jj.templateproject.data.network.network.TemplateNetwork
import com.jj.templateproject.domain.google.TemplateRepository

class DefaultTemplateRepository(
    private val templateNetwork: TemplateNetwork,
) : TemplateRepository {

    override suspend fun getGoogleData() = templateNetwork.getGoogleData()

    override suspend fun getGoogleStatus() = templateNetwork.getGoogleStatus()
}
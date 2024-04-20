package com.jj.templateproject.data.google

import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.utils.toBaseResult
import com.jj.templateproject.domain.google.TemplateRepository

class DefaultTemplateRepository(
    private val templateNetwork: TemplateNetwork,
) : TemplateRepository {

    override suspend fun getGoogleData() = templateNetwork.getGoogleData().toBaseResult()

    override suspend fun getGoogleStatus() = templateNetwork.getGoogleStatus().toBaseResult()
}
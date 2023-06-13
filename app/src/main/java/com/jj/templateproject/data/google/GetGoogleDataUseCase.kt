package com.jj.templateproject.data.google

import com.jj.templateproject.domain.google.TemplateRepository

class GetGoogleDataUseCase(
    private val templateRepository: TemplateRepository,
) {
    suspend operator fun invoke() = templateRepository.getGoogleData()
}
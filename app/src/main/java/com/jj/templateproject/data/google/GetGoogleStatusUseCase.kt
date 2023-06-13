package com.jj.templateproject.data.google

import com.jj.templateproject.domain.google.TemplateRepository

class GetGoogleStatusUseCase(
    private val templateRepository: TemplateRepository,
) {
    suspend operator fun invoke() = templateRepository.getGoogleStatus()
}
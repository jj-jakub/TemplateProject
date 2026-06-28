package com.jj.templateproject.domain.google

class GetGoogleStatusUseCase(
    private val templateRepository: TemplateRepository,
) {
    suspend operator fun invoke() = templateRepository.getGoogleStatus()
}

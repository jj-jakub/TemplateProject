package com.jj.templateproject.domain.google

class GetGoogleDataUseCase(
    private val templateRepository: TemplateRepository,
) {
    suspend operator fun invoke() = templateRepository.getGoogleData()
}

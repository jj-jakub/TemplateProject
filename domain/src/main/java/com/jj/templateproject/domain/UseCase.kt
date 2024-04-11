package com.jj.templateproject.domain

interface UseCase<I, O> {
    suspend operator fun invoke(param: I): O
}
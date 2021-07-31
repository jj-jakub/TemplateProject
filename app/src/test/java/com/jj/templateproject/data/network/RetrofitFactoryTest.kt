package com.jj.templateproject.data.network

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RetrofitFactoryTest {

    private lateinit var serviceFactory: RetrofitFactory

    @BeforeEach
    fun setup() {
        serviceFactory = RetrofitFactory()
    }

    @Test
    fun `should create retrofit with proper baseUrl`() {
        val baseUrl = "http://baseurl/"
        val retrofit = serviceFactory.retrofit(baseUrl)

        assertEquals(baseUrl, retrofit.baseUrl().toString())
    }
}
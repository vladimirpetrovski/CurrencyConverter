package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ClearCacheUseCaseTest {

    private val repo: RatesRepository = mock(RatesRepository::class.java)

    private val useCase = ClearCacheUseCase(repo)

    @Test
    fun `clear cache use case should call repo clear`() {
        // When
        useCase()

        // Then
        verify(repo).clear()
    }
}

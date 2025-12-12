package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.Location
import com.example.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SearchLocationsUseCase Tests")
class SearchLocationsUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: SearchLocationsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = SearchLocationsUseCase(repository)
    }

    @Nested
    @DisplayName("When invoking use case")
    inner class InvokeTests {

        @Test
        @DisplayName("should return empty list for blank query")
        fun `invoke with blank query returns empty list`() = runTest {
        val result = useCase.invoke("")

            assertTrue(result.isSuccess)
            assertEquals(emptyList<Location>(), result.getOrNull())
        }

        @Test
        @DisplayName("should call repository for non-blank query")
        fun `invoke with non-blank query calls repository`() = runTest {
        val query = "Bogotá"
        val expectedLocations = listOf(
            Location(id = 1, name = "Bogotá", country = "Colombia")
        )

        coEvery { repository.searchLocations(query) } returns Result.success(expectedLocations)

        val result = useCase.invoke(query)

            assertTrue(result.isSuccess)
            assertEquals(expectedLocations, result.getOrNull())
        }

        @Test
        @DisplayName("should return failure when repository fails")
        fun `invoke with repository error returns failure`() = runTest {
        val query = "Bogotá"
        val exception = Exception("Network error")

        coEvery { repository.searchLocations(query) } returns Result.failure(exception)

        val result = useCase.invoke(query)

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }
}

package com.regev.poalim.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.regev.poalim.model.Media
import com.regev.poalim.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainPageViewModelTest {

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()

  private lateinit var viewModel: MainPageViewModel
  private lateinit var mediaRepository: MediaRepository

  @Before
  fun setUp() {
      Dispatchers.setMain(Dispatchers.Unconfined)
      mediaRepository = mockk()
      viewModel = MainPageViewModel(mediaRepository)
  }


  @After
  fun tearDown() {
   Dispatchers.resetMain()
  }

  @Test
  fun `init calls refreshPopularMedia`() = runTest {
   // Arrange
   val movies = listOf(Media(
       id = 1, title = "Movie 1", overview = "some things" , posterPath = "path", voteAverage = 5f))
   val tvShows = listOf(Media(id = 2, title = "TV Show 1", overview = "some stuff" , posterPath = "path2", voteAverage = 6f))
   coEvery { mediaRepository.getPopularMovies() } returns movies
   coEvery { mediaRepository.getPopularTvShows() } returns tvShows


   viewModel = MainPageViewModel(mediaRepository)

   // Assert
   assertEquals(movies, viewModel.popularMovies.value)
   assertEquals(tvShows, viewModel.popularTvShow.value)
   coVerify { mediaRepository.getPopularMovies() }
   coVerify { mediaRepository.getPopularTvShows() }
  }


 }
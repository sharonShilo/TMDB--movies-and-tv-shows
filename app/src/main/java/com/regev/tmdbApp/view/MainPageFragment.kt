package com.regev.tmdbApp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.regev.tmdbApp.R
import com.regev.tmdbApp.model.Media
import com.regev.tmdbApp.ui.components.MediaCard
import com.regev.tmdbApp.ui.theme.TmdbAppTheme
import com.regev.tmdbApp.viewmodel.MainPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPageFragment : Fragment() {

    private val viewModel: MainPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TmdbAppTheme{
                    val currentBackStackEntry by findNavController().currentBackStackEntryFlow.collectAsState(initial = null)
                    LaunchedEffect(currentBackStackEntry?.destination?.route) {
                        viewModel.refreshPopularMedia()
                    }
                    MainPage(viewModel,  onSearchClick =  {
                        findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
                    }, onFavoriteClick = {
                        findNavController().navigate(R.id.action_mainFragment_to_favoritesFragment)
                    }, onItemClick = { media ->
                        val bundle = Bundle().apply {
                            putParcelable("media", media)
                        }
                        findNavController().navigate(R.id.action_mainFragment_to_DetailsFragment, bundle)
                    }
                    )
                }
            }
        }
    }
}


@Composable
fun MainPage(
    viewModel: MainPageViewModel,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onItemClick: (Media) -> Unit
) {
    val preImageUrl = "https://image.tmdb.org/t/p/w500"
    val popularMovies = viewModel.popularMovies.collectAsState().value
    val popularTvShows = viewModel.popularTvShow.collectAsState().value


    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = onFavoriteClick) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorites")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text(stringResource(R.string.search_query)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
                    .clickable { onSearchClick() },
                readOnly = true,
                enabled = false
            )

            Text(stringResource(R.string.popular_tv_show), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularTvShows) { tvShow ->
                    MediaCard(
                        imageUrl = tvShow.cachedPosterFullPath ?: tvShow.posterPath?.let{ "$preImageUrl$it"},
                        title = tvShow.title,
                        rating = tvShow.voteAverage.toString(),
                        isFavorite = tvShow.isFavorite,
                        onClick = { onItemClick(tvShow) },
                        favoriteClick = { viewModel.toggleFavorite(tvShow) }
                    )
                }
            }

            Text(stringResource(R.string.popular_movie), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularMovies) { movie ->
                    MediaCard(
                        imageUrl = movie.cachedPosterFullPath ?: movie.posterPath?.let{ "$preImageUrl$it"},
                        title = movie.title,
                        rating = movie.voteAverage.toString(),
                        isFavorite = movie.isFavorite,
                        onClick = { onItemClick(movie) },
                        favoriteClick = { viewModel.toggleFavorite(movie) },
                    )
                }
            }
        }
    }
}




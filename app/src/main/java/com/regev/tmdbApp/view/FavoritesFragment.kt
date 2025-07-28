package com.regev.tmdbApp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.regev.tmdbApp.R
import com.regev.tmdbApp.model.Media
import com.regev.tmdbApp.ui.components.MediaCard
import com.regev.tmdbApp.ui.theme.TmdbAppTheme
import com.regev.tmdbApp.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TmdbAppTheme {
                    FavoriteScreen(
                        viewModel.favorites.collectAsState().value,
                        onItemClick = { media ->
                            val bundle = Bundle().apply {
                                putParcelable("media", media)
                            }
                            findNavController().navigate(R.id.action_favoritesFragment_to_DetailsFragment, bundle)
                        },
                        onClose = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    favoritesMedia: List<Media>,
    onItemClick: (media: Media) -> Unit,
    onClose: () -> Unit
) {
    val preImageUrl = "https://image.tmdb.org/t/p/w500"

    ModalBottomSheet(
        onDismissRequest = onClose,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoritesMedia) { item ->
                    MediaCard(
                        imageUrl = item.cachedPosterFullPath ?: item.posterPath?.let{ "$preImageUrl$it"},
                        title = item.title,
                        rating = item.voteAverage.toString(),
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}
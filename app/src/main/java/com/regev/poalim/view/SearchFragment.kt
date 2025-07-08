package com.regev.poalim.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.regev.poalim.R
import com.regev.poalim.model.Media
import com.regev.poalim.ui.components.MediaCard
import com.regev.poalim.ui.theme.PoalimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PoalimTheme {
                    SearchScreen(
                        searchQuery = stringResource(R.string.search_query),
                        onQueryChange = { viewModel.searchMedia(it) },
                        searchResults = viewModel.searchResults.collectAsState().value ,
                        onItemClick = { media ->
                            val bundle = Bundle().apply {
                            putParcelable("media", media)
                        }
                            findNavController().navigate(R.id.action_searchFragment_to_DetailsFragment, bundle)
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
fun SearchScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Media>,
    onItemClick: (media: Media) -> Unit,
    onClose: () -> Unit
) {
    val preImageUrl = "https://image.tmdb.org/t/p/w500"
    val text = remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onClose
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = text.value,
                onValueChange = { newText ->
                    text.value = newText
                    onQueryChange(newText)
                                },
                placeholder = {
                    Text(searchQuery, style = TextStyle(fontSize = 16.sp))
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { item ->
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
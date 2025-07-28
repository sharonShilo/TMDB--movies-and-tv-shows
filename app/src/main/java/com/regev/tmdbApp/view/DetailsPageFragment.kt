package com.regev.tmdbApp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil3.compose.AsyncImage
import com.regev.tmdbApp.R
import com.regev.tmdbApp.model.Media
import com.regev.tmdbApp.ui.theme.TmdbAppTheme
import com.regev.tmdbApp.viewmodel.DetailsPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class DetailsPageFragment : Fragment() {

    private val viewModel: DetailsPageViewModel by viewModels()
    private val args: DetailsPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        args.media?.let { viewModel.getMediaDetails(it) }
        return ComposeView(requireContext()).apply {
            setContent {
                TmdbAppTheme {
                    DetailsScreen(
                        media = args.media,
                        videoUrl = viewModel.trailerUrl.collectAsState().value,
                        onBackClick = { findNavController().popBackStack() },
                        onFavoriteToggle = { viewModel.toggleFavorite(media = it) },
                        onRequestMediaDetails = {
                            args.media?.let { viewModel.getMediaDetails(it) }
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    media: Media?,
    videoUrl: String?,
    onBackClick: () -> Unit,
    onFavoriteToggle: (media: Media) -> Unit,
    onRequestMediaDetails: () -> Unit
) {
    val preImageUrl = "https://image.tmdb.org/t/p/w500"

    LaunchedEffect(Unit) {
        onRequestMediaDetails()
    }

    val context = LocalContext.current

    val shareText = remember(media) {
        buildString {
            append("Check out \"${media?.title}\"!")
            media?.mediaType?.let { append("\n\nmediaType: $it") }
            media?.overview?.let { append("\n\n$it") }
            media?.popularity?.let { append("\n\npopularity: $it") }
            media?.voteAverage?.let { append("\n\nvoteAverage: $it") }
        }
    }
    val title by remember { mutableStateOf(media?.title ?: "") }
    val imageUrl by remember { mutableStateOf(media?.cachedPosterFullPath ?: media?.posterPath?.let{ "$preImageUrl$it"}) }
    val rate by remember { mutableStateOf(media?.voteAverage ?: "") }
    val overview by remember { mutableStateOf(media?.overview ?: "") }
    val popularity by remember { mutableStateOf(media?.popularity ?: "") }
    var isFavorite by remember { mutableStateOf(media?.isFavorite ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style =  MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color(0xFF6200EE))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share via")
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                modifier = Modifier.padding(16.dp)
            )
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = stringResource(R.string.rate),
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${stringResource(R.string.rate)}: $rate",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = popularity,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = overview,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(16.dp)
            )

            if (!videoUrl.isNullOrEmpty()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.play_trailer))
                }
            }

            Button(
                onClick = {
                    isFavorite = !isFavorite
                    media?.let { onFavoriteToggle(it) }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = if (isFavorite) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite),
                )
            }
        }
    }
}
package com.example.marvelapp.presentation.fragment.characters

import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.marvelapp.R
import com.example.marvelapp.databinding.FragmentCharactersBinding
import com.example.marvelapp.framework.imageloader.ImageLoader
import com.example.marvelapp.presentation.fragment.BaseFragment
import com.example.marvelapp.presentation.fragment.detail.DetailViewArg
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

class CharactersFragment : BaseFragment<FragmentCharactersBinding>() {

    override fun getViewBinding(): FragmentCharactersBinding =
        FragmentCharactersBinding.inflate(layoutInflater)

    private val viewModel: CharactersViewModel by viewModel()

    private val imageLoader: ImageLoader by inject()

    private val charactersAdapter: CharactersAdapter by lazy {
        CharactersAdapter(imageLoader) { character, view ->
            val extras = FragmentNavigatorExtras(
                view to character.name
            )
            val directions = CharactersFragmentDirections
                .actionCharactersFragmentToDetailFragment(
                    character.name,
                    DetailViewArg(character.id, character.name, character.imageUrl)
                )
            findNavController().navigate(directions, extras)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCharactersAdapter()
        loadCharactersAndObserverUiState()
        observerInitialLoadState()
    }

    override fun showActionBarOptionMenu(): Boolean = TRUE

    private fun initCharactersAdapter() {
        postponeEnterTransition()
        binding.recyclerCharacters.run {
            setHasFixedSize(true)
            adapter = charactersAdapter.withLoadStateFooter(
                footer = CharactersLoadStateAdapter(
                    charactersAdapter::retry
                )
            )
            viewTreeObserver.addOnDrawListener {
                startPostponedEnterTransition()
                TRUE
            }
        }
    }

    private fun loadCharactersAndObserverUiState() {
        viewModel.state.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CharactersViewModel.UiState.SearchResult -> {
                    charactersAdapter.submitData(viewLifecycleOwner.lifecycle, uiState.data)
                }
            }
        }
        viewModel.searchCharacters()
    }

    private fun observerInitialLoadState() {
        lifecycleScope.launch {
            charactersAdapter.loadStateFlow.collectLatest { loadState ->
                binding.flipperCharacters.displayedChild = when (loadState.refresh) {
                    is LoadState.Loading -> {
                        setUiState(TRUE, FALSE, FALSE, R.color.character_background_status_loading)
                        FLIPPER_CHILD_LOADING
                    }
                    is LoadState.NotLoading -> {
                        setUiState(FALSE, TRUE, TRUE, R.color.character_background_status)
                        FLIPPER_CHILD_CHARACTER
                    }
                    is LoadState.Error -> {
                        setUiState(FALSE, FALSE, FALSE, R.color.character_background_status_error)
                        binding.includeViewCharactersErrorState.buttonRetry.setOnClickListener {
                            charactersAdapter.retry()
                        }
                        FLIPPER_CHILD_ERROR
                    }
                }
            }
        }
    }

    private fun setUiState(
        shimmer: Boolean,
        toolbar: Boolean,
        menuNav: Boolean,
        @ColorRes color: Int
    ) {
        setShimmerVisibility(shimmer)
        showToolbar(toolbar)
        showMenuNavigation(menuNav)
        setColorStatusBarAndNavigation(color)
    }

    private fun setShimmerVisibility(visibility: Boolean) {
        binding.includeViewCharactersLoadingState.shimmerCharacter.run {
            isVisible = visibility
            if (visibility) {
                startShimmer()
            } else stopShimmer()
        }
    }

    companion object {
        private const val FLIPPER_CHILD_LOADING = 0
        private const val FLIPPER_CHILD_CHARACTER = 1
        private const val FLIPPER_CHILD_ERROR = 2
    }
}
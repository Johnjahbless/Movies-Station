package june.com.viewModelFactory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import june.com.repositories.MovieDetailsRepository
import june.com.viewmodels.MovieDetailsViewModel

/**
 * Created by Kashish on 15-08-2018.
 */
class ViewModelDetailFactory(private val repository: MovieDetailsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
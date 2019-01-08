package june.com.viewModelFactory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import june.com.repositories.SearchRepository
import june.com.viewmodels.SearchViewModel

/**
 * Created by Kashish on 13-08-2018.
 */
class ViewModelSearchFactory(private val repository: SearchRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
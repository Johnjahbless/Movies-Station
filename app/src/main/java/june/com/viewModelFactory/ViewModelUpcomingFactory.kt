package june.com.viewModelFactory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import june.com.repositories.UpcomingRepository
import june.com.viewmodels.UpcomingViewModel

/**
 * Created by Kashish on 14-08-2018.
 */
class ViewModelUpcomingFactory(private val repository: UpcomingRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpcomingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UpcomingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
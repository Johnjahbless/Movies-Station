package june.com.repositories

import android.arch.paging.LivePagedListBuilder
import june.com.boundaryCallbacks.TopRatedBoundaryCallbacks
import june.com.database.DatabaseResults.TopRatedResults
import june.com.database.LocalCache.TopRatedLocalCache
import june.com.network.NetworkService

/**
 * Created by Kashish on 14-08-2018.
 */
class TopRatedRepository(
        private val service: NetworkService,
        private val topRatedCache: TopRatedLocalCache
) {

    fun topRated(region: String): TopRatedResults {

        val dataSourceFactory = topRatedCache.getAllTopRated()

        val boundaryCallback = TopRatedBoundaryCallbacks(region, service, topRatedCache)
        val networkErrors = boundaryCallback.networkErrors
        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()
        return TopRatedResults(data, networkErrors)
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val DATABASE_PAGE_SIZE = 60
    }


}
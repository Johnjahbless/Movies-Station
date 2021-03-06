package june.com.requestmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import june.com.models.MovieVideo

/**
 * Created by Kashish on 12-08-2018.
 */
class MovieVideosRequest {
    @SerializedName("results")
    @Expose
    var videos: List<MovieVideo>? = null
}
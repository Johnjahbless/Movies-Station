package june.com.requestmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import june.com.models.Cast
import june.com.models.Crew

/**
 * Created by Kashish on 13-08-2018.
 */
class MovieCreditRequest {
    @SerializedName("cast")
    @Expose()
    var castResult: List<Cast>? = null

    @SerializedName("crew")
    @Expose()
    var crewResult: List<Crew>? = null
}
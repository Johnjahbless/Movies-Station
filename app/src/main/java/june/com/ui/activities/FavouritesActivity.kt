package june.com.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import june.com.R
import june.com.adapters.FavouritesAdapter
import june.com.database.Entities.FavouritesEntry
import june.com.interfaces.OnMovieClickListener
import june.com.models.Movie
import june.com.viewmodels.FavouritesViewModel
import june.com.database.AppExecutors
import android.support.v7.widget.helper.ItemTouchHelper
import com.google.ads.mediation.admob.AdMobAdapter
import june.com.database.AppDatabase
import com.google.android.gms.ads.*
import io.fabric.sdk.android.services.settings.IconRequest.build


class FavouritesActivity : AppCompatActivity(), OnMovieClickListener, SharedPreferences.OnSharedPreferenceChangeListener  {

    private val GRID_COLUMNS_PORTRAIT = 1
    private val GRID_COLUMNS_LANDSCAPE = 2
    private val TAG: String = SimilarMoviesActivity::class.simpleName.toString()

    lateinit var mFavouriteAdapter: FavouritesAdapter
    private lateinit var mFavouriteRecyclerView : RecyclerView
    private lateinit var mGridLayoutManager: GridLayoutManager

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var favouriteviewModel: FavouritesViewModel

    //Database
    private lateinit var mDatabase: AppDatabase

    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView
    //Toolbar
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                ,resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeDark)
        } else{
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        initViews()
        setToolbar()
        initFavouriteRecyclerView()
        fetchFavouriteMovie()
        removeFavouriteOnSwipe()
        val extras = Bundle()

        extras.putString("max_ad_content_rating", "G")
        MobileAds.initialize(this,
                "ca-app-pub-7446083837533381~6745788204")
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
        mAdView.loadAd(adRequest)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstetial_ad)
        mInterstitialAd.loadAd(AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build())
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
            }


    private fun initFavouriteRecyclerView(){
        configureRecyclerAdapter(resources.configuration.orientation)
        mFavouriteAdapter = FavouritesAdapter(this,mSharedPreferences)
        mFavouriteRecyclerView.setAdapter(mFavouriteAdapter)
    }

    private fun initViews(){
        mToolbar = findViewById(R.id.activity_favourites_toolbar)
        mFavouriteRecyclerView = findViewById(R.id.activity_favourites_movies_recycler_view)
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
        mDatabase = AppDatabase.getInstance(applicationContext)

        favouriteviewModel = ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
    }

    private fun fetchFavouriteMovie(){

        favouriteviewModel.getMovies().observe(this, object : Observer<MutableList<FavouritesEntry>>{
            override fun onChanged(t: MutableList<FavouritesEntry>?) {
                mFavouriteAdapter.submitList(t!!)
            }
        })
        Log.d("FavouritesViewModelTAG","Retreiving updates from livedata")
    }

    private fun setToolbar(){
        mToolbar.title = "Favourites"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun removeFavouriteOnSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // Called when a user swipes left or right on a ViewHolder
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute {
                    val position = viewHolder.adapterPosition
                    mDatabase.favouritesDao().deleteFavourite(mFavouriteAdapter.getMovie(position)!!)
                }
            }
        }).attachToRecyclerView(mFavouriteRecyclerView)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id == android.R.id.home) {
            onBackPressed()
            val extras = Bundle()

            extras.putString("max_ad_content_rating", "G")
            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = getString(R.string.admob_interstetial_ad)
            mInterstitialAd.loadAd(AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build())
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        configureRecyclerAdapter(newConfig!!.orientation)
    }

    private fun configureRecyclerAdapter(orientation: Int) {
        val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        mGridLayoutManager = GridLayoutManager(this, if (isPortrait) GRID_COLUMNS_PORTRAIT else GRID_COLUMNS_LANDSCAPE)
        mFavouriteRecyclerView.setLayoutManager(mGridLayoutManager)
    }

    override fun onMovieClickListener(movie: Movie) {
        val extras = Bundle()

        extras.putString("max_ad_content_rating", "G")
        val detailIntent = Intent(this, DetailActivity::class.java)
        detailIntent.putExtra("movie",movie)
        startActivity(detailIntent)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstetial_ad)
        mInterstitialAd.loadAd(AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build())
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    private fun restartActivity(){
        this.recreate()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key.equals(getString(R.string.pref_night_mode_key)))
            restartActivity()
        }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}


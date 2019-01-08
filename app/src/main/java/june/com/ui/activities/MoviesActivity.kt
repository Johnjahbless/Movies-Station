package june.com.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import june.com.R
import june.com.adapters.MovieViewPagerAdapter
import june.com.ui.fragments.*
import java.util.*
import kotlin.jvm.java
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.activity_movies.*


class MoviesActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mViewPager : ViewPager
    private lateinit var mTabLayout : TabLayout
    private lateinit var mToolBar : Toolbar
    private lateinit var mSharedPreferences: SharedPreferences

    //fragments
    internal lateinit var mUpcomingMoviesFragment   : UpcomingMoviesFragment
    internal lateinit var mTopRatedMoviesFragment   : TopRatedMoviesFragment
    internal lateinit var mPopularMoviesFragment    : PopularMoviesFragment
    internal lateinit var mNowShowingMoviesFragment : NowShowingMoviesFragment
    internal lateinit var mMovieViewPagerAdapter    : MovieViewPagerAdapter
    internal lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (mSharedPreferences.getBoolean(getString(R.string.pref_night_mode_key)
                        , resources.getBoolean(R.bool.pref_night_mode_default_value))) {
            setTheme(R.style.AppThemeDark)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        initViews()
        setupToolBar()
        setupTabLayout()
        setupViewPager()

    }
    override fun onBackPressed() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_ad1)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
        if (mViewPager.currentItem == 0) super.onBackPressed()
        else mViewPager.setCurrentItem(mViewPager.currentItem-1)
    }
    private fun initViews(){
        mToolBar = findViewById(R.id.activity_movies_toolbar)
        mViewPager = findViewById(R.id.activity_movies_view_pager)
        mTabLayout = findViewById(R.id.activity_movies_tab_layout)

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    private fun setupToolBar(){
        mToolBar.title = "Movies"
        setSupportActionBar(mToolBar)
    }
    private fun setupViewPager() {
        mMovieViewPagerAdapter = MovieViewPagerAdapter(supportFragmentManager)
        mUpcomingMoviesFragment = UpcomingMoviesFragment()
        mTopRatedMoviesFragment = TopRatedMoviesFragment()
        mPopularMoviesFragment = PopularMoviesFragment()
        mNowShowingMoviesFragment = NowShowingMoviesFragment()

        mMovieViewPagerAdapter.addFragment(mNowShowingMoviesFragment,"Now Showing")
        mMovieViewPagerAdapter.addFragment(mUpcomingMoviesFragment, "Upcoming")
        mMovieViewPagerAdapter.addFragment(mPopularMoviesFragment, "Popular")
        mMovieViewPagerAdapter.addFragment(mTopRatedMoviesFragment, "Top Rated")
        mViewPager.adapter = mMovieViewPagerAdapter

        mViewPager.offscreenPageLimit = 4
    }
    private fun setupTabLayout(){
        mTabLayout.setupWithViewPager(mViewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.action_settings -> {
                val settingsIntent: Intent = Intent(this,SettingsActivity::class.java)
                mInterstitialAd = InterstitialAd(this)
                mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_ad1)
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                startActivity(settingsIntent)
            }

            R.id.action_favourite -> {
                val favouriteIntent: Intent = Intent(this,FavouritesActivity::class.java)
                mInterstitialAd = InterstitialAd(this)
                mInterstitialAd.adUnitId = "ca-app-pub-6317011955622736/3186274653"
                val adRequest = AdRequest.Builder().build()
                mInterstitialAd.loadAd(adRequest)
                mInterstitialAd.adListener = object : AdListener() {
                    @Override
                    public override fun onAdLoaded() {
                        super.onAdLoaded()
                    }
                }
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                startActivity(favouriteIntent)
            }

            R.id.action_search -> {
                val searchIntent: Intent = Intent(this,SearchActivity::class.java)
                mInterstitialAd = InterstitialAd(this)
                mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_ad1)
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
                startActivity(searchIntent)
            }
            R.id.action_rate -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=june.com")
                startActivity(intent)


            }
            R.id.action_check -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=june.com")
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun restartActivity(){
        this.recreate()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(key.equals(getString(R.string.pref_night_mode_key))){
            restartActivity()
        }
    }

    override fun onStart() {
            super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

}

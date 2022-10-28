package com.andihasan7.restaurantreviewidc2

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.andihasan7.restaurantreviewidc2.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.restaurant.observe(this, { restaurant ->
            setRestaurantData(restaurant)
        })

        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        mainViewModel.listReview.observe(this, { consumerReviews ->
            setReviewData(consumerReviews)
        })

        mainViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        // aksi/ event klik kirim
        binding.btnSend.setOnClickListener { view ->
            mainViewModel.postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // mengobserve variable snackbarText dan menampilkan dgn Snackbar
        mainViewModel.snackbarText.observe(this, {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(
                    window.decorView.rootView,
                    snackBarText,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setRestaurantData(restaurant: Restaurant) {
        binding.tvTitle.text = restaurant.name
        binding.tvDescription.text = restaurant.description
        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        val listReview = consumerReviews.map {
            "${it.review}\n- ${it.name}"
        }
        val adapter = ReviewAdapter(listReview)
        binding.rvReview.adapter = adapter
        binding.edReview.setText("")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
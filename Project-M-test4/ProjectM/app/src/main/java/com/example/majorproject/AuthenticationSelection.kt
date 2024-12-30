package com.example.majorproject

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Scroller
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.adapters.ImageSliderAdapter
import com.example.majorproject.adapters.SignInSignUpAdapter
import com.example.majorproject.databinding.ActivityAuthenticationSelectionBinding
import com.example.majorproject.util.BlurUtil
import java.util.*
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field
import kotlin.math.min

class AuthenticationSelection : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationSelectionBinding
    private lateinit var authViewPager: ViewPager2
    private lateinit var fragmentManager: FragmentManager
    private var autoSlidePaused: Boolean = false
    private var userHasInteracted = false
    private var imageIndex = 0
    private val autoSlideInterval: Long = 5000L
    private var autoSlideTask: TimerTask? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timer = Timer()
    private var images: MutableList<Int> = mutableListOf(
        R.drawable.model_img_1,
        R.drawable.model_img_2,
        R.drawable.model_img_3,
        R.drawable.model_img_4
    )
    private var imagesSize=images.size
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewPager = binding.authenticationViewPager
        fragmentManager = supportFragmentManager

        val fragmentAdapter = SignInSignUpAdapter(fragmentManager, lifecycle)
        authViewPager.adapter = fragmentAdapter

        // Setup the image slider
        imageSliderAdapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = imageSliderAdapter

        startAutoSlide()

        binding.viewPager.setUserInputEnabled(true) // Enable swipe initially

        // Set OnClickListener for ViewPager2
        binding.viewPager.setOnClickListener {
            Log.d("check", "ViewPager2 clicked")
            // Perform any action you want here
        }

        binding.signInButton.setOnClickListener { handleButtonClick() }
        binding.signUpButton.setOnClickListener { handleButtonClick() }
        binding.backgroundImageView.setOnClickListener{
            if (binding.authCardView.visibility == View.VISIBLE) {
                slideDownEffect()

            }
        }


    }

    override fun onBackPressed() {
        if (binding.authCardView.visibility == View.VISIBLE) {
            slideDownEffect()

        } else {
            super.onBackPressed()
        }
    }
    private fun slideDownEffect(){
        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)

        slideDownAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

                binding.authCardView.visibility = View.INVISIBLE
                binding.backgroundImageView.visibility=View.INVISIBLE

                resumeAutoSlide()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // No action needed here
            }
        })

        // Start the animation
        binding.authCardView.startAnimation(slideDownAnimation)
    }
    private fun handleButtonClick() {
        pauseAutoSlide()
        val currentPosition = binding.viewPager.currentItem
        val adapter = binding.viewPager.adapter as? ImageSliderAdapter
        val currentImageResId = adapter?.getImageAtPosition(currentPosition)
        pauseAutoSlide()

        if (currentImageResId != null) {
            val backImage=binding.backgroundImageView
            backImage.setImageResource(currentImageResId)
            backImage.visibility = View.VISIBLE
            BlurUtil.applyBlur(this, binding.backgroundImageView, 10f)


            val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
            binding.authCardView.visibility = View.VISIBLE
            binding.authCardView.startAnimation(slideUpAnimation)


        }


        // Reset user interaction flag immediately after a button click
        userHasInteracted = true
    }

    private fun getNextImage(imagePosition: Int) {
        Log.d("autoslide", "ImagePosition: $imagePosition")
        imageIndex = (imagePosition + 1)

        // Check if we need to load more images
        if (imageIndex >= imagesSize) {
            val newImages = listOf(
                R.drawable.model_img_1,
                R.drawable.model_img_2,
                R.drawable.model_img_3,
                R.drawable.model_img_4
            )
            Log.d("autoslide", "ImageSize: $imagesSize")

            // Update the adapter with new images
            imageSliderAdapter.addImages(images)

            imagesSize += newImages.size // Update imagesSize accordingly
        }

        // Set the current item to the next image
        binding.viewPager.currentItem = imageIndex
    }


    private fun startAutoSlide() {
        autoSlideTask?.cancel()
        autoSlideTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    if (userHasInteracted) {
                        pauseAutoSlide()
                    } else {

                        val imagePosition = binding.viewPager.currentItem
                        getNextImage(imagePosition)
                        val nextPage = (binding.viewPager.currentItem + 1) % images.size
                        binding.viewPager.smoothScrollWithAnimation(nextPage, 1000L)// Now this will handle everything
                        Log.d("autoSlide", "Auto-slide to image index $imageIndex")
                    }
                }
            }
        }
        timer.schedule(autoSlideTask, autoSlideInterval, autoSlideInterval)
    }


    private fun pauseAutoSlide() {
        autoSlideTask?.cancel()
        autoSlidePaused = true
    }

    private fun resumeAutoSlide() {
        userHasInteracted = false
        autoSlidePaused = false
        startAutoSlide()
    }

    private fun checkForUserInteraction() {
        if (!userHasInteracted) {
            Log.d("check", "Resuming auto-slide")
            resumeAutoSlide() // If no interaction, resume auto-slide
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        autoSlideTask?.cancel()
    }
}
private val handler = Handler(Looper.getMainLooper())

fun ViewPager2.smoothScrollWithAnimation(nextPage: Int, duration: Long = 1000L) {
    val currentView = (getChildAt(0) as? RecyclerView)?.findViewHolderForAdapterPosition(currentItem)?.itemView
    val nextView = (getChildAt(0) as? RecyclerView)?.findViewHolderForAdapterPosition(nextPage)?.itemView

    if (currentView != null && nextView != null) {
        // Animate the current view sliding out to the left
        val currentOutAnimator = ObjectAnimator.ofFloat(currentView, "translationX", 0f, -width.toFloat()).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Animate the next view sliding in from the right
        val nextInAnimator = ObjectAnimator.ofFloat(nextView, "translationX", width.toFloat(), 0f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Start both animations simultaneously
        currentOutAnimator.start()
        nextInAnimator.start()

        // Listener to update the ViewPager2's current item after animation completes
        nextInAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Do nothing
            }

            override fun onAnimationEnd(animation: Animator) {
                currentItem = nextPage // Set next item after animation finishes
            }

            override fun onAnimationCancel(animation: Animator) {
                // Do nothing
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Do nothing
            }
        })
    }
}


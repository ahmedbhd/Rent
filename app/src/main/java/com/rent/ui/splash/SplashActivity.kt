package com.rent.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.global.helper.ViewModelFactory
import com.rent.ui.main.MainActivity
import javax.inject.Inject


class SplashActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    private lateinit var anim: Animation
    private lateinit var gifImageView: ImageView
    private lateinit var layoutView: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val context = this
        gifImageView = findViewById(R.id.imageView2) // Declare an imageView to show the animation.

        val imageViewTarget = DrawableImageViewTarget(gifImageView)

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        getGlide()
            .load(R.drawable.thankyouanimation)
            .placeholder(R.drawable.thankyouanimation)
            .centerCrop()
            .transition(withCrossFade())
            .into(imageViewTarget)

        layoutView = findViewById(R.id.activity_splash)
        anim = loadAnimation(
            applicationContext,
            R.anim.fade_in
        ) // Create the animation.
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                val nextScreen = Intent(this@SplashActivity, MainActivity::class.java)

                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(nextScreen)
                ActivityCompat.finishAffinity(this@SplashActivity)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        layoutView.startAnimation(anim)
    }
}
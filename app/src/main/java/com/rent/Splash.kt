package com.rent

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget


class Splash : AppCompatActivity() {
    private lateinit var anim: Animation
    private lateinit var gifImageView: ImageView
    private lateinit var layoutView: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val context = this
        gifImageView = findViewById(R.id.imageView2) // Declare an imageView to show the animation.

        val imageViewTarget = GlideDrawableImageViewTarget(gifImageView)

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        Glide.with(this)
            .load(R.drawable.thankyouanimation)
            .placeholder(R.drawable.thankyouanimation)
            .centerCrop()
            .crossFade()
            .into(imageViewTarget)

        layoutView = findViewById(R.id.activity_splash)
        anim = loadAnimation(
            applicationContext,
            R.anim.fade_in
        ) // Create the animation.
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                val nextScreen = Intent(this@Splash, MainActivity::class.java)

                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(nextScreen)
                ActivityCompat.finishAffinity(this@Splash)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        layoutView.startAnimation(anim)
    }
}
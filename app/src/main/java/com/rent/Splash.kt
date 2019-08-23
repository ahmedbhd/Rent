package com.rent

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_splash.*
import android.content.Intent
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.ImageView


class Splash : AppCompatActivity() {
    private lateinit var anim: Animation
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val context = this
        imageView = findViewById(R.id.imageView2) // Declare an imageView to show the animation.
        anim = loadAnimation(
            applicationContext,
            R.anim.fade_in
        ) // Create the animation.
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                startActivity(Intent(context, MainActivity::class.java))
                // HomeActivity.class is the activity to go after showing the splash screen.
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        imageView.startAnimation(anim)
    }
}
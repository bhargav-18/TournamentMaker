package com.example.tournamentmaker.splashactivity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.tournamentmaker.R
import com.example.tournamentmaker.authactivity.AuthActivity
import com.example.tournamentmaker.mainactivity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), Animation.AnimationListener {

    private lateinit var rotateAnimation: Animation
    private lateinit var ivImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ivImg = findViewById(R.id.iv_img)
        ivImg.setBackgroundResource(R.drawable.splash)

        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        rotateAnimation.setAnimationListener(this)

        ivImg.startAnimation(rotateAnimation)
    }

    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            Intent(this, AuthActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }
}
package br.cin.ufpe.if1001.taskmanager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import br.cin.ufpe.if1001.taskmanager.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        /*
        * There is a way to implement a splash screen without artificial delay
        * by creation a xml file with the logo as bitmap, and in the MainActivity call setTheme()
        * and pass the style.
        *
        * But that would be really quick, cuz my application doesn't need to start a lot of thing
        * so i decided to make this way to show the splashscreen, since is a academic project.
        *
        * More about the right way to make a splash screen:
        * https://web.archive.org/web/20180526144705/https://plus.google.com/+AndroidDevelopers/posts/Z1Wwainpjhd
        */
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this@SplashScreen, MainActivity::class.java).apply { startActivity(this) }
            finish()
        }, 2000)
    }
}
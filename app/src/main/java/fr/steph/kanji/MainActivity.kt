package fr.steph.kanji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.steph.kanji.network.ConnectivityChecker
import fr.steph.kanji.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ConnectivityChecker.setAppContext(applicationContext)
    }
}
package academy.bangkit.capstone.dietin.ui.main_screen

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.ActivityMainScreenBinding
import academy.bangkit.capstone.dietin.ui.search.RecipeSearchActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenBinding

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)

        bottomNavigation = binding.navView

        binding.fabSearch.setOnClickListener {
            val intent = Intent(this, RecipeSearchActivity::class.java)
            startActivity(intent)
        }
    }
}
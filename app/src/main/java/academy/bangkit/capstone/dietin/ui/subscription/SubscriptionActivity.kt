package academy.bangkit.capstone.dietin.ui.subscription

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import academy.bangkit.capstone.dietin.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySubscriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionPageAdapter = SectionPageAdapter(this)
        binding.vpSubscription.adapter = sectionPageAdapter




    }
}
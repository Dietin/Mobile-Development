package academy.bangkit.capstone.dietin.ui.auth.activity

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.ActivityAuthenticationBinding
import academy.bangkit.capstone.dietin.ui.auth.fragments.login.LoginFragment
import academy.bangkit.capstone.dietin.ui.auth.fragments.register.RegisterFragment
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[AuthenticationViewModel::class.java]
        setContentView(binding.root)

        setFragment("login", false)
    }

    fun setFragment(frg: String, addToBackStack: Boolean = true) {
        /**
         * Fragment animation example: https://developer.android.com/guide/fragments/animate
         */
        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.slide_out // popExit
            )
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        when (frg) {
            "login" -> {
                transaction.replace(binding.root.id, LoginFragment())
                    .commit()
            }
            "register" -> {
                transaction.replace(binding.root.id, RegisterFragment())
                    .commit()
            }
        }
    }
}
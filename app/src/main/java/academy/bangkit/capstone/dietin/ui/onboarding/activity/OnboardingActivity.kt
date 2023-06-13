package academy.bangkit.capstone.dietin.ui.onboarding.activity

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.DataUser
import academy.bangkit.capstone.dietin.databinding.ActivityOnboardingBinding
import academy.bangkit.capstone.dietin.ui.onboarding.fragments.page1.Onboarding1Fragment
import academy.bangkit.capstone.dietin.ui.onboarding.fragments.page2.Onboarding2Fragment
import academy.bangkit.capstone.dietin.ui.onboarding.fragments.page3.Onboarding3Fragment
import academy.bangkit.capstone.dietin.ui.onboarding.fragments.page4.Onboarding4Fragment
import academy.bangkit.capstone.dietin.ui.onboarding.fragments.page5.Onboarding5Fragment
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewModel: OnboardingViewModel
    private lateinit var loader: AlertDialog

    private val pages = listOf(
        Onboarding1Fragment(),
        Onboarding2Fragment(),
        Onboarding3Fragment(),
        Onboarding4Fragment(),
        null
    )
    private var currentPage = 0
    private var isFinishedSetup = false

    lateinit var userData: DataUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[OnboardingViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(this)
        setContentView(binding.root)

        binding.lpiOnboarding.max = pages.size-1
        toNextPage(false)

        binding.btnContinue.setOnClickListener {
            toNextPage()
        }
    }

    private fun setupViewModelBinding() {
//        viewModel.isLoading.observe(this) {
//            if (it) {
//                loader.show()
//            } else {
//                loader.dismiss()
//            }
//        }

        viewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            val user = Utils.getUser(this@OnboardingActivity)
            viewModel.userData.value = DataUser(
                id = 0,
                userId = user?.id ?: 0,
                age = 0,
                weight = 0f,
                height = 0f,
                bmr = 0f,
                activityLevel = 0f,
                gender = -1, // 0 laki2, 1 perempuan
                idealCalories = 0f,
                goal = 0,
                currentWeight = 0f,
                user = user
            )
            userData = viewModel.userData.value!!
        }
    }

    fun toNextPage(addToBackStack: Boolean = true) {
        binding.lpiOnboarding.setProgressCompat(currentPage, true)
        if (currentPage+1 > pages.size-1) {
            setToUploadFragment()
            return
        }

        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.slide_out // popExit
            )
            replace(R.id.fragment_container, pages[currentPage]!!)
            if (addToBackStack) addToBackStack(null)
        }

        currentPage++
    }

    private fun setToUploadFragment() {
        if (isFinishedSetup) {
            return
        }

        isFinishedSetup = true
        // remove all fragment from backstack
        supportFragmentManager.popBackStack(null, 1)

        val currentFragment = Onboarding5Fragment()
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.slide_out // popExit
            )
            replace(R.id.fragment_container, currentFragment)
        }
        uploadUserData()

        binding.btnContinue.apply {
            // animate it sliding down
            animate().translationY(200f).duration = 300
            isClickable = false
            isFocusable = false
        }
    }

    fun uploadUserData() {
        viewModel.uploadUserData()
    }

    private fun toPrevPage() {
        if (isFinishedSetup) {
            return
        }
        binding.lpiOnboarding.setProgressCompat(binding.lpiOnboarding.progress-1, true)
        if (currentPage > 1) {
            supportFragmentManager.popBackStack()
            currentPage--
        }
    }

    fun setButtonContinueState(state: Boolean) {
        binding.btnContinue.isEnabled = state
        if (state) {
            binding.btnContinue.alpha = 1f
        } else {
            binding.btnContinue.alpha = 0.5f
        }
    }

    override fun onBackPressed() {
        if (currentPage == 1) {
            finish()
        } else {
            toPrevPage()
        }
    }
}
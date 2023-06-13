package academy.bangkit.capstone.dietin.ui.onboarding.fragments.page4

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentOnboarding4Binding
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment

class Onboarding4Fragment : Fragment() {

    private var _binding: FragmentOnboarding4Binding? = null
    private val binding get() = _binding!!
    private lateinit var activity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding4Binding.inflate(inflater, container, false)
        activity = getActivity() as OnboardingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup data
        val genders = resources.getStringArray(R.array.genders)
        val dropdownAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, genders)
        binding.actGender.setAdapter(dropdownAdapter)

        binding.actGender.setOnItemClickListener { _, _, position, _ ->
            setGenderImage(position)
            activity.userData.gender = position // 0 laki2, 1 perempuan
            updateButtonContinue()
        }

        // Tinggi on text change
        binding.tilTinggi.editText?.doOnTextChanged { text, _, _, _ ->
            val tinggi = try {
                text.toString().toFloat()
            } catch (e: Exception) {
                0f
            }
            activity.userData.height = tinggi
            updateButtonContinue()
        }

        // Berat on text change
        binding.tilBerat.editText?.doOnTextChanged { text, _, _, _ ->
            val berat = try {
                text.toString().toFloat()
            } catch (e: Exception) {
                0f
            }
            activity.userData.currentWeight = berat
            updateButtonContinue()
        }

        // Umur on text change
        binding.tilUmur.editText?.doOnTextChanged { text, _, _, _ ->
            val umur = try {
                text.toString().toInt()
            } catch (e: Exception) {
                0
            }
            activity.userData.age = umur

            if (umur < 17 && umur != 0) {
                binding.tilUmur.error = getString(R.string.ob4_error_age)
            } else {
                binding.tilUmur.error = null
            }
            updateButtonContinue()
        }

        binding.tilBeratHarapan.editText?.doOnTextChanged { text, _, _, _ ->
            val beratHarapan = try {
                text.toString().toFloat()
            } catch (e: Exception) {
                0f
            }
            when {
                activity.userData.goal == 1 && beratHarapan >= activity.userData.currentWeight -> {
                    binding.tilBeratHarapan.error = getString(R.string.ob4_error_target_weight_high)
                }
                activity.userData.goal == 3 && beratHarapan <= activity.userData.currentWeight -> {
                    binding.tilBeratHarapan.error = getString(R.string.ob4_error_target_weight_low)
                }
                else -> {
                    binding.tilBeratHarapan.error = null
                    activity.userData.weight = beratHarapan
                }
            }
            updateButtonContinue()
        }
    }

    override fun onStart() {
        super.onStart()
        setupData()
    }

    private fun setupData() {
        binding.actGender.setText(when(activity.userData.gender) {
            0 -> "Pria"
            1 -> "Wanita"
            else -> ""
        }, false)
        setGenderImage(activity.userData.gender)
        binding.tilTinggi.editText?.setText(if (activity.userData.height == 0f) {
            ""
        } else {
            activity.userData.height.toString()
        })
        binding.tilBerat.editText?.setText(if (activity.userData.weight == 0f) {
            ""
        } else {
            activity.userData.currentWeight.toString()
        })
        binding.tilUmur.editText?.setText(if (activity.userData.age == 0) {
            ""
        } else {
            activity.userData.age.toString()
        })
        binding.tilBeratHarapan.editText?.setText(if (activity.userData.weight == 0f) {
            ""
        } else {
            activity.userData.weight.toString()
        })
        if (activity.userData.goal == 2) {
            binding.tvBeratHarapanDesc.visibility = View.GONE
            binding.tilBeratHarapan.visibility = View.GONE
        }
        updateButtonContinue()
    }

    private fun updateButtonContinue() {
        if (
            activity.userData.gender != -1 &&
            activity.userData.height != 0f &&
            activity.userData.weight != 0f &&
            activity.userData.age >= 17 &&
            binding.tilBeratHarapan.error == null
        ) {
            activity.setButtonContinueState(true)
        } else {
            activity.setButtonContinueState(false)
        }
    }

    private fun setGenderImage(position: Int) {
        when(position){
            0 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_male_divider, null)
            1 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_female_divider, null)
        }
    }

    override fun onStop() {
        super.onStop()
        // Ini harus diresset di sini, kalau tidak, nanti hanya muncul 1 pilihan di dropdown saat kembali ke fragment ini
        binding.actGender.setText("", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
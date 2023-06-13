package academy.bangkit.capstone.dietin.ui.main_screen.profile.edit

import academy.bangkit.capstone.dietin.databinding.FragmentEditProfileBinding
import academy.bangkit.capstone.dietin.di.BottomSheetHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var bottomSheetListener : BottomSheetHelper? = null

    private var name = ""
    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = arguments?.getString(NAME_FIELD).toString()
        email = arguments?.getString(EMAIL_FIELD).toString()

        binding.inputName.editText?.setText(name)
        binding.inputEmail.editText?.setText(email)
        setupListener()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setupListener(){
        binding.btnChangeProfile.setOnClickListener {
            bottomSheetListener?.dataChange(
                name = binding.inputName.editText?.text.toString(),
                email = binding.inputEmail.editText?.text.toString()
            )
            this.dismiss()
        }
    }

    fun setBottomSheetListener(listener : BottomSheetHelper){
        this.bottomSheetListener = listener
    }

    companion object {
        const val TAG = "EditProfileFragment"
        const val NAME_FIELD = "name"
        const val EMAIL_FIELD = "email"
    }

}
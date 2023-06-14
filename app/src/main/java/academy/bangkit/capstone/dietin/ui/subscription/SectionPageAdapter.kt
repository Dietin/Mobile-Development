package academy.bangkit.capstone.dietin.ui.subscription

import academy.bangkit.capstone.dietin.ui.subscription.page.Subscription1Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionPageAdapter(
    activity : AppCompatActivity
) : FragmentStateAdapter(activity) {


    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        var currentFragment : Fragment? = null

        when(position){
            0 -> currentFragment =  Subscription1Fragment()
        }

        return currentFragment as Fragment
    }


}
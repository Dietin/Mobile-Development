package academy.bangkit.capstone.dietin.helper

import academy.bangkit.capstone.dietin.R
import android.animation.ObjectAnimator
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressBarHelper {

    companion object {
        fun setProgress(
            progressBar: CircularProgressIndicator,
            percent : Float
        ){
            progressBar.max = 1000

            val color = when(percent){
                in 0f .. 25f -> R.color.danger
                in 25f .. 75f -> R.color.warning
                in 75f .. 100f -> R.color.success
                else -> R.color.overflow
            }
            progressBar.setIndicatorColor(ContextCompat.getColor(progressBar.context, color))

            val percentValue = (percent * 10).toInt()
            val animation : ObjectAnimator = ObjectAnimator.ofInt(progressBar, "progress", percentValue)

            val duration = when(percentValue){
                in 0..300 -> 400
                in 301 .. 600 -> 600
                in 601 .. 900 -> 800
                else -> 1000
            }.toLong()
            animation.duration = duration
            animation.start()

        }
    }

}
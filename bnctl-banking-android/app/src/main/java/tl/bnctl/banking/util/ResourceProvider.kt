package tl.bnctl.banking.util

import android.content.Context
import androidx.annotation.Dimension

/**
 * Class adapted from the PostBank mWallet app
 */
class ResourceProvider {

    companion object {
        @Dimension
        fun getPixelValue(context: Context, dimenId: Int): Int {
            val resources = context.resources
            val scale = resources.displayMetrics.density
            val value = resources.getDimension(dimenId)
            return (scale * value * 0.5f).toInt()
        }

        @Dimension
        fun getPixelValue(context: Context, value: Float): Int {
            val resources = context.resources
            val scale = resources.displayMetrics.density
            return (scale * value * 0.5f).toInt()
        }
    }
}
package otus.homework.reactivecats

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {
    fun getString(@StringRes id: Int) = context.getString(id)
}

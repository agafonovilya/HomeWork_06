package otus.homework.reactivecats

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {
    fun getString(@StringRes id: Int) = context.resources.getString(id)
    fun getStringArray(@ArrayRes id: Int): Array<String> = context.resources.getStringArray(id)
}

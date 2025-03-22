package fr.steph.kanji.core.util.extension

import android.os.Build
import android.os.Bundle
import java.io.Serializable

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Serializable> Bundle.getSerializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}
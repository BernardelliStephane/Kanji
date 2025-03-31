package fr.steph.kanji.core.util.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator

/**
 * Extension function for [Fragment] that performs the [NavController.navigateUp] action.
 *
 * This function navigates to the previous destination in the navigation stack, as defined by the
 * [NavController] associated with the fragment's view.
 */
fun Fragment.navigateUp() {
    Navigation.findNavController(requireView()).navigateUp()
}

/**
 * Extension function for [Fragment] that safely navigates to a new destination using [NavDirections].
 *
 * This function ensures the navigation happens only if the current destination is valid and the action exists,
 * avoiding potential issues if the navigation is not possible (e.g., navigating from an invalid state).
 * It also supports passing [FragmentNavigator.Extras] to customize the navigation behavior.
 *
 * @see [NavController.navigate] for more information about navigation.
 */
fun Fragment.safeNavigate(direction: NavDirections, extras: FragmentNavigator.Extras? = null) {
    Navigation.findNavController(requireView()).apply {
        currentDestination?.getAction(direction.actionId)?.run {
            extras?.let { navigate(direction, extras) } ?: navigate(direction)
        }
    }
}

/**
 * Returns a quantity-based string resource, with a specific resource for the zero case.
 *
 * @param resId The resource ID of the plurals string for non-zero quantities.
 * @param zeroResId The resource ID of the string used when the quantity is zero.
 * @param quantity The quantity to determine which string to use.
 * @return The appropriate string based on the quantity.
 */
fun Fragment.getQuantityStringZero(resId: Int, zeroResId: Int, quantity: Int): String {
    return if (quantity == 0)
        resources.getString(zeroResId)
    else
        resources.getQuantityString(resId, quantity, quantity)
}

/**
 * Shows a toast message with the provided [message].
 *
 * @param message The message to be displayed in the toast.
 */
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

/**
 * Shows a toast message with the provided string resource [resId].
 *
 * @param resId The resource ID of the string message to be displayed in the toast.
 */
fun Fragment.showToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()
}
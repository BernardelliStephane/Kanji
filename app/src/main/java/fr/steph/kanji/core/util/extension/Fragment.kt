package fr.steph.kanji.core.util.extension

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
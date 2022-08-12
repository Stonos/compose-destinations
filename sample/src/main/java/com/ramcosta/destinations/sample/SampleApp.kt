package com.ramcosta.destinations.sample

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.destinations.sample.core.viewmodel.activityViewModel
import com.ramcosta.destinations.sample.destinations.Destination
import com.ramcosta.destinations.sample.destinations.LoginScreenDestination
import com.ramcosta.destinations.sample.ui.composables.BottomBar
import com.ramcosta.destinations.sample.ui.composables.SampleScaffold
import com.ramcosta.destinations.sample.ui.composables.TopBar

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun SampleApp() {
    val engine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopStart,
//        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING, //default `rootDefaultAnimations` means no animations
//        rootDefaultAnimations = RootNavGraphDefaultAnimations(
//            enterTransition = {
//                slideIntoContainer(
//                    AnimatedContentScope.SlideDirection.Start,
//                    animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
//                )
//            },
//            exitTransition = {
//                slideOutOfContainer(
//                    AnimatedContentScope.SlideDirection.Start,
//                    animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
//                )
//            },
//            popEnterTransition = {
//                slideIntoContainer(
//                    AnimatedContentScope.SlideDirection.End,
//                    animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
//                )
//            },
//            popExitTransition = {
//                slideOutOfContainer(
//                    AnimatedContentScope.SlideDirection.End,
//                    animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
//                )
//            }
//        )
//        rootDefaultAnimations = RootNavGraphDefaultAnimations(
//            enterTransition = {
//                slideInHorizontally(
//                    initialOffsetX = {
//                        Log.d("transition", "enterTransition $it")
//                        it
//                    },
//                    animationSpec = tween(3000)
//                )
//            },
//            exitTransition = {
//                slideOutHorizontally(
//                    targetOffsetX = {
//                        Log.d("transition", "exitTransition ${-it}")
//                        -it
//                    },
//                    animationSpec = tween(3000)
//                ) + fadeOut(
//                    animationSpec = tween(3000)
//                )
//            },
//            popEnterTransition = {
//                slideInHorizontally(
//                    initialOffsetX = {
//                        Log.d("transition", "popEnterTransition ${-it}")
//                        -it
//                    },
//                    animationSpec = tween(3000)
//                )
//            },
//            popExitTransition = {
//                slideOutHorizontally(
//                    targetOffsetX = {
//                        Log.d("transition", "popExitTransition $it")
//                        it
//                    },
//                    animationSpec = tween(3000)
//                )
//            }
//        )

        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { defaultEnterTransition(initialState, targetState) },
            exitTransition = { defaultExitTransition(initialState, targetState) },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ),
//        defaultAnimationsForNestedNavGraph = mapOf(
//            NavGraphs.root to NestedNavGraphDefaultAnimations(
//                enterTransition = { slideInHorizontally(animationSpec = tween(2000)) },
//                exitTransition = { slideOutHorizontally(animationSpec = tween(2000)) }
//            )
//        ) // all other nav graphs not specified in this map, will get their animations from the `rootDefaultAnimations` above.
    )
    val navController = engine.rememberNavController()

    val vm = activityViewModel<MainViewModel>()
    // 👇 this avoids a jump in the UI that would happen if we relied only on ShowLoginWhenLoggedOut
    val startRoute = if (!vm.isLoggedIn) LoginScreenDestination else NavGraphs.root.startRoute

    SampleScaffold(
        navController = navController,
        startRoute = startRoute,
        topBar = { dest, backStackEntry ->
            if (dest.shouldShowScaffoldElements) {
                TopBar(dest, backStackEntry)
            }
        },
        bottomBar = {
            if (it.shouldShowScaffoldElements) {
                BottomBar(navController)
            }
        }
    ) {
        DestinationsNavHost(
            engine = engine,
            navController = navController,
            navGraph = NavGraphs.root,
            modifier = Modifier.padding(it),
            startRoute = startRoute
        )

        // Has to be called after calling DestinationsNavHost because only
        // then does NavController have a graph associated that we need for
        // `appCurrentDestinationAsState` method
        ShowLoginWhenLoggedOut(vm, navController)
    }
}

private val Destination.shouldShowScaffoldElements get() = this !is LoginScreenDestination

@Composable
private fun ShowLoginWhenLoggedOut(
    vm: MainViewModel,
    navController: NavHostController
) {
    val currentDestination by navController.appCurrentDestinationAsState()
    val isLoggedIn by vm.isLoggedInFlow.collectAsState()

    if (!isLoggedIn && currentDestination != LoginScreenDestination) {
        // everytime destination changes or logged in state we check
        // if we have to show Login screen and navigate to it if so
        navController.navigate(LoginScreenDestination) {
            launchSingleTop = true
        }
    }
}

private val NavDestination.hostNavGraph: NavDestination?
    get() = hierarchy.firstOrNull()?.parent

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph?.id != targetNavGraph?.id) {
        return fadeIn(tween(DEFAULT_ANIMATION_SPEC_DURATION))
    }
    return fadeIn(tween(DEFAULT_ANIMATION_SPEC_DURATION)) + slideIntoContainer(
        towards = AnimatedContentScope.SlideDirection.Start,
        animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
    )
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph?.id != targetNavGraph?.id) {
        return fadeOut(tween(DEFAULT_ANIMATION_SPEC_DURATION))
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeOut(tween(DEFAULT_ANIMATION_SPEC_DURATION)) + slideOutOfContainer(
        towards = AnimatedContentScope.SlideDirection.Start,
        animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
    )
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultPopEnterTransition(): EnterTransition {
    return fadeIn(tween(DEFAULT_ANIMATION_SPEC_DURATION)) + slideIntoContainer(
        towards = AnimatedContentScope.SlideDirection.End,
        animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
    )
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultPopExitTransition(): ExitTransition {
    return fadeOut(tween(DEFAULT_ANIMATION_SPEC_DURATION)) + slideOutOfContainer(
        towards = AnimatedContentScope.SlideDirection.End,
        animationSpec = tween(DEFAULT_ANIMATION_SPEC_DURATION)
    )
}

private const val DEFAULT_ANIMATION_SPEC_DURATION = 500
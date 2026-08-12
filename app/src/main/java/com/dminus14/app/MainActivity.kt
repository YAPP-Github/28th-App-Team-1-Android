package com.dminus14.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dminus14.app.error.GlobalErrorHost
import com.dminus14.app.interview.InterviewAppLifecycleCoordinator
import com.dminus14.app.modal.GlobalModalHost
import com.dminus14.app.modal.GlobalModalManager
import com.dminus14.app.navigation.AppNavigationState
import com.dminus14.designsystem.theme.HilitTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationState: AppNavigationState

    @Inject
    lateinit var globalModalManager: GlobalModalManager

    @Inject
    lateinit var interviewAppLifecycleCoordinator: InterviewAppLifecycleCoordinator

    override fun onStart() {
        super.onStart()
        interviewAppLifecycleCoordinator.onForeground()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationState.navigator.onExit = ::finishAffinity

        enableEdgeToEdge()
        setContent {
            HilitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = navigationState.navigator.backStack,
                        onBack = navigationState.navigator::goBack,
                        modifier = Modifier.padding(innerPadding),
                        entryDecorators =
                            listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator(),
                            ),
                        entryProvider =
                            entryProvider {
                                navigationState.entryInstallers.forEach { installer -> installer() }
                            },
                    )
                }

                GlobalModalHost(manager = globalModalManager)
                GlobalErrorHost(
                    onExit = ::finishAffinity,
                    onGlobalEventRendered = interviewAppLifecycleCoordinator::acknowledge,
                )
            }
        }
    }
}

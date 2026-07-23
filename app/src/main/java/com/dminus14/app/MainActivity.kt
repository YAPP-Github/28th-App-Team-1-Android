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
import com.dminus14.app.dialog.GlobalDialogHost
import com.dminus14.app.dialog.GlobalModalManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationState.navigator.onExit = {
            // 앱 종료 방법 PM과 상의 후 결정
        }

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

                GlobalDialogHost(manager = globalModalManager)
            }
        }
    }
}

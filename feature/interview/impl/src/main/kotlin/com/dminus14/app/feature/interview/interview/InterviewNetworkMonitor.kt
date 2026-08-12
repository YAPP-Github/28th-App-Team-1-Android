package com.dminus14.app.feature.interview.interview

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class InterviewNetworkMonitor
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val connectivityManager =
            context.getSystemService(ConnectivityManager::class.java)

        @SuppressLint("MissingPermission")
        val isConnected: Flow<Boolean> =
            callbackFlow {
                fun currentConnection(): Boolean =
                    connectivityManager.activeNetwork
                        ?.let(connectivityManager::getNetworkCapabilities)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

                val callback =
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            trySend(currentConnection())
                        }

                        override fun onLost(network: Network) {
                            trySend(currentConnection())
                        }

                        override fun onCapabilitiesChanged(
                            network: Network,
                            networkCapabilities: NetworkCapabilities,
                        ) {
                            trySend(
                                networkCapabilities.hasCapability(
                                    NetworkCapabilities.NET_CAPABILITY_VALIDATED,
                                ),
                            )
                        }
                    }
                trySend(currentConnection())
                connectivityManager.registerDefaultNetworkCallback(callback)
                awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
            }
    }

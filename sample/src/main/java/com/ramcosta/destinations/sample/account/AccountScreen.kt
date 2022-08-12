package com.ramcosta.destinations.sample.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.destinations.sample.AccountNavGraph
import com.ramcosta.destinations.sample.core.viewmodel.viewModel

@AccountNavGraph(start = true)
@Destination
@Composable
fun AccountScreen(
    vm: AccountViewModel = viewModel(),
) {

    Button(onClick = { vm.onLogoutClick() }, modifier = Modifier.fillMaxSize()) {
        Text("Logout")
    }

}
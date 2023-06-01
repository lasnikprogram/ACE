package com.kuhakupixel.atg.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.kuhakupixel.atg.R
import com.kuhakupixel.atg.ui.bottomnav.BottomBar
import com.kuhakupixel.atg.ui.bottomnav.BottomBarMenu
import com.kuhakupixel.atg.ui.bottomnav.BottomNavGraph
import com.kuhakupixel.atg.ui.menu.HomeMenu
import com.kuhakupixel.atg.ui.util.WarningDialog
import com.topjohnwu.superuser.Shell


@Composable
fun MainScreen(askForOverlayPermission: () -> Unit) {
    // =================== Check if root has been granted ===========
    // https://topjohnwu.github.io/libsu/com/topjohnwu/superuser/Shell.html#isAppGrantedRoot()
    val isRootGranted: Boolean? = Shell.isAppGrantedRoot()
    if (isRootGranted != null) {
        // apk not granted root, show warning
        if (!isRootGranted) {
            val showDialog: MutableState<Boolean> = remember { mutableStateOf(true) }
            if (showDialog.value) {
                WarningDialog(msg = "Root not granted, This apk need root in order to work :(",
                    onClose = { showDialog.value = false },
                    onConfirm = {})
            }

        }
    } else {
        // root access can't be determined if [isRootGranted] is null
        val showDialog: MutableState<Boolean> = remember { mutableStateOf(true) }
        if (showDialog.value) {
            WarningDialog(msg = "Cannot determine whether Root has been granted or not :(",
                onClose = { showDialog.value = false },
                onConfirm = {})
        }
    }
    // ==============================================================
    val navController = rememberNavController()
    var globalConf: GlobalConf = GlobalConf(LocalContext.current)
    // ============================ each menu in bottom nav ===================
    val menus = listOf(
        BottomBarMenu(
            route = "Home",
            title = "Home",
            iconId = R.drawable.ic_home,
            content = { HomeMenu(globalConf, askForOverlayPermission = askForOverlayPermission) },
        ),
    )
    // =====================================================
    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                menus = menus
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            BottomNavGraph(navController = navController, menus = menus, startDestinationIndex = 0)
        }
    }
}

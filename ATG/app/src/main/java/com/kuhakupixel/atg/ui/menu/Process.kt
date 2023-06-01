package com.kuhakupixel.atg.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kuhakupixel.atg.R
import com.kuhakupixel.atg.backend.ACE
import com.kuhakupixel.atg.backend.ProcInfo
import com.kuhakupixel.atg.ui.GlobalConf
import com.kuhakupixel.atg.ui.overlay.service.OverlayComposeUI.OverlayManager
import com.kuhakupixel.atg.ui.util.CreateTable

/**
 * which process we are currently attached to?
 * */
private var attachedStatusString: MutableState<String> = mutableStateOf("None")

private fun AttachToProcess(
    ace: ACE?,
    pid: Long,
    onProcessNoExistAnymore: () -> Unit,
    onAttachSuccess: () -> Unit,
    onAttachFailure: (msg: String) -> Unit,
) {

    // check if its still alive
    if (!ace!!.IsPidRunning(pid)) {
        onProcessNoExistAnymore()
        return
    }
    // DeAttach first if we have been attached previously
    if (ace.IsAttached()) {
        ace.DeAttach()
    }
    // attach
    ace.Attach(pid)
    var attachedPid: Long = -1
    try {
        attachedPid = ace.GetAttachedPid()
    } catch (e: Exception) {
        onAttachFailure("Unable to attach to process:  ${e.stackTraceToString()}")
        return
    }
    // final check to see if we are attached
    // to the correct process
    if (attachedPid == pid) {
        onAttachSuccess()
    } else {
        onAttachFailure("Unexpected Error, cannot attach to $pid")
    }

}

@Composable
fun ProcessTable(
    processList: SnapshotStateList<ProcInfo>,
    onProcessSelected: (pid: Long, procName: String) -> Unit,
) {


    CreateTable(
        modifier = Modifier.padding(16.dp),
        colNames = listOf("Pid", "Name"),
        colWeights = listOf(0.3f, 0.7f),
        itemCount = processList.size,
        minEmptyItemCount = 50,
        onRowClicked = { rowIndex: Int ->
            onProcessSelected(
                processList[rowIndex]
                    .GetPidStr()
                    .toLong(),
                processList[rowIndex].GetName(),
            )

        },
        drawCell = { rowIndex: Int, colIndex: Int, cellModifier: Modifier ->
            if (colIndex == 0) {
                Text(text = processList[rowIndex].GetPidStr(), modifier = cellModifier)
            }
            if (colIndex == 1) {
                Text(text = processList[rowIndex].GetName(), modifier = cellModifier)
            }
        }
    )
}

@Composable
private fun _ProcessMenu(
    runningProcState: SnapshotStateList<ProcInfo>,
    onRefreshClicked: () -> Unit,
    onAttach: (pid: Long, procName: String) -> Unit,


    ) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column() {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Selected process: ${attachedStatusString.value}")
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onRefreshClicked, modifier = Modifier.padding(start = 10.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                )
            }

            ProcessTable(
                processList = runningProcState,
                onProcessSelected = onAttach,
            )
        }
    }
}


fun refreshProcList(ace: ACE?, processList: SnapshotStateList<ProcInfo>) {

    // remove old elements
    processList.clear()
    // grab new one and add to the list
    val runningProcs: List<ProcInfo>? = ace!!.ListRunningProc()
    if (runningProcs != null) {
        for (proc in runningProcs)
            processList.add(proc)
    }
}

@Composable
fun ProcessMenu(globalConf: GlobalConf?, overlayManager: OverlayManager?) {
    val ace: ACE? = globalConf?.getAce()
    // list of processes that are gonna be shown
    val currentProcList = remember { SnapshotStateList<ProcInfo>() }
    //
    // initialize the list first
    refreshProcList(ace, currentProcList)
    //
    _ProcessMenu(
        currentProcList,
        onAttach = { pid: Long, procName: String ->
            overlayManager!!.Dialog(
                title = "Attach to ${pid} - ${procName} ? ", text = "",
                onConfirm = {
                    AttachToProcess(
                        ace = ace, pid = pid,
                        onAttachSuccess = {
                            overlayManager.Dialog(
                                title = "Attaching to ${procName} is successful",
                                onConfirm = {},
                                text = "",
                            )
                            attachedStatusString.value = "${pid} - ${procName}"
                        },
                        onProcessNoExistAnymore = {
                            overlayManager.Dialog(
                                title = "Process ${procName} is not running anymore, Can't attach",
                                onConfirm = {},
                                text = "",
                            )
                        },
                        onAttachFailure = { msg: String ->
                            overlayManager.Dialog(
                                title = msg,
                                onConfirm = {},
                                text = "",
                            )
                        },
                    )
                },
            )
        },
        onRefreshClicked = { refreshProcList(ace, currentProcList) }
    )

}

@Preview(showBackground = true)
@Composable
fun PreviewTable() {
    val myList: SnapshotStateList<ProcInfo> = remember {

        mutableStateListOf(
            ProcInfo("1 init"),
            ProcInfo("2 systemd"),
            ProcInfo("3 daemonSomething"),
        )

    }
    ProcessTable(
        myList,
        { pid: Long, procName: String ->

            // do nothing

        },
    )
}

@Composable
@Preview
fun ProcessMenuPreview() {
    ProcessMenu(null, null)
}
package org.grating.recolldroid.ui.screens.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.grating.recolldroid.R
import org.grating.recolldroid.ui.model.RecollDroidViewModel

/**
 * Composable for confirming a requested action that might potentially be painful.
 */
@Composable
fun ConfirmationDialog(
    viewModel: RecollDroidViewModel,
    message: String,
    onConfirmRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.warning),
                contentDescription = "Exercise Caution",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
            Text(text = message)
            Row {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                ) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = {
                        onConfirmRequest()
                        viewModel.clearConfirmableAction()
                    },
                    modifier = Modifier
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}
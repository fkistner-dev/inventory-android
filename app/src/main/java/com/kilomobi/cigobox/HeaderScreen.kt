package com.kilomobi.cigobox

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kilomobi.cigobox.ui.theme.CigOrange
import com.kilomobi.cigobox.ui.theme.CigoGreen

@Composable
fun HeaderItem(
    allowEdit: Boolean,
    isBoxScreen: Boolean,
    selectedPlayerBox: Int,
    onEditAction: () -> Unit,
    onBoxAction: () -> Unit,
    onValidateAction: () -> Unit
) {
    Row {
        Column(Modifier.weight(0.5f), Arrangement.Center) {
            Image(painterResource(R.drawable.logo_cigobox), "logo", Modifier.size(200.dp))
        }
        Column(Modifier.weight(0.5f), Arrangement.Center, Alignment.CenterHorizontally) {
            if (!isBoxScreen) {
                Button(
                    border = BorderStroke(2.dp, CigOrange),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allowEdit) CigOrange else Color.White
                    ),
                    onClick = { onEditAction() }
                ) {
                    Text(
                        stringResource(id = R.string.btn_edit_stock),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
            if (!allowEdit) {
                Button(
                    border = BorderStroke(2.dp, CigOrange),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBoxScreen) CigOrange else Color.White
                    ),
                    onClick = { onBoxAction() }
                ) {
                    Text(
                        stringResource(id = R.string.btn_substract_box), color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
            if (allowEdit || (isBoxScreen && selectedPlayerBox > 0)) {
                Button(
                    border = BorderStroke(2.dp, CigoGreen),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    onClick = {
                        onValidateAction()
                    }
                ) {
                    Text(
                        stringResource(id = R.string.btn_validate), color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

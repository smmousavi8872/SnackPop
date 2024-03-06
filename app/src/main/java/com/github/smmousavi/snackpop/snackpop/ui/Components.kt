package com.github.smmousavi.snackpop.snackpop.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.smmousavi.snackpop.snackpop.SnackPopType
import com.github.smmousavi.snackpop.snackpop.SnackTypeScope

class Components {

    @Composable
    @Preview
    fun SnackPopView(
        modifier: Modifier = Modifier,
        @SnackTypeScope type: SnackPopType.SnackType = SnackPopType.Done
    ) {
        Surface(modifier = modifier) {
            Row {
                Column {

                }
            }
        }
    }
}
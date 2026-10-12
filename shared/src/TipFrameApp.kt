package com.ma7moud3ly.tipframe

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ma7moud3ly.tipframe.feature.tipframe.TipFrameScreen
import com.ma7moud3ly.tipframe.feature.tipframe.TipFrameViewModel
import com.ma7moud3ly.tipframe.ui.theme.AppTheme

@Composable
fun TipFrameApp(
    viewModel: TipFrameViewModel = viewModel { TipFrameViewModel() }
) {
    AppTheme(darkTheme = viewModel.darkTheme) {
        TipFrameScreen(viewModel = viewModel)
    }
}

package com.lmt.global.base.presenter.main.history

import com.lmt.global.base.common.IViewModel

class HistoryViewModel(
) : IViewModel<HistoryState>() {
    override fun onState(state: HistoryState) {
        TODO("Not yet implemented")
    }

}

sealed class HistoryState : IViewModel.IState
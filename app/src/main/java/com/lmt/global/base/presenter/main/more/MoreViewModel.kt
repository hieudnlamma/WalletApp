package com.lmt.global.base.presenter.main.more

import com.lmt.global.base.common.IViewModel

class MoreViewModel(
) : IViewModel<MoreState>() {
    override fun onState(state: MoreState) {
        TODO("Not yet implemented")
    }

}

sealed class MoreState : IViewModel.IState
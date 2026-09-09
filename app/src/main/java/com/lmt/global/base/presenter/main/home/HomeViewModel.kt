package com.lmt.global.base.presenter.main.home

import com.lmt.global.base.common.IViewModel

class HomeViewModel(
) : IViewModel<HomeState>() {
    override fun onState(state: HomeState) {
        TODO("Not yet implemented")
    }

}

sealed class HomeState : IViewModel.IState
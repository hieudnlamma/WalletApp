package com.lmt.global.base.presenter.main

import com.lmt.global.base.common.IViewModel

class MainViewModel(
) : IViewModel<HomeState>() {
    override fun onState(state: HomeState) {
        TODO("Not yet implemented")
    }

}

sealed class HomeState : IViewModel.IState
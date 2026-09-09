package com.lmt.global.base.presenter.main.cards

import com.lmt.global.base.common.IViewModel

class CardsViewModel(
) : IViewModel<CardsState>() {
    override fun onState(state: CardsState) {
        TODO("Not yet implemented")
    }

}

sealed class CardsState : IViewModel.IState
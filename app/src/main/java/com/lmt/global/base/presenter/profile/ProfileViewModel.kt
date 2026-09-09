package com.lmt.global.base.presenter.profile

import com.lmt.global.base.common.IViewModel

class ProfileViewModel(
) : IViewModel<ProfileState>() {
    override fun onState(state: ProfileState) {
        TODO("Not yet implemented")
    }

}

sealed class ProfileState : IViewModel.IState
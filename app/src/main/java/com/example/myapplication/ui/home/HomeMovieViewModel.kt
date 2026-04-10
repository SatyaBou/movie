package com.example.myapplication.ui.home

import com.example.common.base.BaseViewModel

class HomeMovieViewModel() : BaseViewModel<HomeMovieState, HomeMovieIntent, HomeMovieEffect>(
    initialState = HomeMovieState()
) {
    override fun handleIntent(intent: HomeMovieIntent) {

    }

}

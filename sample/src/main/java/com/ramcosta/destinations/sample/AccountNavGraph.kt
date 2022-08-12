package com.ramcosta.destinations.sample

import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph()
@com.ramcosta.composedestinations.annotation.NavGraph
annotation class AccountNavGraph(
    val start: Boolean = false
)

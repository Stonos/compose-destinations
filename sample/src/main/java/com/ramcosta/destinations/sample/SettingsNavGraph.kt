package com.ramcosta.destinations.sample

import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph()
@com.ramcosta.composedestinations.annotation.NavGraph
annotation class SettingsNavGraph(
    val start: Boolean = false
)

package com.ramcosta.destinations.sample

import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(true)
@com.ramcosta.composedestinations.annotation.NavGraph
annotation class TasksNavGraph(
    val start: Boolean = false
)

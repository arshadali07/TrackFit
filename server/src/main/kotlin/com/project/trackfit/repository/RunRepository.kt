package com.project.trackfit.repository

import model.server.run.RunResponse

class RunRepository {
    private val allRuns = mutableListOf<RunResponse>()

    fun allRuns(): List<RunResponse> = allRuns

    fun addRun(runResponse: RunResponse) {
        allRuns.add(runResponse)
    }

    fun removeRun(id: String): Boolean {
        return allRuns.removeIf { it.id == id }
    }
}
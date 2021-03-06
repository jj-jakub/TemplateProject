package com.jj.core.domain.samples.samples.gps

import com.jj.core.domain.samples.analysis.AnalysedSample
import java.util.Date

data class PathData(
    val id: Int,
    val startTime: Date,
    val endTime: Date,
    val pathNodes: List<AnalysedSample.AnalysedGPSSample>
)
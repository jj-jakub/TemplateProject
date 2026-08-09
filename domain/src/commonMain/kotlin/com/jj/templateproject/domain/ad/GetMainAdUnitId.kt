package com.jj.templateproject.domain.ad

class GetMainAdUnitId(private val adUnitIds: AdUnitIds) {
    operator fun invoke(): String = adUnitIds.mainBannerId
}

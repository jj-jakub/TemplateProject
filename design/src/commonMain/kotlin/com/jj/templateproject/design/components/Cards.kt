package com.jj.templateproject.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jj.templateproject.design.gridMultiple

/**
 * A themed surface for grouping related content, shaped via `MaterialTheme.shapes.medium` and
 * padded on the 8dp grid. Content is laid out in a [Column].
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(gridMultiple(i = 2)),
            verticalArrangement = Arrangement.spacedBy(gridMultiple(i = 1)),
            content = content,
        )
    }
}

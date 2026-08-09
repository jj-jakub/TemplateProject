package com.jj.templateproject.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.design.gridMultiple

/**
 * A one-glance gallery of every design-system component under [TemplateTheme]. It doubles as
 * living documentation (open the @ThemePreviews in the IDE to see light + dark) and a manual
 * visual-QA surface for a branching developer.
 *
 * It sits in androidMain because it follows [ThemePreviews], the annotation that gives it its
 * purpose. Everything it renders is shared code; the gallery itself is a preview surface, and a
 * copy in commonMain stripped of that annotation would be a gallery nothing ever renders.
 */
@ThemePreviews
@Composable
fun ComponentCatalog() {
    TemplateTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(gridMultiple(i = 2)),
                verticalArrangement = Arrangement.spacedBy(gridMultiple(i = 2)),
            ) {
                SectionHeader(text = "Buttons")
                PrimaryButton(text = "Primary", onClick = {})
                SecondaryButton(text = "Secondary", onClick = {})

                SectionHeader(text = "Card")
                AppCard {
                    SectionHeader(text = "Card title")
                    BodyText(text = "Card body text using the body type role.")
                }

                SectionHeader(text = "States")
                LoadingState()
                ErrorState(message = "Something went wrong", onRetry = {})
                EmptyState(title = "Nothing here yet", actionLabel = "Add", onAction = {})
            }
        }
    }
}

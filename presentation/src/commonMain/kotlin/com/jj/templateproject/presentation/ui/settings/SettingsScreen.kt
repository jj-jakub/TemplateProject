package com.jj.templateproject.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jj.templateproject.design.components.BodyText
import com.jj.templateproject.design.components.PrimaryButton
import com.jj.templateproject.design.components.SecondaryButton
import com.jj.templateproject.design.components.SectionHeader
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.domain.game.SavedGameState
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.RequestNotificationPermissionOnLaunch
import com.jj.templateproject.presentation.generated.resources.Res
import com.jj.templateproject.presentation.generated.resources.achievement_first_save
import com.jj.templateproject.presentation.generated.resources.achievement_five_saves
import com.jj.templateproject.presentation.generated.resources.achievement_status_locked
import com.jj.templateproject.presentation.generated.resources.achievement_status_unlocked
import com.jj.templateproject.presentation.generated.resources.achievements_section
import com.jj.templateproject.presentation.generated.resources.api_call_data
import com.jj.templateproject.presentation.generated.resources.api_call_status
import com.jj.templateproject.presentation.generated.resources.check_in_action
import com.jj.templateproject.presentation.generated.resources.installed_from_valid_source_value
import com.jj.templateproject.presentation.generated.resources.loading
import com.jj.templateproject.presentation.generated.resources.save_progress_action
import com.jj.templateproject.presentation.generated.resources.saved_progress_none
import com.jj.templateproject.presentation.generated.resources.saved_progress_section
import com.jj.templateproject.presentation.generated.resources.saved_progress_value
import com.jj.templateproject.presentation.generated.resources.streak_reminder_disabled
import com.jj.templateproject.presentation.generated.resources.streak_reminder_enabled
import com.jj.templateproject.presentation.generated.resources.streak_reminder_toggle_section
import com.jj.templateproject.presentation.generated.resources.streak_section
import com.jj.templateproject.presentation.generated.resources.streak_value
import com.jj.templateproject.presentation.generated.resources.theme_dark
import com.jj.templateproject.presentation.generated.resources.theme_light
import com.jj.templateproject.presentation.generated.resources.theme_section
import com.jj.templateproject.presentation.generated.resources.theme_system
import com.jj.templateproject.presentation.generated.resources.version
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import com.jj.templateproject.presentation.ui.state.UiState
import com.jj.templateproject.presentation.ui.state.UiStateContent
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()
    RequestNotificationPermissionOnLaunch()

    SettingsScreenViewContent(
        state = state,
        onRetry = viewModel::retry,
        onSelectTheme = viewModel::setThemeMode,
        onSaveProgress = viewModel::saveDemoProgress,
        onCheckIn = viewModel::checkInToday,
        onToggleReminder = viewModel::setReminderEnabled,
    )
}

// internal, not private: see MainScreen.kt's identical note on why the preview lives elsewhere.
// Takes the whole SettingsScreenViewState rather than one param per field: this screen bundles
// several independent demo features (theme, API status, saved progress, achievements, a streak),
// and a param per field would mean every new one grows this signature indefinitely — a new piece
// of *state* should not require touching this composable's shape at all, only a new *interaction*
// (one of the callbacks below) does.
@Composable
internal fun SettingsScreenViewContent(
    state: SettingsScreenViewState,
    onRetry: () -> Unit,
    onSelectTheme: (ThemeMode) -> Unit,
    onSaveProgress: () -> Unit,
    onCheckIn: () -> Unit,
    onToggleReminder: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            SettingsTextField(text = stringResource(Res.string.version, state.versionText))

            UiStateContent(state = state.apiState, onRetry = onRetry) { apiData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettingsTextField(text = stringResource(Res.string.api_call_status, apiData.status))
                    SettingsTextField(text = stringResource(Res.string.api_call_data, apiData.data))
                }
            }

            SettingsTextField(
                text = stringResource(
                    Res.string.installed_from_valid_source_value,
                    state.installedFromValidSource?.toString() ?: stringResource(Res.string.loading),
                ),
            )

            ThemeSelector(selectedMode = state.themeMode, onSelect = onSelectTheme)

            SavedProgressSection(savedGameState = state.savedGameState, onSaveProgress = onSaveProgress)

            AchievementsSection(unlockedAchievements = state.unlockedAchievements)

            StreakSection(
                currentStreak = state.currentStreak,
                reminderEnabled = state.reminderEnabled,
                onCheckIn = onCheckIn,
                onToggleReminder = onToggleReminder,
            )
        }
    }
}

@Composable
private fun StreakSection(
    currentStreak: Int,
    reminderEnabled: Boolean,
    onCheckIn: () -> Unit,
    onToggleReminder: (Boolean) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        SectionHeader(text = stringResource(Res.string.streak_section))
        SettingsTextField(text = stringResource(Res.string.streak_value, currentStreak))
        PrimaryButton(text = stringResource(Res.string.check_in_action), onClick = onCheckIn)

        SectionHeader(text = stringResource(Res.string.streak_reminder_toggle_section))
        // selectableGroup() + per-option `selected` semantics, matching ThemeSelector's own
        // pattern below for the same TalkBack reason.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.selectableGroup(),
        ) {
            listOf(true, false).forEach { enabled ->
                val label = stringResource(
                    if (enabled) Res.string.streak_reminder_enabled else Res.string.streak_reminder_disabled,
                )
                val optionModifier = Modifier.semantics { selected = (enabled == reminderEnabled) }
                if (enabled == reminderEnabled) {
                    PrimaryButton(text = label, onClick = { onToggleReminder(enabled) }, modifier = optionModifier)
                } else {
                    SecondaryButton(text = label, onClick = { onToggleReminder(enabled) }, modifier = optionModifier)
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection(unlockedAchievements: Set<Achievement>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        SectionHeader(text = stringResource(Res.string.achievements_section))
        Achievement.entries.forEach { achievement ->
            val name = stringResource(achievement.labelRes())
            val text = if (achievement in unlockedAchievements) {
                stringResource(Res.string.achievement_status_unlocked, name)
            } else {
                stringResource(Res.string.achievement_status_locked, name)
            }
            SettingsTextField(text = text)
        }
    }
}

private fun Achievement.labelRes(): StringResource = when (this) {
    Achievement.FIRST_SAVE -> Res.string.achievement_first_save
    Achievement.FIVE_SAVES -> Res.string.achievement_five_saves
}

@Composable
private fun SavedProgressSection(
    savedGameState: SavedGameState?,
    onSaveProgress: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        SectionHeader(text = stringResource(Res.string.saved_progress_section))
        val progressText = if (savedGameState != null) {
            stringResource(Res.string.saved_progress_value, savedGameState.score)
        } else {
            stringResource(Res.string.saved_progress_none)
        }
        SettingsTextField(text = progressText)
        PrimaryButton(text = stringResource(Res.string.save_progress_action), onClick = onSaveProgress)
    }
}

@Composable
private fun ThemeSelector(
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    val options = listOf(
        ThemeMode.SYSTEM to Res.string.theme_system,
        ThemeMode.LIGHT to Res.string.theme_light,
        ThemeMode.DARK to Res.string.theme_dark,
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        SectionHeader(text = stringResource(Res.string.theme_section))
        // selectableGroup() + per-option `selected` semantics so TalkBack announces the active
        // theme, not just the visual fill/outline difference.
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.selectableGroup(),
        ) {
            options.forEach { (mode, labelRes) ->
                val label = stringResource(labelRes)
                val optionModifier = Modifier.semantics { selected = (mode == selectedMode) }
                if (mode == selectedMode) {
                    PrimaryButton(text = label, onClick = { onSelect(mode) }, modifier = optionModifier)
                } else {
                    SecondaryButton(text = label, onClick = { onSelect(mode) }, modifier = optionModifier)
                }
            }
        }
    }
}

@Composable
private fun SettingsTextField(text: String) {
    BodyText(
        text = text,
        modifier = Modifier.padding(
            bottom = gridMultiple(i = 1)
        ),
    )
}

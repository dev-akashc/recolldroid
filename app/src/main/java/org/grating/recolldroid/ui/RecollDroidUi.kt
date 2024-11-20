/* Copyright (C) 2024 Graham Bygrave
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the
 *   Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.grating.recolldroid.ui

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import org.grating.recolldroid.R
import org.grating.recolldroid.data.AppErrorState
import org.grating.recolldroid.data.DocType
import org.grating.recolldroid.data.DownloadedDocumentState
import org.grating.recolldroid.ui.model.QueryFragment
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.screens.CheatSheetScreen
import org.grating.recolldroid.ui.screens.PreviewScreen
import org.grating.recolldroid.ui.screens.QueryScreen
import org.grating.recolldroid.ui.screens.RawDetailsScreen
import org.grating.recolldroid.ui.screens.ResultsScreen
import org.grating.recolldroid.ui.screens.SearchHistoryScreen
import org.grating.recolldroid.ui.screens.SnippetsScreen
import org.grating.recolldroid.ui.screens.dialogs.ConfirmationDialog
import org.grating.recolldroid.ui.screens.dialogs.DateRangeFilterSearchDialog
import org.grating.recolldroid.ui.screens.dialogs.IncludeExcludeFilterSearchDialog
import org.grating.recolldroid.ui.screens.settings.SettingsScreen
import java.io.File
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecollDroidUi(
    viewModel: RecollDroidViewModel = viewModel(factory = RecollDroidViewModel.Factory),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val ctx = LocalContext.current

    AppLevelErrorNotice(viewModel)
    DownloadedDocumentOpener(viewModel, ctx)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RecollDroidTopAppBar(
                currentScreen = RecollDroidScreen.valueOf(
                    backStackEntry?.destination?.route ?: RecollDroidScreen.Query.name
                ),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navigateSettings = { navController.navigate(RecollDroidScreen.Settings.name) },
                navigateCheatSheet = { navController.navigate(RecollDroidScreen.CheatSheet.name) },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { contentPadding ->
        val uiState = viewModel.uiState.collectAsState().value
        NavHost(
            navController = navController,
            startDestination = RecollDroidScreen.Query.name,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(route = RecollDroidScreen.Query.name) {
                QueryScreen(viewModel = viewModel,
                            onQueryChanged = viewModel::updateCurrentQuery,
                            onQueryExecuteRequest = {
                                viewModel.executeCurrentQuery()
                                navController.navigate(RecollDroidScreen.Results.name)
                            },
                            onGotoSearchHistory = {
                                navController.navigate(RecollDroidScreen.SearchHistory.name)
                            },
                            onQuerySupportRequested = { queryFragment ->
                                triggerQuerySupport(viewModel, navController, queryFragment)
                            }
                )
            }
            composable(route = RecollDroidScreen.Results.name) {
                ResultsScreen(
                    viewModel = viewModel,
                    onQueryChanged = viewModel::updateCurrentQuery,
                    onQueryExecuteRequest = {
                        viewModel.executeCurrentQuery()
                    },
                    onPreviewShow = { result ->
                        viewModel.retrievePreview(result)
                        navController.navigate(RecollDroidScreen.Preview.name)
                    },
                    onOpenDocument = { result ->
                        if (result.iPath.isBlank())
                            viewModel.requestDownload(result, ctx)
                        else {
                            viewModel.setConfirmableAction(
                                "You have requested to open an internal document.  This has to be "
                                        + "extracted from a (potentially large) containing document and can "
                                        + "therefore take a long time to complete (i.e. minutes).\n"
                                        + "During this time the recoll server database will be locked "
                                        + "and no requests will be processed."
                            ) {
                                viewModel.requestInternalDocumentDownload(result, ctx)
                            }
                            navController.navigate(RecollDroidScreen.Confirmation.name)
                        }
                    },
                    onSnippetsShow = { result ->
                        viewModel.retrieveSnippets(result)
                        navController.navigate(RecollDroidScreen.Snippets.name)
                    },
                    onRawDataShow = { result ->
                        viewModel.setCurrentResult(result)
                        navController.navigate(RecollDroidScreen.RawDetail.name)
                    },
                    onMimeTypeClick = { result ->
                        viewModel.setCurrentResult(result)
                        navController.navigate(RecollDroidScreen.FilterMime.name)
                    },
                    onDateClick = { result ->
                        viewModel.setCurrentResult(result)
                        navController.navigate(RecollDroidScreen.FilterDateRange.name)
                    },
                    onGotoSearchHistory = {
                        navController.navigate(RecollDroidScreen.SearchHistory.name)
                    },
                    onQuerySupportRequested = { queryFragment ->
                        triggerQuerySupport(viewModel, navController, queryFragment)
                    }
                )
            }
            composable(route = RecollDroidScreen.RawDetail.name) {
                RawDetailsScreen(viewModel = viewModel)
            }
            composable(route = RecollDroidScreen.Preview.name) {
                PreviewScreen(viewModel = viewModel)
            }
            composable(route = RecollDroidScreen.Snippets.name) {
                SnippetsScreen(viewModel = viewModel)
            }
            composable(route = RecollDroidScreen.CheatSheet.name) {
                CheatSheetScreen(viewModel = viewModel)
            }
            composable(route = RecollDroidScreen.Settings.name) {
                SettingsScreen(viewModel = viewModel)
            }
            dialog(route = RecollDroidScreen.Confirmation.name) {
                ConfirmationDialog(viewModel,
                                   uiState.confirmationMessage,
                                   onConfirmRequest = {
                                       uiState.confirmableAction()
                                       navController.navigateUp()
                                   },
                                   onDismissRequest = {
                                       navController.navigateUp()
                                   })
            }
            dialog(route = RecollDroidScreen.FilterMime.name) {
                val qf = uiState.queryFragment
                val (rawType, delFunc) = when {
                    qf.isMimeTypeFilter() -> Pair(
                        qf.getMimeType(),
                        fun() {
                            viewModel.stripCurrentFragmentFromQuery()
                            navController.navigateUp()
                        })

                    uiState.currentResult != null -> Pair(
                        uiState.currentResult.mType.rawType, null
                    )

                    else -> { // Shouldn't really be here then :-S
                        navController.navigateUp()
                        Pair("", null)
                    }
                }

                IncludeExcludeFilterSearchDialog(viewModel = viewModel,
                                                 message = buildAnnotatedString {
                                                     append("Filter current search by mime-type: ")
                                                     withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                                         append(rawType)
                                                     }
                                                 },
                                                 docType = DocType.fromText(rawType),
                                                 includeMessage = "Include",
                                                 onIncludeRequest = {
                                                     viewModel.updateMimeTypeFilter("mime:$rawType")
                                                     navController.navigateUp()
                                                 },
                                                 excludeMessage = "Exclude",
                                                 onExcludeRequest = {
                                                     viewModel.updateMimeTypeFilter("-mime:$rawType")
                                                     navController.navigateUp()
                                                 },
                                                 onDeleteRequest = delFunc,
                                                 onDismissRequest = {
                                                     viewModel.clearQueryFragment()
                                                     navController.navigateUp()
                                                 })
            }
            dialog(route = RecollDroidScreen.FilterDateRange.name) {
                val qf = uiState.queryFragment
                val (dateRange, delFunc) = when {
                    qf.isDateRangeFilter() -> Pair(
                        getDateRange(uiState.queryFragment.word) ?: DEFAULT_DATE_RANGE,
                        fun() {
                            viewModel.stripCurrentFragmentFromQuery()
                            navController.navigateUp()
                        })

                    uiState.currentResult != null -> Pair(
                        Pair(uiState.currentResult.date, uiState.currentResult.date),
                        null
                    )

                    else -> Pair(DEFAULT_DATE_RANGE, null)
                }

                DateRangeFilterSearchDialog(viewModel = viewModel,
                                            message = AnnotatedString("Filter by date range"),
                                            range = dateRange,
                                            onSetDateRangeInclusive = {
                                                viewModel.updateFilterDateRange(it)
                                                navController.navigateUp()
                                            },
                                            onDeleteRequest = delFunc,
                                            onDismissRequest = {
                                                viewModel.clearQueryFragment()
                                                navController.navigateUp()
                                            }
                )
            }
            composable(route = RecollDroidScreen.SearchHistory.name) {
                SearchHistoryScreen(viewModel,
                                    onHistoricalSearchClick = {
                                        viewModel.updateCurrentQuery(it)
                                        navController.navigateUp()
                                    })
            }
        }
    }
}

private fun triggerQuerySupport(
    viewModel: RecollDroidViewModel,
    navController: NavHostController,
    queryFragment: QueryFragment
): Boolean {
    return when {
        queryFragment.isDateRangeFilter() -> {
            viewModel.updateQueryFragment(queryFragment)
            navController.navigate(RecollDroidScreen.FilterDateRange.name)
            true
        }

        queryFragment.isMimeTypeFilter() -> {
            viewModel.updateQueryFragment(queryFragment)
            navController.navigate(RecollDroidScreen.FilterMime.name)
            true
        }

        else -> {
            false
        }
    }
}

/**
 * Try to open downloaded documents/files once they arrive.
 */
@Composable
private fun DownloadedDocumentOpener(
    viewModel: RecollDroidViewModel,
    ctx: Context
) {
    val dds = viewModel.ddLatch.downloadedDocumentFlow.collectAsState().value
    try {
        when (dds) {
            is DownloadedDocumentState.Ready -> {
                val viewIntent =
                    Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val contentUri = FileProvider.getUriForFile(
                    ctx,
                    ctx.applicationContext.packageName + ".provider",
                    File(URI(dds.detail.uri.toString())))
                viewIntent.setDataAndType(contentUri, dds.detail.mimeType)
                ctx.startActivity(viewIntent)
                viewModel.ddLatch.clearDocument()
            }

            DownloadedDocumentState.Idle -> {} // nothing to do here.
        }
    } catch (t: Throwable) {
        viewModel.ddLatch.clearDocument()
        viewModel.errorLatch.raiseError("Problem while trying to open document.", t)
    }
}

/**
 * Composable for showing application level errors signalled via the application error latch.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppLevelErrorNotice(viewModel: RecollDroidViewModel) {
    when (val appErrState = viewModel.errorLatch.errorFlow.collectAsState().value) {
        is AppErrorState.Error ->
            BasicAlertDialog(onDismissRequest = { viewModel.errorLatch.clearError() },
                             modifier = Modifier) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.error),
                            contentDescription = "Application Error",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp)
                        )
                        Text(text = appErrState.msg)
                        Text(text = appErrState.t.message ?: appErrState.t.toString(),
                             Modifier.padding(8.dp))
                    }
                }
            }

        AppErrorState.Ok -> {} // nothing to see here
    }
}

enum class RecollDroidScreen(@StringRes val title: Int) {
    Query(title = R.string.query_screen_title),
    Results(title = R.string.results_screen_title),
    RawDetail(title = R.string.raw_detail_screen_title),
    Preview(title = R.string.preview_screen_title),
    Snippets(title = R.string.snippets_list_screen_title),
    Settings(title = R.string.settings_screen_title),
    Confirmation(title = R.string.confirmation_dialog),
    FilterMime(title = R.string.filter_mimetype_dialog),
    FilterDateRange(title = R.string.filter_date_range),
    SearchHistory(title = R.string.search_history_screen_title),
    CheatSheet(title = R.string.cheat_sheet_screen_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecollDroidTopAppBar(
    currentScreen: RecollDroidScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    navigateCheatSheet: () -> Unit,
    navigateSettings: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) = CenterAlignedTopAppBar(
    scrollBehavior = scrollBehavior,
    title = {
        Text(
            text = stringResource(currentScreen.title),
            style = MaterialTheme.typography.headlineSmall
        )
    },
    colors = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    modifier = modifier,
    navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }
    },
    actions = {
//            if(uiState.currentScreenIsScrollable) {
//                IconButton(onClick = {}) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.Top
//                    )
//                }
//            }
        if (currentScreen != RecollDroidScreen.CheatSheet) {
            IconButton(onClick = { navigateCheatSheet() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = stringResource(R.string.cheatsheet_button)
                )
            }
        }
        if (currentScreen != RecollDroidScreen.Settings) {
            IconButton(onClick = { navigateSettings() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_button)
                )
            }
        }
    }

)


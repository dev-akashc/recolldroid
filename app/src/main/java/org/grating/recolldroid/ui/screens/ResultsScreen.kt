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
package org.grating.recolldroid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import org.grating.recolldroid.R
import org.grating.recolldroid.data.AlwaysIdleDownloadedDocumentLatch
import org.grating.recolldroid.data.AlwaysOkErrorLatch
import org.grating.recolldroid.data.RecollSearchResult
import org.grating.recolldroid.data.ResultSet
import org.grating.recolldroid.data.fake.FakeResultsDataProvider
import org.grating.recolldroid.data.fake.FakeResultsRepository
import org.grating.recolldroid.data.shortName
import org.grating.recolldroid.ui.addLoadState
import org.grating.recolldroid.ui.cleanup
import org.grating.recolldroid.ui.data.fake.FakeSettingsRepository
import org.grating.recolldroid.ui.doHighlight
import org.grating.recolldroid.ui.model.QueryFragment
import org.grating.recolldroid.ui.model.QueryResponse
import org.grating.recolldroid.ui.model.RecollDroidViewModel
import org.grating.recolldroid.ui.or
import org.grating.recolldroid.ui.readableFileSize
import org.grating.recolldroid.ui.simpleVerticalScrollbar
import org.grating.recolldroid.ui.theme.RecollDroidTheme
import org.grating.recolldroid.ui.toDateString


@Composable
fun ResultsScreen(
    viewModel: RecollDroidViewModel,
    modifier: Modifier = Modifier,
    onQueryChanged: (query: TextFieldValue) -> Unit,
    onQueryExecuteRequest: () -> Unit,
    onPreviewShow: (RecollSearchResult) -> Unit,
    onOpenDocument: (RecollSearchResult) -> Unit,
    onSnippetsShow: (RecollSearchResult) -> Unit,
    onRawDataShow: (RecollSearchResult) -> Unit,
    onMimeTypeClick: (RecollSearchResult) -> Unit,
    onDateClick: (RecollSearchResult) -> Unit,
    onQuerySupportRequested: (QueryFragment) -> Boolean,
    onGotoSearchHistory: () -> Unit
) {
    Column {
        QueryBar(viewModel = viewModel,
                 onQueryChanged = onQueryChanged,
                 onQueryExecuteRequest = onQueryExecuteRequest,
                 onGotoSearchHistory = onGotoSearchHistory,
                 onQuerySupportRequested = onQuerySupportRequested)

        val uiState = viewModel.uiState.collectAsState().value
        when (uiState.queryResponse) {
            is QueryResponse.Success -> PopulatedResults(
                pageFlow = uiState.queryResponse.result,
                onPreviewShow = onPreviewShow,
                onOpenDocument = onOpenDocument,
                onSnippetsShow = onSnippetsShow,
                onResultInfoShow = onRawDataShow,
                onMimeTypeClick = onMimeTypeClick,
                onDateClick = onDateClick,
                modifier = modifier,
            )

            is QueryResponse.Pending -> TODO()
            is QueryResponse.Error -> TODO()
            QueryResponse.None -> TODO()
        }
    }
}

@Composable
fun PopulatedResults(
    pageFlow: Flow<PagingData<RecollSearchResult>>,
    onPreviewShow: (RecollSearchResult) -> Unit,
    onOpenDocument: (RecollSearchResult) -> Unit,
    onSnippetsShow: (RecollSearchResult) -> Unit,
    onResultInfoShow: (RecollSearchResult) -> Unit,
    onMimeTypeClick: (RecollSearchResult) -> Unit,
    onDateClick: (RecollSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally)
    {
        val results = pageFlow.collectAsLazyPagingItems()
        val resultSet =
            if (results.itemCount > 0)
                results[0]?.resultSet ?: ResultSet.NULL
            else ResultSet.NULL

        if (results.itemCount > 0 || results.loadState.isIdle)
            Text("Recoll returned ${resultSet.size} results in ${resultSet.queryMs} millis.")
        val lazyListState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .simpleVerticalScrollbar(lazyListState, resultSet.size)
                .align(Alignment.CenterHorizontally)
        ) {
            // Display loaded items...
            items(results.itemCount) { index ->
                ResultCard(result = results[index]!!,
                           onPreviewShow = onPreviewShow,
                           onOpenDocument = onOpenDocument,
                           onSnippetsShow = onSnippetsShow,
                           onResultInfoShow = onResultInfoShow,
                           onMimeTypeClick = onMimeTypeClick,
                           onDateClick = onDateClick,
                           modifier = modifier)
            }

            // Display error loading while appending...
            addLoadState(results.loadState.append)
            addLoadState(results.loadState.refresh)
        }
    }
}


@Composable
fun ResultCard(
    result: RecollSearchResult,
    onPreviewShow: (RecollSearchResult) -> Unit,
    onOpenDocument: (RecollSearchResult) -> Unit,
    onSnippetsShow: (RecollSearchResult) -> Unit,
    onResultInfoShow: (RecollSearchResult) -> Unit,
    onMimeTypeClick: (RecollSearchResult) -> Unit,
    onDateClick: (RecollSearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(modifier = modifier) {
            Column(modifier = Modifier) {
                Image(
                    painter = painterResource(result.mType.docType.typeIcon),
                    contentDescription = result.mType.rawType,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            onMimeTypeClick(result)
                        },
                    contentScale = ContentScale.Crop
                )
                Text(text = "#${result.idx + 1}",
                     modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Column(modifier = Modifier.padding(4.dp)) {
                Row {
                    Text(text = result.title or result.filename,
                         style = MaterialTheme.typography.titleLarge,
                         modifier = Modifier.weight(1f))
                    OutlinedIconButton(
                        onClick = {
                            onResultInfoShow(result)
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Info,
                             contentDescription = stringResource(R.string.show_raw_information))
                    }
                }
                Row {
                    Text(text = "${result.relevancyRatingPercent}%",
                         style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = result.fBytes.readableFileSize(),
                         style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = result.mType.docType.shortName(),
                         style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = result.date.toDateString(),
                         style = MaterialTheme.typography.titleSmall,
                         color = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.clickable { onDateClick(result) }
                    )
                }
            }
        }
        Row {
            Text(text =
                 if (result.iPath.isBlank())
                     result.snippetsAbstract.cleanup().doHighlight()
                 else
                     result.abstract.cleanup().doHighlight(),
                 Modifier.padding(8.dp))
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { onPreviewShow(result) },
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(text = stringResource(R.string.preview_btn),
                     maxLines = 1)
            }
            OutlinedButton(
                onClick = { onOpenDocument(result) },
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(text = stringResource(R.string.open_btn),
                     maxLines = 1)
            }
            OutlinedButton(
                onClick = { onSnippetsShow(result) },
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(text = stringResource(R.string.snippets_btn),
                     maxLines = 1)
            }
        }

    }
}

@Composable
fun NoResults() {
    Text("No results")
}

@Composable
fun ResultsError() {
    Text("Error!")
}


@Preview(showBackground = true)
@Composable
fun ResultCardPreview() {
    RecollDroidViewModel(AlwaysOkErrorLatch,
                         AlwaysIdleDownloadedDocumentLatch,
                         FakeResultsRepository(),
                         FakeSettingsRepository())
    RecollDroidTheme {
        ResultCard(
            result = FakeResultsDataProvider.getFirstResult(),
            onPreviewShow = {},
            onOpenDocument = {},
            onSnippetsShow = {},
            onResultInfoShow = {},
            onMimeTypeClick = {},
            onDateClick = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    val vm = RecollDroidViewModel(AlwaysOkErrorLatch,
                                  AlwaysIdleDownloadedDocumentLatch,
                                  FakeResultsRepository(),
                                  FakeSettingsRepository())
    vm.updateCurrentQuery(TextFieldValue("Some query or other"))
    vm.executeCurrentQuery()
    RecollDroidTheme {
        ResultsScreen(
            viewModel = vm,
            onQueryChanged = {},
            onQueryExecuteRequest = {},
            onPreviewShow = {},
            onOpenDocument = {},
            onSnippetsShow = {},
            onMimeTypeClick = {},
            onRawDataShow = {},
            onDateClick = {},
            onGotoSearchHistory = {},
            onQuerySupportRequested = {false}
        )
    }
}

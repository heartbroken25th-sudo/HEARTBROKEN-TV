package com.heartbroken.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartbroken.tv.ui.theme.Gold
import com.heartbroken.tv.ui.theme.Obsidian900
import com.heartbroken.tv.ui.theme.Obsidian950
import com.heartbroken.tv.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Obsidian950, Obsidian900)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "HEARTBROKEN TV",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Discover",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Browse television channels by country and category.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(Modifier.height(32.dp))

        when (val s = state) {
            is HomeUiState.Loading -> LoadingState()
            is HomeUiState.Empty -> EmptyState()
            is HomeUiState.Error -> ErrorState(s.message, viewModel::loadChannels)
            is HomeUiState.Success -> SuccessState(s, onCountryClick)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Gold)
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No channels available yet", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Text("The channel catalog is empty.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Could not load channels", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Obsidian950)) {
            Text("Retry")
        }
    }
}

@Composable
private fun SuccessState(state: HomeUiState.Success, onCountryClick: (String) -> Unit) {
    Column {
        Text("Countries", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        CountryGrid(state.countries, onCountryClick)

        Spacer(Modifier.height(32.dp))

        Text("TV Categories", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        CategoryList(state.categories)

        Spacer(Modifier.height(32.dp))
        Text("${state.totalChannels} channels available", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun CountryGrid(countries: List<CountrySummary>, onCountryClick: (String) -> Unit) {
    val chunked = countries.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        chunked.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { country ->
                    CountryCard(country, Modifier.weight(1f)) { onCountryClick(country.name) }
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CountryCard(country: CountrySummary, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Text(country.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text("${country.channelCount} channels", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun CategoryList(categories: List<CategorySummary>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(category.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("${category.channelCount}", style = MaterialTheme.typography.bodyMedium, color = Gold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

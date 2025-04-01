package com.tcgcollector.ui.scan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tcgcollector.data.model.CardMatchResult
import com.tcgcollector.data.network.MockCardService
import kotlinx.coroutines.launch

@Composable
fun ConfirmCardMatchScreen(
    cardName: String,
    cardNumber: String,
    onSubmit: (action: String, quantity: Int, variant: String, cardId: String) -> Unit,
    onBackToName: () -> Unit,
    onBackToNumber: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var action by remember { mutableStateOf("Add") }
    var quantityStr by remember { mutableStateOf("1") }

    var matchedCards by remember { mutableStateOf<List<CardMatchResult>>(emptyList()) }
    var selectedCard by remember { mutableStateOf<CardMatchResult?>(null) }
    var selectedVariant by remember { mutableStateOf("") }

    // Fetch card matches on launch or when inputs change
    LaunchedEffect(cardName, cardNumber) {
        matchedCards = MockCardService.searchCardByNameAndNumber(cardName, cardNumber)
        selectedCard = matchedCards.firstOrNull()
        selectedVariant = selectedCard?.variants?.firstOrNull().orEmpty()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Confirm Match", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Card Name + back button
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Card Name: $cardName", modifier = Modifier.weight(1f))
            TextButton(onClick = onBackToName) { Text("Edit") }
        }

        // Card Number + back button
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Card Number: $cardNumber", modifier = Modifier.weight(1f))
            TextButton(onClick = onBackToNumber) { Text("Edit") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show matching card images
        if (matchedCards.isEmpty()) {
            Text("No matches found.", color = MaterialTheme.colorScheme.error)
        } else {
            Text("Tap to select matching card:")
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(matchedCards) { card ->
                    CardImageItem(
                        card = card,
                        isSelected = card.id == selectedCard?.id,
                        onClick = {
                            selectedCard = card
                            selectedVariant = card.variants.firstOrNull().orEmpty()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add/Remove Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Action:")
            Spacer(modifier = Modifier.width(16.dp))
            SegmentedButton(action, onActionChange = { action = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quantity with +/- buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Quantity:")
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val current = quantityStr.toIntOrNull() ?: 1
                if (current > 1) quantityStr = (current - 1).toString()
            }) { Text("-") }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = quantityStr,
                onValueChange = {
                    quantityStr = it.filter { ch -> ch.isDigit() }.take(3)
                },
                modifier = Modifier.width(80.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val current = quantityStr.toIntOrNull() ?: 1
                quantityStr = (current + 1).toString()
            }) { Text("+") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Variant selector
        if (!selectedCard?.variants.isNullOrEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Variant:")
                Spacer(modifier = Modifier.width(16.dp))
                DropdownMenuBox(
                    options = selectedCard?.variants ?: emptyList(),
                    selected = selectedVariant,
                    onSelected = { selectedVariant = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit/Cancel
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                onCancel()
            }) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (selectedCard != null && selectedVariant.isNotBlank()) {
                        val qty = quantityStr.toIntOrNull() ?: 1
                        Toast.makeText(
                            context,
                            "$action $qty x $selectedVariant of ${selectedCard!!.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSubmit(action, qty, selectedVariant, selectedCard!!.id)
                    } else {
                        Toast.makeText(context, "Please select a card and variant", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Confirm")
            }
        }
    }
}

// ---------------------------
// Reusable Composables Below
// ---------------------------

@Composable
fun CardImageItem(card: CardMatchResult, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
            .width(120.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(card.imageUrl),
            contentDescription = card.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f),
            contentScale = ContentScale.Crop
        )
        if (isSelected) {
            Text("✓ Selected", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Select Variant") },
            readOnly = true,
            modifier = Modifier
                .width(200.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SegmentedButton(selected: String, onActionChange: (String) -> Unit) {
    Row {
        Button(
            onClick = { onActionChange("Add") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected == "Add") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text("Add")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onActionChange("Remove") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected == "Remove") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text("Remove")
        }
    }
}
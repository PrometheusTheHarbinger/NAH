package com.example.notalonehelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.example.notalonehelper.card.Card as GameCard
import com.example.notalonehelper.player.PlayerVM
import com.example.notalonehelper.ui.theme.NotAloneHelperTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val players: SnapshotStateList<PlayerVM> = mutableStateListOf()
        val showNewGameDialog = mutableStateOf(false)
        enableEdgeToEdge()
        setContent {
            NotAloneHelperTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            title = {},
                            actions = {
                                IconButton(onClick = { showNewGameDialog.value = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.dice),
                                        contentDescription = "",
                                        modifier = Modifier.scale(0.9f)
                                    )
                                }
                            },
                            modifier = Modifier.height(IntrinsicSize.Min)
                        )
                    }
                ) { innerPadding ->
                    GameView(
                        players = players,
                        modifier = Modifier.padding(innerPadding),
                        showNewGameDialog = showNewGameDialog.value,
                        onNewGameDialogDismiss = { showNewGameDialog.value = false },
                        onNewGameDialogConfirm = { playersNumber -> run{
                            showNewGameDialog.value = false
                            players.clear()
                            (1..playersNumber).forEach { players.add(PlayerVM(it.toString())) }
                        }}
                    )
                }
            }
        }
    }
}

@Composable
fun DialogNewGame(modifier: Modifier = Modifier, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var playersNumber by remember { mutableIntStateOf(0) }
    val focusRequester = FocusRequester()
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier.padding(4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    singleLine = true,
                    value = if (playersNumber != 0) playersNumber.toString() else "",
                    onValueChange = { changedNumber ->
                                        if (changedNumber.isNotEmpty() && changedNumber.isDigitsOnly() && changedNumber.toInt() >= 2 && changedNumber.toInt() < 7) {
                                            playersNumber = changedNumber.toInt()
                                        }
                                        if (changedNumber.isEmpty()) { playersNumber = 0 }
                                    },
                    label = { Text(stringResource(R.string.prompt_player_number)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { onConfirm(playersNumber) }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DialogAddGameCard(modifier: Modifier = Modifier, onDismiss: () -> Unit, onCardChosen: (card: GameCard) -> Unit) {
    val paintersArray = listOf(R.drawable.card_swamp, R.drawable.card_shelter, R.drawable.card_wreck, R.drawable.card_source, R.drawable.card_artifact)
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally), modifier = modifier.padding(6.dp)) {
                for (cardOrdinal in 5..9) {
                    Image(
                        modifier = Modifier.weight(1f).clickable { onCardChosen(GameCard.entries[cardOrdinal]) },
                        painter = painterResource(paintersArray[cardOrdinal-5]),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun DialogRename(modifier: Modifier = Modifier, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newName by remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier.padding(4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    singleLine = true,
                    value = newName,
                    onValueChange = { changedName -> if (changedName.length < 25) newName = changedName },
                    label = { Text(stringResource(R.string.prompt_player_name)) }
                )
                Row {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { onConfirm(newName) }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun CardView(modifier: Modifier = Modifier, card: GameCard) {
    Image(painterResource(card.resource), null, modifier = modifier)
}

@Composable
fun PlayerRow(
    modifier: Modifier = Modifier,
    name: String = "Default",
    onRenameClick: () -> Unit,
    onGameCardAddClick: () -> Unit
) {
    Row(Modifier.height(IntrinsicSize.Min).padding(vertical = 4.dp, horizontal = 6.dp)) {
        Text(
            modifier = modifier.align(Alignment.CenterVertically),
            text = name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onRenameClick) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.scale(0.8f).align(Alignment.CenterVertically)
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onGameCardAddClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun DeckView(modifier: Modifier = Modifier, cards: List<GameCard>, cardClicked: (GameCard) -> Unit) {
    LazyVerticalGrid(
        modifier = modifier.padding(horizontal = 4.dp),
        columns = GridCells.Adaptive(minSize = 48.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards) { card: GameCard ->
            CardView(card = card, modifier = Modifier.clickable { cardClicked(card) })
        }
    }
}

@Composable
fun PlayerView(modifier: Modifier = Modifier, player: PlayerVM) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddGameCardDialog by remember { mutableStateOf(false) }
    if (showRenameDialog) {
        DialogRename(
            onDismiss = { showRenameDialog = false },
            onConfirm = {
                newName -> run{
                    player.name = newName
                    showRenameDialog = false
                }
            }
        )
    }
    if (showAddGameCardDialog) {
        DialogAddGameCard(
            modifier = Modifier,
            onDismiss = { showAddGameCardDialog = false },
            onCardChosen = {
                gameCard -> run{
                    player.addHand(gameCard)
                    showAddGameCardDialog = false
                }
            }
        )
    }
    ElevatedCard(modifier = modifier) {
        PlayerRow(name = player.name, onRenameClick = { showRenameDialog = true }, onGameCardAddClick = { showAddGameCardDialog = true })
        HorizontalDivider(thickness = 4.dp)
        DeckView(
            cards = player.hand.sortedBy { it.ordinal },
            cardClicked = { card: GameCard -> player.handToDiscard(card) },
            modifier = Modifier.weight(1f).padding(4.dp)
        )
        HorizontalDivider(thickness = 2.dp)
        DeckView(
            cards = player.discard.sortedBy { it.ordinal },
            cardClicked = { card: GameCard -> player.discardToHand(card) },
            modifier = Modifier.weight(1f).padding(4.dp)
        )
    }
}

@Composable
fun GameView(
    modifier: Modifier = Modifier,
    players: SnapshotStateList<PlayerVM>,
    showNewGameDialog: Boolean,
    onNewGameDialogDismiss: () -> Unit,
    onNewGameDialogConfirm: (Int) -> Unit
) {
    if (showNewGameDialog) {
        DialogNewGame(
            onDismiss = onNewGameDialogDismiss,
            onConfirm = onNewGameDialogConfirm
        )
    }
    LazyColumn(modifier) {
        items(items = players, key = { player -> player.name }) {
            player -> PlayerView(Modifier.padding(4.dp).fillParentMaxHeight(1/3f), player)
        }
    }
}

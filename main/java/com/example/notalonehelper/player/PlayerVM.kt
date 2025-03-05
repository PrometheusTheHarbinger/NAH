package com.example.notalonehelper.player

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.notalonehelper.card.Card

class PlayerVM(var name: String): ViewModel() {
    var hand = mutableStateListOf(Card.LAIR, Card.JUNGLE, Card.RIVER, Card.BEACH, Card.ROVER)
    var discard: SnapshotStateList<Card> = mutableStateListOf()
    fun addHand(card: Card) {
        hand.add(card)
    }
    fun handToDiscard(card: Card) {
        hand.remove(card)
        discard.add(card)
    }
    fun discardToHand(card: Card) {
        discard.remove(card)
        hand.add(card)
    }
}
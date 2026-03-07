package com.example.safetynet.domain

data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)

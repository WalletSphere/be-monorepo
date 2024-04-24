package com.walletsphere.pricingservice.model.internall.exchanger

data class UsedTokens(val records: List<UsedToken>)

data class UsedToken(val code: String,
                     val currencies: String)

package com.sbeve.jada

//type of failure that occurred while looking for a definition for the submitted query
enum class ErrorType {
    CallFailed,
    NoMatch
}

//type of content to be shown on the screen when the fragment is recreated
enum class NetworkRequestResult {
    Success,
    Failure
}

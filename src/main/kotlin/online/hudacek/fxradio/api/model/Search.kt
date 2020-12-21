package online.hudacek.fxradio.api.model

data class SearchBody(val name: String, val limit: Int = 200)

data class SearchByTagBody(val tag: String, val limit: Int = 200)
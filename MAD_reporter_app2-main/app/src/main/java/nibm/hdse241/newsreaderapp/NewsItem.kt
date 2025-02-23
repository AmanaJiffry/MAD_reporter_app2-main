package nibm.hdse241.newsreaderapp

data class NewsItem(
    var newsId: String = "",  // Ensure this property is present
    var heading: String = "",
    var category: String = "",
    var article: String = "",
    var status: String = "",
    var userId: String = ""
)

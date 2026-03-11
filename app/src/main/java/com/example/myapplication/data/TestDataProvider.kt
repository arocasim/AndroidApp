package com.example.myapplication.data

import com.example.myapplication.model.Article

object TestDataProvider {

    fun getArticles(): List<Article> {
        return listOf(
            Article(
                title = "Artificial Intelligence in Medicine",
                subtitle = "How AI is changing healthcare",
                content = "Artificial intelligence is widely used in medicine for diagnostics, data analysis, patient monitoring, and decision support. Modern systems can help doctors detect diseases faster and improve the quality of treatment.",
                author = "John Smith",
                category = "Technology",
                date = "2026-03-09"
            ),
            Article(
                title = "Space Exploration",
                subtitle = "New missions to Mars",
                content = "Space agencies continue developing missions to Mars. These missions aim to study the planet's surface, atmosphere, and the possibility of future human colonization.",
                author = "Emma Brown",
                category = "Science",
                date = "2026-03-08"
            ),
            Article(
                title = "Climate Change",
                subtitle = "Global impact and solutions",
                content = "Climate change affects ecosystems, weather conditions, and human health. Governments and organizations are working on renewable energy, green technologies, and environmental protection policies.",
                author = "Michael Green",
                category = "Environment",
                date = "2026-03-07"
            ),
            Article(
                title = "Nutrition Science",
                subtitle = "Simple habits for better health",
                content = "Healthy nutrition plays an important role in maintaining energy, improving concentration, and preventing disease. Scientists continue studying the connection between diet and long-term health.",
                author = "Anna Wilson",
                category = "Science",
                date = "2026-03-06"
            ),
            Article(
                title = "Business Automation",
                subtitle = "How digital tools improve companies",
                content = "Modern companies focus on digital transformation, process automation, data analysis, and customer personalization. These technologies improve productivity and support business growth.",
                author = "David Lee",
                category = "Technology",
                date = "2026-03-05"
            ),
            Article(
                title = "Environmental Monitoring",
                subtitle = "Technology for protecting nature",
                content = "Modern monitoring systems help track air quality, water pollution, and climate changes. These tools support environmental protection and sustainable development.",
                author = "Chris Taylor",
                category = "Environment",
                date = "2026-03-04"
            )
        )
    }
}
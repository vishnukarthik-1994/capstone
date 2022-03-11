package com.example.dfu_app.data

import com.example.dfu_app.R
import com.example.dfu_app.model.Recommendation

class RecommendationSource() {
    fun loadRecommendations():List<Recommendation>{
        return listOf<Recommendation>(
            Recommendation(R.string.recommendation1),
            Recommendation(R.string.recommendation2),
            //Recommendation(R.string.recommendation3),
            //Recommendation(R.string.recommendation4),
        )
    }
}
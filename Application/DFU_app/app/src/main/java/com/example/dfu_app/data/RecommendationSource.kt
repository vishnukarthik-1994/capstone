package com.example.dfu_app.data

class Solutions {
    private var suggestions: ArrayList<ArrayList<String>> = arrayListOf(
        arrayListOf("Prompt vascular consultation re possible revascularization",
            "Optimise glycaemic control"
        ), // ischemia
        arrayListOf("Cleanse, debride all necrotic tissue and surrounding callus",
            "Start empiric oral antibiotic therapy",
            "Put negative pressure to help heal post-operative wounds"
        ), // infection
        arrayListOf("Do not soak the feet",
            "Avoid walking barefoot, in socks without footwear, or in thin-soled slippers, whether at home or outside",
            "Do not wear shoes that are too tight, have rough edges or uneven seams",
            "Visually inspect and feel inside all shoes before you put them on",
            "do not wear tight or kneehigh sock and change socks daily",
            "Wash feet daily and dry them carefully, especially between the toes",
            "Do not use any kind of heater or a hot-water bottle to warm feet",
            "Do not use chemical agents or plasters to remove corns and calluses"), //  neither
        arrayListOf("Your condition is severe, please contact your healthcare provider as soon as possible") // emergency
    )

    fun getSolutions(choices: ArrayList<Int>): ArrayList<String> {
        val solutions =  arrayListOf<String>()
        val type = choices[0]
        solutions.add(suggestions[2][(0..suggestions[2].size).random()])
        when (type) {
            3 -> {
                solutions.add(suggestions[1][(0 until suggestions[1].size).random()])
                solutions.add(suggestions[0][(0 until suggestions[0].size).random()])
            }
            2 -> {
                solutions.add(suggestions[1][(0 until suggestions[1].size).random()])
            }
            1 -> {
                solutions.add(suggestions[0][(0 until suggestions[0].size).random()])
            }
        }
        if (choices.contains(1)) {
            solutions.add(suggestions[3][0])
        }
        return solutions
    }
}
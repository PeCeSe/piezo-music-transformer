data class PiezoSpec(
    val name: String,
    val tempo: Int,
    val melody: List<String>,
    val pins: List<String>,
    val rhythm: List<Int>,
    val waits: List<Float>
) {

    override fun toString(): String {
        return """
        #define song_size ${melody.size}
        #define tempo $tempo
        #define song_name $name
        
        int melody[] = {
            ${melody.map { "NOTE_" + it.toUpperCase() }.joinToString(", ")}
        };
        
        int rhythm[] = {
            ${rhythm.joinToString(", ")}
        };
        
        int pins[] = {
            ${pins.joinToString(", ")}
        };
        
        float waits[] = {
            ${waits.joinToString(", ")}
        };
    """.trimIndent()
    }
}

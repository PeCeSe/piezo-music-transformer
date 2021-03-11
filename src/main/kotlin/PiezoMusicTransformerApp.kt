import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class PiezoMusicTransformerApp {

    private val gson = Gson()
    private val songType = object : TypeToken<SongSpec>() {}.type

    fun transformSong(filePath: String) {
        val song = getSongFromFile(filePath)

        val piezoSpec = transformSongToPiezoSpec(song)

        println(piezoSpec.toString())
    }

    private fun getSongFromFile(filePath: String): SongSpec {
        val jsonString = File(filePath).readText()
        return gson.fromJson(jsonString, SongSpec::class.java)
    }

    private fun transformSongToPiezoSpec(song: SongSpec): PiezoSpec {
        val toneSegments = song.score.map { track ->
            var currentDuration = 0f
            track.beats.mapIndexed { i, beat ->
                if(i > 0)
                    currentDuration += 1f / beat.toFloat()
                ToneSegment(track.pitches[i], beat, track.pin, currentDuration)
            }
        }.flatten().sortedBy { it.currentDuration }
        return PiezoSpec(
            song.name,
            song.tempo,
            toneSegments.map { it.pitch },
            toneSegments.map { it.pin },
            toneSegments.map { it.beat },
            getDifferences( toneSegments.map { it.currentDuration } )
        )
    }

    private fun getDifferences(timestamps: List<Float>): List<Float> {
        return timestamps.mapIndexed { index, fl ->
                fl - timestamps.getOrElse(index - 1 ) { 0f }
        }
    }

}

fun main() {

    val transformer = PiezoMusicTransformerApp()

    transformer.transformSong("songs/imperialMarch.json")
}
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
                val toneSegment = ToneSegment(track.pitches[i], beat, track.pin, currentDuration)
                currentDuration += 1f / beat.toFloat()
                toneSegment
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
                timestamps.getOrElse(index + 1 ) { fl } - fl
        }
    }

}

fun main() {

    val transformer = PiezoMusicTransformerApp()

    transformer.transformSong("songs/hurraForDeg.json")
}
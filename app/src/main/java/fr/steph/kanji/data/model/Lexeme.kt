package fr.steph.kanji.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.steph.kanji.data.utils.enumeration.LexemeType

@Entity
open class Lexeme(
    @PrimaryKey(autoGenerate = true)
    open val id: Int,
    open val type: LexemeType,
    open val characters: String,
    open val romaji: String,
    open val meaning: String,
    open val onyomi: String? = null,
    open val onyomiRomaji: String? = null,
    open val kunyomi: String? = null,
    open val kunyomiRomaji: String? = null,
) {
    override fun toString(): String {
        return "Lexeme(id=$id, type=$type, characters='$characters', romaji='$romaji', meaning='$meaning', onyomi=$onyomi, onyomiRomaji=$onyomiRomaji, kunyomi=$kunyomi, kunyomiRomaji=$kunyomiRomaji)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Lexeme) return false

        if (id != other.id) return false
        if (type != other.type) return false
        if (characters != other.characters) return false
        if (romaji != other.romaji) return false
        if (meaning != other.meaning) return false
        if (onyomi != other.onyomi) return false
        if (onyomiRomaji != other.onyomiRomaji) return false
        if (kunyomi != other.kunyomi) return false
        if (kunyomiRomaji != other.kunyomiRomaji) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + type.hashCode()
        result = 31 * result + characters.hashCode()
        result = 31 * result + romaji.hashCode()
        result = 31 * result + meaning.hashCode()
        result = 31 * result + (onyomi?.hashCode() ?: 0)
        result = 31 * result + (onyomiRomaji?.hashCode() ?: 0)
        result = 31 * result + (kunyomi?.hashCode() ?: 0)
        result = 31 * result + (kunyomiRomaji?.hashCode() ?: 0)
        return result
    }
}
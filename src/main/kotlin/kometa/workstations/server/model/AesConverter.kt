package kometa.workstations.server.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Converter
@Component
class AesConverter(
    @Value("\${encryption.aes-key}") private val aesKey: String
) : AttributeConverter<String, String> {

    private val algorithm = "AES"
    private val key: ByteArray
        get() = aesKey.toByteArray()

    override fun convertToDatabaseColumn(attribute: String?): String? {
        if (attribute == null) return null
        return try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, algorithm))
            Base64.getEncoder().encodeToString(cipher.doFinal(attribute.toByteArray()))
        } catch (e: Exception) {
            throw IllegalStateException("Error encrypting data", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) return null
        return try {
            val cipher = Cipher.getInstance(algorithm)
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, algorithm))
            String(cipher.doFinal(Base64.getDecoder().decode(dbData)))
        } catch (e: Exception) {
            throw IllegalStateException("Error decrypting data", e)
        }
    }
}

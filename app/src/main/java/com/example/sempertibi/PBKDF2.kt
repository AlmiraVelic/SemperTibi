package com.example.sempertibi

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PBKDF2 {

    companion object {
        /*
      The salt value should be unique for each user and should be generated using a secure random number generator.
      This helps to prevent attackers from using precomputed tables of hashes to attack multiple passwords at once.
       */
        fun generateSalt(): ByteArray {
            val salt = ByteArray(16)
            SecureRandom().nextBytes(salt)
            return salt
        }

        /*
        hash the password using a key derivation function called PBKDF2
         */
        fun hashPassword(password: String, salt: ByteArray): ByteArray {
            val iterations = 10000
            val keyLength = 256
            val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength / 8)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            return factory.generateSecret(spec).encoded
        }
    }
}
/*
  fun verify(password: String, user: User): Boolean {
      val salt = user.salt
      val storedHash = user.passwoHash

      val hashSize = storedHash.size

      val iterations = 10000
      val keyLength = 256

      val generatedHash = ByteArray(hashSize)
      val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength/8)
      val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
      factory.generateSecret(spec).encoded.copyInto(generatedHash)

      return generatedHash.contentEquals(storedHash)
  }

  fun verify(password: String, hash: ByteArray, salt: ByteArray): Boolean {
      val testHash = hashPassword(password, salt)
      if (testHash.size != hash.size) {
          return false
      }
      var diff = 0
      for (i in hash.indices) {
          diff = diff or (hash[i].toInt() xor testHash[i].toInt())
      }
      return diff == 0


  }
}
}*/
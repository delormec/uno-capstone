using System;
using System.Security.Cryptography;
using System.Text;

/// <summary>
/// Class that provides static functions for encryption of passwords.
/// Conforms to Best Practices, code was taken from http://www.obviex.com/samples/hash.aspx.
/// </summary>
public class HOSTEncrpyption
{

    /// <summary>
    /// Used to generate 4-8 bytes worth of random salt. Uses RNGCryptoServiceProvider for random number generation.
    /// </summary>
    /// <returns></returns>
    public static byte[] getRandomSaltBytes()
    {
        // Define min and max salt sizes.
        int minSaltSize = 4;
        int maxSaltSize = 8;
        byte[] saltBytes;

        // Generate a random number for the size of the salt.
        Random random = new Random();
        int saltSize = random.Next(minSaltSize, maxSaltSize);

        // Allocate a byte array, which will hold the salt.
        saltBytes = new byte[saltSize];

        // Initialize a random number generator.
        RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();

        // Fill the salt with cryptographically strong byte values.
        rng.GetNonZeroBytes(saltBytes);

        return saltBytes;
    }

    /// <summary>
    /// Generates a hash for the given plain text value and returns a
    /// base64-encoded result. Before the hash is computed, a random salt
    /// is to the plain text. This salt is stored at
    /// the end of the hash value, so it can be used later for hash
    /// verification.
    /// </summary>
    /// <param name="plainText">
    /// Plaintext value to be hashed. The function does not check whether
    /// this parameter is null.
    /// </param>
    /// <param name="saltBytes">
    /// Salt bytes. 
    /// </param>
    /// <returns>
    /// Hash value formatted as a base64-encoded string.
    /// </returns>
    public static string ComputeHash(string plainText, byte[] saltBytes)
    {
        // Convert plain text into a byte array.
        byte[] plainTextBytes = Encoding.UTF8.GetBytes(plainText);

        // Allocate array, which will hold plain text and salt.
        byte[] plainTextWithSaltBytes =
                new byte[plainTextBytes.Length + saltBytes.Length];

        // Copy plain text bytes into resulting array.
        for (int i = 0; i < plainTextBytes.Length; i++)
            plainTextWithSaltBytes[i] = plainTextBytes[i];

        // Append salt bytes to the resulting array.
        for (int i = 0; i < saltBytes.Length; i++)
            plainTextWithSaltBytes[plainTextBytes.Length + i] = saltBytes[i];

        HashAlgorithm hash = new SHA512Managed();

        // Compute hash value of our plain text with appended salt.
        byte[] hashBytes = hash.ComputeHash(plainTextWithSaltBytes);

        // Create array which will hold hash and original salt bytes.
        byte[] hashWithSaltBytes = new byte[hashBytes.Length + saltBytes.Length];

        // Copy hash bytes into resulting array.
        for (int i = 0; i < hashBytes.Length; i++)
            hashWithSaltBytes[i] = hashBytes[i];

        // Append salt bytes to the result.
        for (int i = 0; i < saltBytes.Length; i++)
            hashWithSaltBytes[hashBytes.Length + i] = saltBytes[i];

        // Convert result into a base64-encoded string.
        string hashValue = Convert.ToBase64String(hashWithSaltBytes);

        // Return the result.
        return hashValue;
    }

    /// <summary>
    /// Compares a hash of the specified plain text value to a given hash
    /// value. Plain text is hashed with the same salt value as the original
    /// hash.
    /// </summary>
    /// <param name="plainText">
    /// Plain text to be verified against the specified hash. The function
    /// does not check whether this parameter is null.
    /// </param>
    /// <param name="hashValue">
    /// Base64-encoded hash value produced by ComputeHash function. This value
    /// includes the original salt appended to it.
    /// </param>
    /// <returns>
    /// If computed hash mathes the specified hash the function the return
    /// value is true; otherwise, the function returns false.
    /// </returns>
    public static bool VerifyHash(string plainText, string hashValue)
    {
        // Convert base64-encoded hash value into a byte array.
        byte[] hashWithSaltBytes = Convert.FromBase64String(hashValue);
        
        // We must know size of hash (without salt).
        int hashSizeInBytes;
        
        // Convert size of hash from bits to bytes.
        hashSizeInBytes = 512 / 8;

        // Make sure that the specified hash value is long enough.
        if (hashWithSaltBytes.Length < hashSizeInBytes)
            return false;

        // Allocate array to hold original salt bytes retrieved from hash.
        byte[] saltBytes = new byte[hashWithSaltBytes.Length - 
                                    hashSizeInBytes];

        // Copy salt from the end of the hash to the new array.
        for (int i=0; i < saltBytes.Length; i++)
            saltBytes[i] = hashWithSaltBytes[hashSizeInBytes + i];

        // Compute a new hash string.
        string expectedHashString = ComputeHash(plainText, saltBytes);

        // If the computed hash matches the specified hash,
        // the plain text value must be correct.
        return (hashValue == expectedHashString);
    }
}
package se.simbio.encryption;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import third.part.android.util.Base64;

public class Encryption {
    private final Builder mBuilder;

    public static class Builder {
        private String mAlgorithm;
        private int mBase64Mode;
        private String mCharsetName;
        private String mDigestAlgorithm;
        private int mIterationCount;
        private byte[] mIv;
        private IvParameterSpec mIvParameterSpec;
        private String mKey;
        private String mKeyAlgorithm;
        private int mKeyLength;
        private String mSalt;
        private String mSecretKeyType;
        private SecureRandom mSecureRandom;
        private String mSecureRandomAlgorithm;

        public static Builder getDefaultBuilder(String key, String salt, byte[] iv) {
            return new Builder().setIv(iv).setKey(key).setSalt(salt).setKeyLength(128).setKeyAlgorithm("AES").setCharsetName("UTF8").setIterationCount(1).setDigestAlgorithm("SHA1").setBase64Mode(0).setAlgorithm("AES/CBC/PKCS5Padding").setSecureRandomAlgorithm("SHA1PRNG").setSecretKeyType("PBKDF2WithHmacSHA1");
        }

        public Encryption build() throws NoSuchAlgorithmException {
            setSecureRandom(SecureRandom.getInstance(getSecureRandomAlgorithm()));
            setIvParameterSpec(new IvParameterSpec(getIv()));
            return new Encryption(this);
        }

        private String getCharsetName() {
            return this.mCharsetName;
        }

        public Builder setCharsetName(String charsetName) {
            this.mCharsetName = charsetName;
            return this;
        }

        private String getAlgorithm() {
            return this.mAlgorithm;
        }

        public Builder setAlgorithm(String algorithm) {
            this.mAlgorithm = algorithm;
            return this;
        }

        private String getKeyAlgorithm() {
            return this.mKeyAlgorithm;
        }

        public Builder setKeyAlgorithm(String keyAlgorithm) {
            this.mKeyAlgorithm = keyAlgorithm;
            return this;
        }

        private int getBase64Mode() {
            return this.mBase64Mode;
        }

        public Builder setBase64Mode(int base64Mode) {
            this.mBase64Mode = base64Mode;
            return this;
        }

        private String getSecretKeyType() {
            return this.mSecretKeyType;
        }

        public Builder setSecretKeyType(String secretKeyType) {
            this.mSecretKeyType = secretKeyType;
            return this;
        }

        private String getSalt() {
            return this.mSalt;
        }

        public Builder setSalt(String salt) {
            this.mSalt = salt;
            return this;
        }

        private String getKey() {
            return this.mKey;
        }

        public Builder setKey(String key) {
            this.mKey = key;
            return this;
        }

        private int getKeyLength() {
            return this.mKeyLength;
        }

        public Builder setKeyLength(int keyLength) {
            this.mKeyLength = keyLength;
            return this;
        }

        private int getIterationCount() {
            return this.mIterationCount;
        }

        public Builder setIterationCount(int iterationCount) {
            this.mIterationCount = iterationCount;
            return this;
        }

        private String getSecureRandomAlgorithm() {
            return this.mSecureRandomAlgorithm;
        }

        public Builder setSecureRandomAlgorithm(String secureRandomAlgorithm) {
            this.mSecureRandomAlgorithm = secureRandomAlgorithm;
            return this;
        }

        private byte[] getIv() {
            return this.mIv;
        }

        public Builder setIv(byte[] iv) {
            this.mIv = iv;
            return this;
        }

        private SecureRandom getSecureRandom() {
            return this.mSecureRandom;
        }

        public Builder setSecureRandom(SecureRandom secureRandom) {
            this.mSecureRandom = secureRandom;
            return this;
        }

        private IvParameterSpec getIvParameterSpec() {
            return this.mIvParameterSpec;
        }

        public Builder setIvParameterSpec(IvParameterSpec ivParameterSpec) {
            this.mIvParameterSpec = ivParameterSpec;
            return this;
        }

        private String getDigestAlgorithm() {
            return this.mDigestAlgorithm;
        }

        public Builder setDigestAlgorithm(String digestAlgorithm) {
            this.mDigestAlgorithm = digestAlgorithm;
            return this;
        }
    }

    public interface Callback {
        void onError(Exception exception);

        void onSuccess(String str);
    }


    private Encryption(Builder builder) {
        this.mBuilder = builder;
    }

    public static Encryption getDefault(String key, String salt, byte[] iv) {
        try {
            return Builder.getDefaultBuilder(key, salt, iv).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException {
        if (data == null) {
            return null;
        }
        SecretKey secretKey = getSecretKey(hashTheKey(this.mBuilder.getKey()));
        byte[] dataBytes = data.getBytes(this.mBuilder.getCharsetName());
        Cipher cipher = Cipher.getInstance(this.mBuilder.getAlgorithm());
        cipher.init(1, secretKey, this.mBuilder.getIvParameterSpec(), this.mBuilder.getSecureRandom());
        return Base64.encodeToString(cipher.doFinal(dataBytes), this.mBuilder.getBase64Mode());
    }

    public String encryptOrNull(String data) {
        try {
            return encrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void encryptAsync(final String data, final Callback callback) {
        if (callback != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String encrypt = Encryption.this.encrypt(data);
                        if (encrypt == null) {
                            callback.onError(new Exception("Encrypt return null, it normally occurs when you send a null data"));
                        }
                        callback.onSuccess(encrypt);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }).start();
        }
    }

    public String decrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (data == null) {
            return null;
        }
        byte[] dataBytes = Base64.decode(data, this.mBuilder.getBase64Mode());
        SecretKey secretKey = getSecretKey(hashTheKey(this.mBuilder.getKey()));
        Cipher cipher = Cipher.getInstance(this.mBuilder.getAlgorithm());
        cipher.init(2, secretKey, this.mBuilder.getIvParameterSpec(), this.mBuilder.getSecureRandom());
        return new String(cipher.doFinal(dataBytes));
    }

    public String decryptOrNull(String data) {
        try {
            return decrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void decryptAsync(final String data, final Callback callback) {
        if (callback != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String decrypt = Encryption.this.decrypt(data);
                        if (decrypt == null) {
                            callback.onError(new Exception("Decrypt return null, it normally occurs when you send a null data"));
                        }
                        callback.onSuccess(decrypt);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }).start();
        }
    }

    private SecretKey getSecretKey(char[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        return new SecretKeySpec(SecretKeyFactory.getInstance(this.mBuilder.getSecretKeyType()).generateSecret(new PBEKeySpec(key, this.mBuilder.getSalt().getBytes(this.mBuilder.getCharsetName()), this.mBuilder.getIterationCount(), this.mBuilder.getKeyLength())).getEncoded(), this.mBuilder.getKeyAlgorithm());
    }

    private char[] hashTheKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(this.mBuilder.getDigestAlgorithm());
        messageDigest.update(key.getBytes(this.mBuilder.getCharsetName()));
        return Base64.encodeToString(messageDigest.digest(), 1).toCharArray();
    }
}
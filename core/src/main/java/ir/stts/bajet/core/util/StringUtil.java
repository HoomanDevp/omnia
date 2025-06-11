package ir.stts.bajet.core.util;

import org.springframework.util.ObjectUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtil {

    private static final Map<Character, Character> PERSIAN_CHAR_MAP = new HashMap<>();

    static {
        // تبدیل حروف عربی به فارسی
        PERSIAN_CHAR_MAP.put('ي', 'ی');
        PERSIAN_CHAR_MAP.put('ك', 'ک');
        PERSIAN_CHAR_MAP.put('ة', 'ه');
        PERSIAN_CHAR_MAP.put('ؤ', 'و');
        PERSIAN_CHAR_MAP.put('إ', 'ا');
        PERSIAN_CHAR_MAP.put('أ', 'ا');
        PERSIAN_CHAR_MAP.put('ئ', 'ی');
    }

    public static String maskPan(String pan) {

        String start = pan.substring(0, 4);
        String end = pan.substring(pan.length() - 2);
        return start + "**********" + end;
    }

    // Reverse string in blocks of specified size
    public static String inverseByBase(String st, int moveBase) {

        StringBuilder sb = new StringBuilder();
        st = convertToLetterDigit(st);
        int c;
        for (int i = 0; i < st.length(); i += moveBase) {
            if (i + moveBase > st.length() - 1)
                c = st.length() - i;
            else
                c = moveBase;
            sb.append(new StringBuilder(st.substring(i, i + c)).reverse().toString());
        }

        return sb.toString().toUpperCase();
    }

    // Shuffle characters in the string based on their ASCII values
    public static String boring(String st) {

        int newPlace;
        char ch;
        StringBuilder sb = new StringBuilder(st);
        for (int i = 0; i < sb.length(); i++) {

            newPlace = i * (int) sb.charAt(i);
            newPlace = newPlace % sb.length();
            ch = sb.charAt(i);
            sb.deleteCharAt(i);
            sb.insert(newPlace, ch);
        }

        return sb.toString().toUpperCase();
    }

    // Generate a password from a string and an identifier
    public static String makePassword(String st, String identifier) {

        if (identifier.length() != 3)
            throw new IllegalArgumentException("Identifier must be exactly three characters.");

        int[] num = new int[3];
        num[0] = Character.getNumericValue(identifier.charAt(0));
        num[1] = Character.getNumericValue(identifier.charAt(1));
        num[2] = Character.getNumericValue(identifier.charAt(2));
        st = boring(st);
        st = inverseByBase(st, num[0]);
        st = inverseByBase(st, num[1]);
        st = inverseByBase(st, num[2]);
        StringBuilder sb = new StringBuilder();
        for (char ch : st.toCharArray())
            sb.append(changeChar(ch, num));

        return md5Encryptor(sb.toString(), (short) sb.length()).toUpperCase();
    }

    // MD5 encryption
    private static String md5Encryptor(String st, short length) {

        String strBase = st;
        try {

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            strBase = Base64.getEncoder().encodeToString(md5.digest(strBase.getBytes()));
            strBase = removeUseLess(strBase);
            if (strBase.length() < length)
                return strBase + md5Encryptor(st, (short) (length - strBase.length()));

            return strBase.substring(0, length);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String removeUseLess(String st) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < st.length(); i++) {

            char ch = Character.toUpperCase(st.charAt(i));
            if ((ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9'))
                sb.append(ch);
        }

        return sb.toString();
    }

    private static String convertToLetterDigit(String st) {

        StringBuilder sb = new StringBuilder();
        for (char ch : st.toCharArray()) {

            if (!Character.isLetterOrDigit(ch))
                sb.append((int) ch);
            else
                sb.append(ch);
        }

        return sb.toString();
    }

    private static char changeChar(char ch, int[] enCode) {

        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'H')
            return (char) (ch + 2 * enCode[0]);
        else if (ch >= 'I' && ch <= 'P')
            return (char) (ch - enCode[2]);
        else if (ch >= 'Q' && ch <= 'Z')
            return (char) (ch - enCode[1]);
        else if (ch >= '0' && ch <= '4')
            return (char) (ch + 5);
        else if (ch >= '5' && ch <= '9')
            return (char) (ch - 5);
        else
            return '0';
    }

    public static String arrange(String shuffled, long seed) {

        int shuffledLength = shuffled.length() / 2;
        List<Integer> mapping = IntStream.range(0, shuffledLength).boxed().collect(Collectors.toList());
        Random random = new Random(seed);

        Collections.shuffle(mapping, random);

        String[] arrangedTextArray = new String[shuffledLength];
        for (int i = 0; i < shuffled.length(); i += 2)
            arrangedTextArray[mapping.get(i / 2)] = new String(new BigInteger(String.valueOf(shuffled.charAt(i)) + shuffled.charAt(i + 1), 16).toByteArray(), StandardCharsets.UTF_8);

        return Arrays.stream(arrangedTextArray)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String shuffle(String plaintext, long seed) {

        if (ObjectUtils.isEmpty(seed) || ObjectUtils.isEmpty(plaintext))
            return null;

        Random random = new Random(seed);

        List<String> plainTextCharList = Arrays.asList(plaintext.split(""));
        Collections.shuffle(plainTextCharList, random);
        String shuffledText = plainTextCharList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        StringBuilder shuffledHexText = new StringBuilder();
        byte[] shuffledBytes = shuffledText.getBytes(StandardCharsets.UTF_8);
        for (byte b : shuffledBytes)
            shuffledHexText.append(String.format("%02x", b));

        return shuffledHexText.toString();
    }


    private static char reverseChangeChar(char ch, int[] enCode) {

        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'H')
            return (char) (ch - 2 * enCode[0]);
        else if (ch >= 'I' && ch <= 'P')
            return (char) (ch + enCode[2]);
        else if (ch >= 'Q' && ch <= 'Z')
            return (char) (ch + enCode[1]);
        else if (ch >= '0' && ch <= '4')
            return (char) (ch - 5);
        else if (ch >= '5' && ch <= '9')
            return (char) (ch + 5);
        else
            return '0';
    }

    // Reverse block inversion
    private static String reverseInverseByBase(String st, int moveBase) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < st.length(); i += moveBase) {

            int c;
            if (i + moveBase > st.length() - 1)
                c = st.length() - i;
            else
                c = moveBase;
            sb.append(new StringBuilder(st.substring(i, i + c)).reverse().toString());
        }

        return sb.toString().toUpperCase();
    }

    // Main decryption process
    public static String decrypt(String encryptedText, String identifier) {

        if (identifier.length() != 3)
            throw new IllegalArgumentException("Identifier must be exactly three characters.");

        int[] num = new int[3];
        num[0] = Character.getNumericValue(identifier.charAt(0));
        num[1] = Character.getNumericValue(identifier.charAt(1));
        num[2] = Character.getNumericValue(identifier.charAt(2));

        StringBuilder sb = new StringBuilder();
        for (char ch : encryptedText.toCharArray())
            sb.append(reverseChangeChar(ch, num));

        String intermediateText = sb.toString();

        intermediateText = reverseInverseByBase(intermediateText, num[2]);
        intermediateText = reverseInverseByBase(intermediateText, num[1]);
        intermediateText = reverseInverseByBase(intermediateText, num[0]);

        return intermediateText;
    }

    // تابعی برای نرمال‌سازی رشته‌ها
    public static String normalizeString(String text) {

        StringBuilder normalized = new StringBuilder();

        // حذف نیم‌فاصله و فاصله‌های غیرضروری
        text = text.replaceAll("\\s+", "").replaceAll("\\u200C", "");

        // تبدیل حروف عربی به معادل فارسی
        for (char ch : text.toCharArray())
            normalized.append(PERSIAN_CHAR_MAP.getOrDefault(ch, ch));

        // حذف علائم تشکیل‌دهنده (مثلاً اعراب‌ها)
        return Normalizer.normalize(normalized.toString(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    // تابع محاسبه Levenshtein Distance برای شباهت متنی
    public static int levenshteinDistance(String s1, String s2) {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {

                if (i == 0)
                    dp[i][j] = j;
                else if (j == 0)
                    dp[i][j] = i;
                else
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1]
                            + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1), dp[i - 1][j] + 1), dp[i][j - 1] + 1);
            }
        }

        return dp[s1.length()][s2.length()];
    }

    // تابعی برای بررسی شباهت بین دو شهر
    public static boolean isSimilar(String city1, String city2) {

        city1 = normalizeString(city1);
        city2 = normalizeString(city2);

        int distance = levenshteinDistance(city1, city2);

        // اگر فاصله کمتر از یک آستانه معین باشد، دو شهر مشابه در نظر گرفته می‌شوند
        return distance <= Math.max(city1.length(), city2.length()) / 4; // مثال: 25% تفاوت مجاز است
    }

    public static String convertPersianDigitsToEnglish(String input) {

        if (input == null)
            return null;

        // جایگزینی کاراکترهای فارسی با انگلیسی
        //noinspection UnnecessaryUnicodeEscape
        String[] persianDigits = {"\u06F0", "\u06F1", "\u06F2", "\u06F3", "\u06F4", "\u06F5", "\u06F6", "\u06F7", "\u06F8", "\u06F9"};
        String[] englishDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        String result = input;

        for (int i = 0; i < persianDigits.length; i++)
            result = result.replace(persianDigits[i], englishDigits[i]);

        return result;
    }
}
package com.gongsibao.sms.utils.security;



import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.gongsibao.sms.utils.DateUtils;



public class SecurityUtils {
    //    private static Logger logger = Logger.getLogger(SecurityUtils.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String RC4_DEFAULT_PASSWORD = "3984aF333#@213";
    private static final String PASSWD_SALT = "user!@#123";
    private static final String PASSWD_SALT_ACCOUNT = "abc!@#123";
    private static final String PASSWD_SALT_JNZ = "jnz!@#123";

    /**
     * 取MD5加密的secretKey的后8位当做密钥
     *
     * @param sourceSecretKey
     * @return
     */
    private static String getSecretKey(String sourceSecretKey) {
        String md5secretKey = MD5Encode(sourceSecretKey);
        return md5secretKey.substring(md5secretKey.length() - 8);
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String MD5Encode(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String MD5Encode(String str, String encoding) {
        if (str == null) return null;

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(str.getBytes(encoding)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String MD5Encode(InputStream input) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte[] b = new byte[1024 * 10];
            int length = -1;

            while ((length = input.read(b)) > -1) {
                messagedigest.update(b, 0, length);
            }
            return bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("InputStream md5 name error");
        }
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        String c0 = hexDigits[(bt & 0xf0) >> 4];
        String c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static final char[] c = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz".toCharArray();
    private static final char[] c1 = "0123456789".toCharArray();
    private static final char[] c2 = "abcdefghijkmnpqrstuvwxyz".toCharArray();

    /**
     * 获得指定长度的随机字符串
     *
     * @param len
     * @return
     */
    public static String getRandomString(int len) {
        return getRandomString(len, null);
    }

    public static String getRandomString(int len, char[] carr) {
        carr = null == carr ? c : carr;
        if (len < 1) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(carr[random.nextInt(carr.length)]);
        }
        return sb.toString();
    }

    public static String getRandomPasswd() {
        StringBuilder passwd = new StringBuilder();

        passwd.append(getRandomString(2, c2).toLowerCase());
        passwd.append(getRandomString(6, c1));

        return passwd.toString();
    }


    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 加密
     * 加密字符串和原字符串长度基本相同
     * 主要针对页面ID的加密
     *
     * @param source
     * @return
     */
    public static String rc4Encrypt(Object source) {
        String encrypt = "";
        try {
            encrypt = Base64.encode(RC4.RC4encrypt(RC4_DEFAULT_PASSWORD.getBytes(DEFAULT_CHARSET),
                    String.valueOf(source).getBytes(DEFAULT_CHARSET)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encrypt.replaceAll("/", "_").replaceAll("\\+", "-").replaceAll("=", "\\$");
    }

    /**
     * 解密
     * 加密字符串和原字符串长度基本相同
     * 主要针对页面ID的加密
     *
     * @return
     */
    public static String rc4Decrypt(Object encryptObj) {
        return rc4Decrypt(StringUtils.trimToEmpty(encryptObj));
    }

    public static List<Integer> rc4Decrypt(String[] encryptObj) {
        List<Integer> list = new ArrayList<Integer>();
        if (ArrayUtils.isEmpty(encryptObj)) {
            return list;
        }

        for (String s : encryptObj) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            int id = NumberUtils.toInt(rc4Decrypt(s));
            if (id == 0) {
                continue;
            }
            list.add(id);
        }

        return list;
    }

    /**
     * 解密
     * 加密字符串和原字符串长度基本相同
     * 主要针对页面ID的加密
     *
     * @return
     */
    public static String rc4Decrypt(String encryptStr) {
        if (StringUtils.isBlank(encryptStr)) {
            return "";
        }
        encryptStr = StringUtils.trimToEmpty(encryptStr);
        encryptStr = encryptStr.replaceAll("_", "/").replaceAll("-", "+").replaceAll("\\$", "=").replaceAll("\\~", "=");
        String decrypt = "";
        try {
            decrypt = new String(RC4.RC4encrypt(RC4_DEFAULT_PASSWORD.getBytes(DEFAULT_CHARSET),
                    Base64.decode(encryptStr)), DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decrypt;
    }

    public static List<Integer> rc4DecryptBatch(String[] idStrs) {
        List<Integer> list = new ArrayList<Integer>();
        if (ArrayUtils.isEmpty(idStrs)) {
            return list;
        }

        for (String idStr : idStrs) {
            int id = NumberUtils.toInt(rc4Decrypt(idStr));
            if (id > 0) {
                list.add(id);
            }
        }

        return list;
    }

    public static String contactMd5(String contact) {
        return passwdMd5(contact, "ESo1!@$#!weqafklweq");
    }

    /**
     * 生成密码
     *
     * @param passwd
     * @return
     */
    public static String passwdMd5(String passwd) {
        return passwdMd5(passwd, PASSWD_SALT);
    }

    public static String passwdAccountMD5(String passwd) {
        return passwdMd5(passwd, PASSWD_SALT_ACCOUNT);
    }
    public static String passwdJNZMD5(String passwd) {
        return passwdMd5(passwd, PASSWD_SALT_JNZ);
    }

    public static String passwdMd5(String passwd, String salt) {
        if (StringUtils.isBlank(passwd)) {
            return "";
        }

        String pwdMD5 = MD5Encode(passwd + salt).toLowerCase();
        pwdMD5 = pwdMD5.substring(8);
        pwdMD5 = pwdMD5.substring(0, 16);
        return pwdMD5;
    }


    public static void main(String[] args) {

        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(22);
        list.add(5);
        list.add(2);
        list.add(4);
        list.add(6);
        list.add(11);
        list.add(67);

        List<Integer> list2 = new ArrayList<Integer>(list);

        list2.clear();
        System.out.println(list);
//        Iterator<Integer> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            Integer next = iterator.next();
//            if (next % 2 == 0) {
//                iterator.remove();
//            }
//        }

//        for (Integer num : list) {
//            if (num % 2 == 0) {
//                list.remove(num);
//                System.out.println(num);
//            }
//        }
//        for (int i = 0; i < list.size(); i ++ ) {
//            int num = list.get(i);
//            if (num % 2 == 0) {
//                list.removeAll(Arrays.asList(num));
//                System.out.println(num);
//            }
//        }
//
//
//        System.out.println(list);
//
//        System.out.println(rc4Decrypt("0nZ72d0$"));
//
//        Date today = new Date();
//        Date monthBegin = DateUtils.getMonthBegin(today);
//        Date monthEnd = DateUtils.getMonthEnd(today);
//
//        System.out.println(DateUtils.dateStr(monthBegin, "yyyy-MM-dd HH:mm:ss"));
//        System.out.println(DateUtils.dateStr(monthEnd, "yyyy-MM-dd HH:mm:ss"));
//
//        System.out.println(RegexUtils.isPhone("1160044811"));

        System.out.println(rc4Encrypt(260632));

        for (int i = 0; i < list.size(); i ++ ) {
            int num = list.get(i);
            if (num % 2 == 0) {
                list.removeAll(Arrays.asList(num));
                System.out.println(num);
            }
        }

        System.out.println(list);

        System.out.println(rc4Decrypt("0nZ72d0$"));

        Date today = new Date();
        Date monthBegin = DateUtils.getMonthBegin(today);
        Date monthEnd = DateUtils.getMonthEnd(today);

        System.out.println(DateUtils.dateStr(monthBegin, "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateUtils.dateStr(monthEnd, "yyyy-MM-dd HH:mm:ss"));

        System.out.println(passwdAccountMD5("1"));
        System.out.println(rc4Encrypt(3048));
        System.out.println(rc4Decrypt("1HR-3A$$"));

        List<String> ll = new ArrayList<String>();
        for (Integer integer : Arrays.asList(1137, 1177, 1823, 1514, 1108, 1424, 1524, 1525, 1147, 1134, 1099, 1274)) {
            ll.add(rc4Encrypt(integer));
        }
        System.out.println(StringUtils.join(ll, ","));
    }
}

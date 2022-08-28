package org.dol.database.utils;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static final  String SPLIT_PROPERTY_REGX = "[;；,，|\\s]";
    public static final  String STR_COMMA           = ",";
    public static final  String STR_DOT             = ".";
    public static final  String EMPTY_STRING        = "";
    public static final  String LETTER_NUMBER_CHAR  = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final  String LETTER_CHAR         = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final  String NUMBER_CHAR         = "0123456789";
    private static final String ALL_CHAR            = "ABCDEFGHIJKLMNOPQRSTUVWXYabcdefghijklmnopqrstuvwxy0123456789~!@#$%^&*()_-+=";

    public static boolean hasText(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    public static List<String> splitName(String name) {
        List<String> parts = new ArrayList<>();
        if (name.contains("_")) {
            String[] ss = name.split("_");
            for (String s : ss) {
                if (s.trim().length() > 0) {
                    parts.addAll(splitName(s));
                }
            }
            return parts;
        }
        int pre = 0;
        char c = name.charAt(0);
        boolean preIsDigit = Character.isDigit(c);
        boolean preIsUpperCase = !preIsDigit && Character.isUpperCase(c);

        for (int i = 0; i < name.length(); i++) {
            c = name.charAt(i);
            boolean isDigit = Character.isDigit(c);
            boolean isUpperCase = Character.isUpperCase(c);
            if (isDigit || isUpperCase) {
                if (!preIsDigit && !preIsUpperCase) {
                    String part = name.substring(pre, i);
                    pre = i;
                    parts.add(part);
                }
            } else {
                if (preIsDigit) {
                    String part = name.substring(pre, i);
                    parts.add(part);
                    pre = i;
                } else if (preIsUpperCase && (i - pre) > 1) {
                    String part = name.substring(pre, i - 1);
                    parts.add(part);
                    pre = i - 1;
                }
            }
            preIsDigit = isDigit;
            preIsUpperCase = isUpperCase;
        }
        if (pre < name.length()) {
            parts.add(name.substring(pre));
        }
        return parts;
    }

    public static void main(String[] args) {
        System.out.println(splitName("user2name"));
        System.out.println(splitName("userName"));
        System.out.println(splitName("userANDName"));
        System.out.println(splitName("userID"));
        System.out.println(splitName("user2Name"));
        System.out.println(splitName("user_Name"));
        System.out.println(splitName("user_name"));
        System.out.println(splitName("_user_name"));
        System.out.println(splitName("_user_2_name_A"));
    }


    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    public static boolean isEmpty(Collection<?> children) {
        return children == null || children.isEmpty();
    }

    public static boolean notEmpty(Object[] parameters) {
        return !isEmpty(parameters);
    }

    public static boolean notEmpty(Collection<?> parameters) {
        return !isEmpty(parameters);
    }

    public static boolean isEmpty(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return true;
        }
        for (Object parameter : parameters) {
            if (parameter != null) {
                return false;
            }
        }
        return true;
    }

    public static <T> T ifNull(T val, T defaultValue) {
        return val != null ? val : defaultValue;
    }


    public static int len(Object obj) {
        return obj == null ? 0 : obj.toString().length();
    }

    public static String padLeft(String str, int len, char paddingChar) {
        int needPaddingChars = len - str.length();
        if (needPaddingChars > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < needPaddingChars; i++) {
                sb.append(paddingChar);
            }
            sb.append(str);
            return sb.toString();
        } else {
            return str;
        }
    }

    public static String padRight(String str, int len, char paddingChar) {
        int needPaddingChars = len - str.length();
        if (needPaddingChars > 0) {
            StringBuilder sb = new StringBuilder(str);
            for (int i = 0; i < needPaddingChars; i++) {
                sb.append(paddingChar);
            }
            return sb.toString();
        } else {
            return str;
        }
    }


    public static boolean isEmptyString(Object value) {
        return (value instanceof CharSequence) && isEmpty(value.toString());
    }

    public static boolean isInteger(String no) {
        try {
            Integer.valueOf(no);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public static String randString(int count) {
        return randString(count, ALL_CHAR);
    }


    public static String randomLetterString(int count) {
        return randString(count, LETTER_CHAR);
    }


    public static String randomNumString(int count) {
        return randString(count, NUMBER_CHAR);
    }

    public static String randString(int count, String chars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb.toString();
    }


    public static String randomMobile() {
        return "1" + randomNumString(10);
    }

    public static String randomEmail() {
        return randomLetterString(6) + "@" + randomLetterString(6) + ".com";
    }

    public static String randomURL() {
        return "http://www." + randomLetterString(6) + ".com/" + randomLetterString(6);
    }

    public static String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        String pre = parts[0];
        sb.append(pre);
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (pre.endsWith("/")) {
                if (part.startsWith("/")) {
                    sb.append(part.substring(1));
                } else {
                    sb.append(part);
                }
            } else {
                if (part.startsWith("/")) {
                    sb.append(part);
                } else {
                    sb.append("/").append(part);
                }
            }
            pre = part;
        }
        return sb.toString().replaceAll("//", "/");
    }

    public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationClazz) {
        if (clazz == null || Object.class == clazz) {
            return false;
        }
        if (clazz.isAnnotationPresent(annotationClazz)) {
            return true;
        }
        if (isAnnotationPresent(clazz.getSuperclass(), annotationClazz)) {
            return true;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (isAnnotationPresent(anInterface, annotationClazz)) {
                return true;
            }
        }
        return false;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationClazz) {
        if (clazz == null || Object.class == clazz) {
            return null;
        }
        A annotation = clazz.getAnnotation(annotationClazz);
        if (annotation != null) {
            return annotation;
        }
        annotation = getAnnotation(clazz.getSuperclass(), annotationClazz);
        if (annotation != null) {
            return annotation;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            annotation = getAnnotation(anInterface, annotationClazz);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    public static String randomIPv4() {
        return ThreadLocalRandom.current().nextLong(1, 255)
                + "." + ThreadLocalRandom.current().nextLong(1, 255)
                + "." + ThreadLocalRandom.current().nextLong(1, 255)
                + "." + ThreadLocalRandom.current().nextLong(1, 255);
    }

    public static String join(Collection<?> values, char c) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            if (value == null || value.toString().trim().length() == 0) {
                continue;
            }
            sb.append(value).append(c);
        }
        return sb.length() == 0 ? "" : sb.substring(0, sb.length() - 1);
    }


}

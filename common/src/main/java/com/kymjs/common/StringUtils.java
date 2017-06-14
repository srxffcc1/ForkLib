/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kymjs.common;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包<br>
 * <p>
 * <b>创建时间</b> 2014-8-14
 *
 * @author kymjs (https://github.com/kymjs)
 * @version 1.1
 */
public class StringUtils {

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence... strs) {
        for (CharSequence str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * String转long
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * String转double
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static double toDouble(String obj) {
        try {
            return Double.parseDouble(obj);
        } catch (Exception e) {
        }
        return 0D;
    }

    /**
     * 字符串转布尔
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断一个字符串是不是数字
     */
    public static boolean isNumber(CharSequence str) {
        try {
            Integer.parseInt(str.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * byte[]数组转换为16进制的字符串。
     *
     * @param data 要转换的字节数组。
     * @return 转换后的结果。
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 16进制表示的字符串转换为字节数组。
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return d;
    }

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern phone = Pattern
            .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");


    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(CharSequence email) {
        if (isEmpty(email))
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(CharSequence phoneNum) {
        if (isEmpty(phoneNum))
            return false;
        return phone.matcher(phoneNum).matches();
    }


    /**
     * 将123转成竖直的一百二十三
     *
     * @param d
     * @return
     */
    public static String int2String(int d) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        String[] str = {"零", "一", "二", "三", "四", "伍", "六", "七", "八", "九"};
        String ss[] = new String[]{"", "十", "百", "千", "万", "十", "百", "千", "亿"};
        String s = String.valueOf(d);

        for (int i = 0; i < s.length(); i++) {
            String index = String.valueOf(s.charAt(i));
            sb = sb.append(str[Integer.parseInt(index)]);
        }
        String sss = String.valueOf(sb);

        int i = 0;
        for (int j = sss.length(); j > 0; j--) {
            sb = sb.insert(j, ss[i++]);
        }
        char[] tmp = sb.toString().toCharArray();
        String[] tmp2 = new String[tmp.length];
        for (int j = 0; j < tmp2.length; j++) {
            if (j != tmp2.length - 1) {
                tmp2[j] = tmp[j] + "\n";
            } else {
                tmp2[j] = tmp[j] + "";
            }
            result = result + tmp2[j];
        }
        if (s.length() == 2) {
            if (s.substring(1, 2).equals("0")) {
                result = result.substring(2, 4);
            } else {
                result = result.substring(2, result.length());
            }

        }
        return result;
    }

    /**
     * // 将16进制数转换为汉字
     *
     * @param hex
     * @return
     */
    public static String hexs2Chineses(String hex) {
        String enUnicode = null;
        String deUnicode = null;
        for (int i = 0; i < hex.length(); i++) {
            if (enUnicode == null) {
                enUnicode = String.valueOf(hex.charAt(i));
            } else {
                enUnicode = enUnicode + hex.charAt(i);
            }
            if (i % 4 == 3) {
                if (enUnicode != null) {
                    if (deUnicode == null) {
                        deUnicode = String.valueOf((char) Integer.valueOf(
                                enUnicode, 16).intValue());
                    } else {
                        deUnicode = deUnicode
                                + String.valueOf((char) Integer.valueOf(
                                enUnicode, 16).intValue());
                    }
                }
                enUnicode = null;
            }

        }
        return deUnicode;
    }

    /**
     * 将汉字转换为16进制数
     *
     * @param chinese
     * @return
     */
    public static String chineses2Hexs(String chinese) {
        String enUnicode = null;
        for (int i = 0; i < chinese.length(); i++) {
            if (i == 0) {
                enUnicode = hex2Chinese(Integer.toHexString(chinese.charAt(i))
                        .toUpperCase());
            } else {
                enUnicode = enUnicode
                        + hex2Chinese(Integer.toHexString(chinese.charAt(i))
                        .toUpperCase());
            }
        }
        return enUnicode;
    }

    private static String hex2Chinese(String hexString) {
        String hexStr = "";
        for (int i = hexString.length(); i < 4; i++) {
            if (i == hexString.length())
                hexStr = "0";
            else
                hexStr = hexStr + "0";
        }
        return hexStr + hexString;
    }

    /**
     * 对不可变得数组进行包装 实现可以增加
     *
     * @param stringorg
     * @param target
     * @return
     */
    public static String[] add(String[] stringorg, String target) {
        String[] result = null;
        if (stringorg == null) {
            result = new String[]{target};

        } else {
            result = new String[stringorg.length + 1];
            for (int i = 0; i < stringorg.length; i++) {
                result[i] = stringorg[i];
            }
            result[result.length - 1] = target;
        }
        return result;
    }

    /**
     * 对不可变得数组进行包装 实现可以增加
     *
     * @param stringorg
     * @param target
     * @return
     */
    public static String[] add(String[] stringorg, String[] target) {
        String[] result = null;
        if (stringorg == null) {
            if (target != null) {
                result = target;
            } else {

            }


        } else {
            result = new String[stringorg.length + target.length];
            for (int i = 0; i < stringorg.length; i++) {
                result[i] = stringorg[i];
            }
            for (int i = stringorg.length; i < result.length; i++) {
                result[i] = target[i - stringorg.length];
            }

        }
        return result;
    }

    /**
     * 判断是否包含在list里
     *
     * @param checklist
     * @param key
     * @return
     */
    public static boolean isInList(List<String> checklist, String key) {
        boolean result = false;
        for (int i = 0; i < checklist.size(); i++) {
            if (checklist.get(i).equals(key)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 高亮关键字
     *
     * @param text
     * @param target
     * @return
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        text = text == null ? "" : text;
        target = target == null ? "" : target;
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        if (!text.equals("") && !target.equals("")) {
            CharacterStyle span = null;

            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(text);
            while (m.find()) {
                span = new ForegroundColorSpan(Color.RED);// 需要重复！
                spannable.setSpan(span, m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {

        }

        return spannable;
    }

    /**
     * 转义特殊字符
     *
     * @param keyword
     * @return
     */
    public static String washString(String keyword) {
        if (keyword != null && !keyword.equals("")) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]",
                    "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    /**
     * 中文字符转拼音
     *
     * @param chineses
     * @return
     */
    public static String chinese2Pinyin(String chineses) {
        long old=System.currentTimeMillis();
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String targetchinese = Pinyin.toPinyin(chineses, "");

//        char[] chinesechar = chinesestr.toCharArray();
//        try {
//            for (int i = 0; i < chinesechar.length; i++) {
//
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        if (targetchinese.length() > 60) {
            targetchinese = targetchinese.substring(0, 60);
        }

        return targetchinese.toLowerCase();
    }
}

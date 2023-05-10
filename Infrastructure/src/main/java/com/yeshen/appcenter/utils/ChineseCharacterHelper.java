package com.yeshen.appcenter.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.util.StringUtils;

/**
 * Date 2022/09/22  16:26
 * author  by HuBingKuan
 */
public class ChineseCharacterHelper {
    public static String getHeadPinyin(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        for (final char characters : chars) {
            String[] strings = PinyinHelper.toHanyuPinyinStringArray(characters);
            if(strings!=null&&strings.length>0){
                sb.append(strings[0].substring(0, 1));
            }
        }
        if(sb.toString().length()==0){
            return StringUtils.trimAllWhitespace(str);
        }
        return sb.toString();
    }
}
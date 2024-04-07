package com.example.delivery.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

public class SentenceUtils {
    public static String getKeyword(String var){
        String room;
        List<Term> words = HanLP.segment(var);
        boolean flag = false;
        for(Term term: words){
            if(term.nature == Nature.v){
                flag = true;
            }
            else if(term.nature == Nature.m){
                if(flag){
                    room = term.word;
                    return room;
                }
            }
            else{
                flag = false;
            }
        }
        return "没听到房间号";
    }

    private static Boolean inText(Term keyword, List<Term> words) {
        for(Term word: words) {
            if (keyword.word.equals(word.word)) {
                words.remove(word);
                return true;
            }
        }
        return false;
    }

    public static Boolean checkText(String text, String keyText) {
        HanLP.Config.ShowTermNature = false;
        List<Term> words = HanLP.segment(text);
        List<Term> dict = HanLP.segment(keyText);
        for(Term keyword : dict) {
            Boolean tag = inText(keyword, words);
            if(!tag) {
                return false;
            }
        }
        return true;
    }
}

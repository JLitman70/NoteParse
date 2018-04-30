package com.example.john.noteparse;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

/**
 * Created by John on 4/24/2018.
 * The following is a text parser that takes the text generated from the OCR and creates a mark-up
 * version of the string
 */

public class Parser {

    Parser(){}

    SpannableString Parse(SpannableString s) {

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '@' && s.charAt(i + 1) != '/') {
                if (s.subSequence(i, i + 1).toString().equals("@t")) {
                        // s.setSpan(new StyleSpan(Typeface.BOLD),i+2,j-1,0);}
                    }
            }

        }

        return s;
    }
}
package com.infoline.doctorcha.presentation.bean;

/**
 * Created by Administrator on 2017-01-11.
 */

public class BeanSpinnerPair2 {
    private int value;
    private String text;

    public BeanSpinnerPair2(final int value, final String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }

    //1. spinner에 표현하기 위해 요거이가 중요하다
    @Override
    public String toString() {
        return getText();
    }
}
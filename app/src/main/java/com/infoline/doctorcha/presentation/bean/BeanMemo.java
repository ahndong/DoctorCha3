package com.infoline.doctorcha.presentation.bean;

/**
 * Created by Administrator on 2016-10-21.
 */

public class BeanMemo {
    public int writer_id;
    public int target_id;
    public int id;
    public String mm;
    public String ud;
    public int vhm; //view holder mode mode : -1.empty, 0.nomal, 1.edit, 2.new

    public BeanMemo() {
        this.mm = "";
        this.ud = "2017.01.01";
    }
}

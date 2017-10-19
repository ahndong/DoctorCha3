package com.infoline.doctorcha.presentation.bean;

import java.util.List;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanCarInfoAndCarOptions {
    public List<BeanCarInfo> d1;
    public List<BeanCarOption> d2;

    public BeanCarInfoAndCarOptions() {

    }

    public BeanCarInfoAndCarOptions(List<BeanCarInfo> d1, List<BeanCarOption> d2) {
        this.d1 = d1;
        this.d2 = d2;
    }
}
package com.infoline.doctorcha.presentation.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanCarAreaPhoto {
    public int id;
    public String nm;
    public String url; //server url 또는 media uri 또는 media realPath

    //1. 낸중에 정리하자
    //2. server file 삭제
    //3. 삭제 button click시 이전 이미지 복원 등
    //public boolean isDeleted;

    public BeanCarAreaPhoto() {

    }

    public BeanCarAreaPhoto(int id, String nm, String url) {
        this.id = id;
        this.nm = nm;
        this.url = url;
    }
}
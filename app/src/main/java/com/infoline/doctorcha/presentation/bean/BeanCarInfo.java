package com.infoline.doctorcha.presentation.bean;

/**
 * Created by Administrator on 2015-07-01.
 */

public class BeanCarInfo {
    public int member_id;
    public String cdk = ""; //car detail kind
    public int ccc; //car cc
    public String cdg = ""; //car detail grade
    public String crn = ""; //car reg number
    public String crd = ""; //car reg date
    public String coy = ""; //car out year
    public String cbn = ""; //car body number
    public int ckm; //car killo meter
    public int ctk; //car transmission kind - 상수정의
    public int cpk; //car pure kind - 상수정의
    public String cos = ""; //car options - 1,0,1,1,0,0,0,1,0(9개 고정)
    public String car = ""; //car address - 차량 판매 지역
    public int cak; //car accident kind - 상수정의(무사고,유사고,잘모름)
    public int cwk; //car water kind - 상수정의(침수없슴,침수있슴,잘모름)
    public String cem = ""; //car etc memo
    public String cps = ""; //car photos - 1,0,1,1,0,0,0,1,0(9개 고정)

    public BeanCarInfo() {

    }

    public BeanCarInfo(int member_id, String cdk, int ccc, String cdg, String crn, String crd, String coy, String cbn, int ckm, int ctk, int cpk, String cos, String car, int cak, int cwk, String cem, String cps) {
        this.member_id = member_id;
        this.cdk = cdk;
        this.ccc = ccc;
        this.cdg = cdg;
        this.crn = crn;
        this.crd = crd;
        this.coy = coy;
        this.cbn = cbn;
        this.ckm = ckm;
        this.ctk = ctk;
        this.cpk = cpk;
        this.cos = cos;
        this.car = car;
        this.cak = cak;
        this.cwk = cwk;
        this.cem = cem;
        this.cps = cps;
    }
}
package com.infoline.doctorcha.presentation.bean;

public class BeanContent {
	public int post_id;
	public int id;            //content id
	public int cot; //content type
	public String cob; //content body
	public float sn;        //serial no

	//SparseArray로 사용하고 이거는 제거할 것
	public String realUrl;

	public BeanContent() {
		this.cob = "";
		this.realUrl = "";
		this.cot = 1;
		this.sn = 1;
	}
}

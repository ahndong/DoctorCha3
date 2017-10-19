package com.infoline.doctorcha.presentation.bean;

import java.util.List;

//1. 차후에는 d1을 단일 JsonObject로 받을 수 있도록 하자
public class BeanPostAndContents_test {
	public BeanPost d1;
	public List<BeanContent> d2;
	public List<BeanDeleted> d3;

	public BeanPostAndContents_test() {

	}

	public BeanPostAndContents_test(BeanPost d1, List<BeanContent> d2, List<BeanDeleted> d3) {
		this.d1 = d1;
		this.d2 = d2;
		this.d3 = d3;
	}
}

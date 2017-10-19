package com.infoline.doctorcha.presentation.bean;

import java.util.List;

//1. 차후에는 d1을 JsonObject로 받을 수 있도록 하자
public class BeanPostAndContents {
	public List<BeanPost> d1;
	public List<BeanContent> d2;

	public BeanPostAndContents() {

	}

	public BeanPostAndContents(List<BeanPost> d1, List<BeanContent> d2) {
		this.d1 = d1;
		this.d2 = d2;
	}
}

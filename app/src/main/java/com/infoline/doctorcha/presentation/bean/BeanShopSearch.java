package com.infoline.doctorcha.presentation.bean;

import java.io.Serializable;

public class BeanShopSearch implements Serializable {
	public int bmCategory_id;
	public String ca;
	public String cn;
	public String words; //ci, ht

	public BeanShopSearch() {
		bmCategory_id = -1;
		ca = "";
		cn = "";
		words = "";
	}
}

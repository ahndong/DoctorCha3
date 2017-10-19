package com.infoline.doctorcha.presentation.bean;

import java.io.Serializable;

public class BeanPostSearch implements Serializable {
	public int bmCategory_id;
	public int board_id;
	public int owner_id;
	public int creator_id;
	public String words; //tt, cob

	public BeanPostSearch() {
		bmCategory_id = -1;
		owner_id = -1;
		creator_id = -1;
		board_id = -1;
		words = "";
	}
}

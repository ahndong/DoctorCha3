package com.infoline.doctorcha.presentation.bean;

import java.util.List;

public class BeanBoarde {
	public int id;
	public String nm;

	public BeanBoarde() {

	}

	public BeanBoarde(int id, String nm) {
		this.id = id;
		this.nm = nm;
	}

	//TODO : ArrayList 검색루틴으로 대체할 것
	public static String getNmFromId(final List<BeanBoarde> beanBoardeList,  final int board_id) {
		String boardNm = "";

		for(BeanBoarde b : beanBoardeList) {
			if(b.id == board_id) {
				boardNm = b.nm;
			}
		}

		return boardNm;
	}
}

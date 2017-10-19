package com.infoline.doctorcha.presentation.bean;

import java.io.Serializable;

public class BeanPost implements Serializable {
	public String topic;

	public int board_id;
	public int owner_id;
	public int member_id;
	public int parent_id;
	public int id;        //post id
	public String ud;     //update date
	public String tt;     //title
	public String fcb;     //first text
	public String ffn;     //first image file name

	public int rec; //read count
	public int coc; //comment count
	public int lic; //like count
	public int fac; //favorite count

	public String cn; //소유자 블로그명
	public String nn; //작성자 닉네임
	public String witn; //작성자(writer) itn
	public String wsr; //작성자 sr

	public BeanPost() {

	}
}

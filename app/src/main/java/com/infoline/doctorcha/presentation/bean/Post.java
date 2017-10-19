package com.infoline.doctorcha.presentation.bean;

import java.io.Serializable;

public class Post implements Serializable {
	public static final long serialVersionUID = 1L;
	public int owner_id;      //owner id - post 소유자 id - shop id 또는 member id
	public String owner_tp;   //owner type - P.Portal S.Shop B.Blog
	public int member_id;     //member id - 포스트작성자
	public int posttp_id;     //posttype id - 게시판종류
	public int postct1_id;        //category1 id - post의 1차 카테고리
	public int postct2_id;        //category1 id - post의 2차 카테고리
	public String nin;        //nick name
	public int id;            //post id
	public String tit;        //title
	public String crd;        //create date
	public int rec; 		   //reading count
	public int coc; 		   //comment count
	public int suc; 		   //suggest count - 추천수(좋아요)
	public int shc; 		   //shared count  - 공유수
	public int content_id;    //첫번째 콘텐츠의 id
	public int cot;           //첫번째 콘텐츠의 타입 - 1.text 2.photo 3. movie 4.file 5.link 6.gps
	public String exn;        //콘텐트의 타입이 이미지, 동영상일 경우 그 확장자

	//Pet note 관련 필드
	public String red;        //real date : 실제 구매, 진료, 접종, 또는 행위일자
	public int shop_id;       //구매 샵을 특정하였을 경우 그 id
	public String shop_nm;       //구매샵명 : 자유기재일 경우 shop_id = 0;
	public int pr;            //관련 금액

	public Post() {
		
	}

	public Post(int owner_id, String owner_tp, int member_id, int posttp_id, int postct1_id, int postct2_id, String nin, int id, String tit, String crd, int rec, int coc, int suc, int shc, int content_id, int cot, String exn, String red, int shop_id, String shop_nm, int pr) {
		this.owner_id = owner_id;
		this.owner_tp = owner_tp;
		this.member_id = member_id;
		this.posttp_id = posttp_id;
		this.postct1_id = postct1_id;
		this.postct2_id = postct2_id;
		this.nin = nin;
		this.id = id;
		this.tit = tit;
		this.crd = crd;
		this.rec = rec;
		this.coc = coc;
		this.suc = suc;
		this.shc = shc;
		this.content_id = content_id;
		this.cot = cot;
		this.exn = exn;
		
		this.red = red;
		this.shop_id = shop_id;
		this.shop_nm = shop_nm;
		this.pr = pr;
	}
	
	public int getOwner_id() {
		return owner_id;
	}
	
	public String getOwner_tp() {
		return owner_tp;
	}
	
	public int getMember_id() {
		return member_id;
	}
	
	public int getPosttp_id() {
		return posttp_id;
	}
	
	public int getPostct1_id() {
		return postct1_id;
	}
	
	public int getPostct2_id() {
		return postct2_id;
	}
	
	public String getNin() {
		return nin;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTit() {
		return tit;
	}
	
	public String getCrd() {
		return crd;
	}
		
	public int getRec() {
		return rec;
	}
	public int getCoc() {
		return coc;
	}
	
	public int getSuc() {
		return suc;
	}
	
	public int getShc() {
		return shc;
	}
	
	public int getContent_id() {
		return content_id;
	}
	
	public int getCot() {
		return cot;
	}
	
	public String getExn() {
		return exn;
	}
	
//-------------------------------
	
	public String getRed() {
		return red;
	}
	
	public int getShop_id() {
		return shop_id;
	}
		
	public String getShop_nm() {
		return shop_nm;
	}
	
	public int getPr() {
		return pr;
	}
}

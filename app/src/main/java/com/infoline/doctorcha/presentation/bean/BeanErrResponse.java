package com.infoline.doctorcha.presentation.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class BeanErrResponse {
	public int ec;    //error code
	public String em; //error message
	public String sv; //scalar value

	public BeanErrResponse() {

	}

	public BeanErrResponse(String errResponse) {
		try {
			final JSONObject jo = new JSONObject(errResponse);
			this.ec = jo.getInt("ec");
			this.em = jo.getString("em");
			this.sv = jo.getString("sv");
		}
		catch (JSONException e) {
			//
		}
	}
}
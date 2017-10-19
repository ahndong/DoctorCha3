package com.infoline.doctorcha.presentation.bean;
import android.support.annotation.DrawableRes;
import com.infoline.doctorcha.R;

public class BeanCarOption {
	public int id;
	public boolean isChecked;

	public BeanCarOption() {

	}

	public BeanCarOption(final int id, final boolean isChecked) {
		this.id = id;
		this.isChecked = isChecked;
	}

	public static @DrawableRes int getOptionDrawableId(final int id) {
		final @DrawableRes int drawableId;

		switch (id) {
			case 1:
				drawableId = R.drawable.ic_option_leatherseat;
				break;
			case 2:
				drawableId = R.drawable.ic_option_vibrationseat;
				break;
			case 3:
				drawableId = R.drawable.ic_option_heaterseat;
				break;
			case 4:
				drawableId = R.drawable.ic_option_navi;
				break;
			case 5:
				drawableId = R.drawable.ic_option_sunroof;
				break;
			case 6:
				drawableId = R.drawable.ic_option_hipass;
				break;
			case 7:
				drawableId = R.drawable.ic_option_smartkey;
				break;
			case 8:
				drawableId = R.drawable.ic_option_cruisecontrol;
				break;
			case 9:
				drawableId = R.drawable.ic_option_airbag;
				break;
			case 10:
				drawableId = R.drawable.ic_option_backsensor;
				break;
			case 11:
				drawableId = R.drawable.ic_option_backcamera;
				break;
			case 12:
			default:
				drawableId = R.drawable.ic_option_tpms;
		}

		return drawableId;
	}
}

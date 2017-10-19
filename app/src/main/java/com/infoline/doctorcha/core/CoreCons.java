package com.infoline.doctorcha.core;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Administrator on 2016-01-10.
 *
 */
public class CoreCons {
    public static final Interpolator ACCELERATE = new AccelerateInterpolator();
    public static final Interpolator DECELERATE2 = new DecelerateInterpolator();
    //public static final Interpolator DECELERATE3 = new AccelerateDecelerateInterpolator()

    public enum InterpolatorType {
        ACCELERATE {
            public Interpolator getInterpolator() {
                return new AccelerateInterpolator();
            }
        },
        DECELERATE {
            @Override
            public Interpolator getInterpolator() {
                return new AccelerateInterpolator();
            }
        },
        ACCELERATEDECELERATE {
            @Override
            public Interpolator getInterpolator() {
                return new AccelerateDecelerateInterpolator();
            }
        };

        public abstract Interpolator getInterpolator();
    }

    public enum EnumDateFormat {
        UNIQUE_FILENAME("yyyyMMdd_HHmmss_SSS"),
        DATE_AND_TIME("yyyy-MM-dd HH:mm");

        private String text;

        EnumDateFormat(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }

    public enum EnumExtraName {
        ANIM_START_POINT,
        SHHOP_ID,
        TITLE,
        MENU4;
    }
}

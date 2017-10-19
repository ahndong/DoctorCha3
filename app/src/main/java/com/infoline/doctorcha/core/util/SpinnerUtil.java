package com.infoline.doctorcha.core.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.infoline.doctorcha.R;
import com.infoline.doctorcha.presentation.MainCons;
import com.infoline.doctorcha.presentation.bean.BeanBoarde;
import com.infoline.doctorcha.presentation.bean.BeanSpinnerPair2;

import java.util.ArrayList;
import java.util.List;

import static com.infoline.doctorcha.presentation.MainApp.beanBoardeList;

public class SpinnerUtil {
    public static final int CN_BASECODETYPE_BMCATEGORY = 1;
    public static final int CN_BASECODETYPE_BOARD = 2;

    public void attachBasicCodeAdapter(final Spinner sp, final int baseCodeType, final int defaultValue, @Nullable final String firstItemText ) {
        final List<BeanSpinnerPair2> beanSpinnerPair2 = new ArrayList<>();
        int defaultPos = -1;
        int i = -1;

        if(firstItemText != null) {
            beanSpinnerPair2.add(new BeanSpinnerPair2(-1, firstItemText));
        }

        switch (baseCodeType) {
            case CN_BASECODETYPE_BMCATEGORY:
                for(MainCons.EnumBmCategory enumBmCategory : MainCons.EnumBmCategory.values()) {
                    i++;
                    final int id = enumBmCategory.getId();

                    beanSpinnerPair2.add(new BeanSpinnerPair2(id, enumBmCategory.getNm()));

                    if(defaultValue != -1 && defaultPos == -1 && defaultValue == id) {
                        defaultPos = i;
                    }
                }

                break;
            case CN_BASECODETYPE_BOARD:
                for(BeanBoarde beanBoarde : beanBoardeList) {
                    i++;
                    final int id = beanBoarde.id;
                    beanSpinnerPair2.add(new BeanSpinnerPair2(id, beanBoarde.nm));

                    if(defaultValue != -1 && defaultPos == -1 && defaultValue == id) {
                        defaultPos = i;
                    }
                }

                break;
            case 99:
            default:

        }

        /*
        //1.  value에 class를 지정할 경우 서용 : T class를 이룔한 beanSpinnerPair는 Uinspect 참고할 것
        for(BeanBasicCode beanBasicCode : beanBasicCodeList) {
            beanSpinnerPair2.add(new BeanSpinnerPair(beanBasicCode.KNAME, beanBasicCode));
        }

        final ArrayAdapter<BeanSpinnerPair> adapter = new ArrayAdapter<>(ctx, R.layout.tmp_spinner_textview, beanSpinnerPair2);
        adapter.setDropDownViewResource(R.layout.tmp_spinner_dropdownitem);
        sp.setAdapter(adapter);
        */

        final ArrayAdapter<BeanSpinnerPair2> adapter = new ArrayAdapter<>(sp.getContext(), R.layout.tmp_spinner_textview, beanSpinnerPair2);
        adapter.setDropDownViewResource(R.layout.tmp_spinner_dropdownitem);
        sp.setAdapter(adapter);

        //굳이 setBasicCode()호출하여 중복작업 하지 않고 attach시 바로 기본값 설정한다
        sp.setSelection(defaultPos + (firstItemText == null ? 0 : 1), true);
    }

    public void setBasicCode(final Spinner sp, final int codeValue) {
        //1. foreach문이 안먹힌다.
        final ArrayAdapter<BeanSpinnerPair2> adapter = (ArrayAdapter<BeanSpinnerPair2>)sp.getAdapter();

        for(int i = 0; i < adapter.getCount(); i++) {
            final BeanSpinnerPair2 beanSpinnerPair2 = adapter.getItem(i);
            if(beanSpinnerPair2.getValue() == codeValue) {
                sp.setSelection(i, true);
                break;
            }
        }
    }

    public int getBasicCode(final Spinner sp) {
        //1. foreach문이 안먹힌다.
        final BeanSpinnerPair2 beanSpinnerPair2 = (BeanSpinnerPair2)sp.getSelectedItem();

        return beanSpinnerPair2.getValue();
    }
}

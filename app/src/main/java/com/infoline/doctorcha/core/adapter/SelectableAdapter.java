package com.infoline.doctorcha.core.adapter;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;

import com.infoline.doctorcha.core.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DELL on 2016-03-06.
 * 1. 강제사항은 아니지만 추상 class는 일반적으로 하나 이상의 추상 method를가진다
 * 2. 객체를 생설할 수 없다
 * 3. super class로 사용 가능하다
 * 4. 추상 method를 사용하기 위해서는 반드시 해당 method를 재정의해야 한다
 * 5. 상속받은 class는 모든 추상 method를 재정의하여야 하며 그렇지 않은 경우는 자신도 추상 class가 된다
 * 5. 추상 method : 내용이 없는 비어 있는 method
 * 6. @Override compiler에게 재정의된 method임을 통지하는 역할을 하며 굳이 명시적으로 선언하지 않아도 된다
 *
 * 1. interface는 추상 class의 극단적인 경우이다
 * 2. interface는 추상 method로만 구성되어 있으며method의 선언만 가능하다
 * 3. interface를 사용하기 위해서는 implements keyword를 사용해야만 한다
 * 4  @Override compiler에게 재정의된 method임을 통지하는 역할을 하며 굳이 명시적으로 선언하지 않아도 된다
 * 5. 멤버변수와 메소드로 구성되는데 멤버변수는 모두 상수형으로 선언되고 메소드는 모두 추상 메소드로 선언된다.
 * 6. 다중 상속이 가능하다
 *
 *
 * 이해불가
 * 클래스는 인터페이스 유형으로 변환될 수 있는데, 클래스가 구현하고 있는 인터페이스 유형으로만 변환할 수 있다.

 */
//public abstract class SelectableAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
public abstract class SelectableAdapter extends RecyclerView.Adapter {
    public static final int MODE_IDLE = 0;
    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTI = 2;

    public ArrayList<Integer> selectedPositions;
    private int mMode;
    private String menuName = "";
    //protected RecyclerView rv;

    public boolean isSelectMode = false;
    protected boolean isMoved = false;
    protected boolean isSelectAll = false;
    protected boolean mLastItemInActionMode = false;

    public SelectableAdapter(String menuName) {
        //CommonUtil.writeLog(menuName, null);
        this.menuName = menuName;

        selectedPositions = new ArrayList<>();
        mMode = MODE_IDLE;
    }

    /*
	@Override
    public void onAttachedToRecyclerView(RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        this.rv = rv;
    }
    */

    public void setMode(int mode) {
        this.mMode = mode;
        mLastItemInActionMode = (mode == MODE_IDLE);
    }

    public int getMode() {
        return mMode;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public boolean isLastItemInActionMode() {
        return mLastItemInActionMode;
    }

    public void resetActionModeFlags() {
        this.isSelectAll = false;
        this.mLastItemInActionMode = false;
    }

    public boolean isSelected(int pos) {
        return selectedPositions.contains(Integer.valueOf(pos));
    }

    public abstract  boolean isSelectable(int pos);

    public void toggleSelection(int pos) {
        if (pos < 0) return;
        
        if (mMode == MODE_SINGLE)
            clearSelection();

        int index = selectedPositions.indexOf(pos);

        if (index == -1) {
            selectedPositions.add(pos);
        } else {
            selectedPositions.remove(index);
        }

        notifyItemChanged(pos);
        isSelectMode = getSelectedItemCount() != 0;
    }

   public void selectAll(Integer... viewTypes) {
        isSelectAll = true;
        List<Integer> viewTypesToSelect = Arrays.asList(viewTypes);

        selectedPositions = new ArrayList<Integer>(getItemCount());
        int positionStart = 0, itemCount = 0;

        for (int i = 0; i < getItemCount(); i++) {
            if (isSelectable(i) && (viewTypesToSelect.size() == 0 || viewTypesToSelect.contains(getItemViewType(i)))) {
                selectedPositions.add(i);
                itemCount++;
            } else {
                //Optimization for ItemRangeChanged
                if (positionStart + itemCount == i) {
                    handleSelection(positionStart, itemCount);
                    itemCount = 0;
                    positionStart = i;
                }
            }
        }

        handleSelection(positionStart, getItemCount());
    }

    public void clearSelection() {
        //Reverse-sort the list, start from last position for efficiency
        Collections.sort(selectedPositions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });

        Iterator<Integer> iterator = selectedPositions.iterator();
        int positionStart = 0, itemCount = 0;

        //The notification is done only on items that are currently selected.
        while (iterator.hasNext()) {
            int pos = iterator.next();
            iterator.remove();
            //Optimization for ItemRangeChanged
            if (positionStart + itemCount == pos) {
                itemCount++;
            } else {
                //Notify previous items in range
                handleSelection(positionStart, itemCount);
                positionStart = pos;
                itemCount = 1;
            }
        }
        //Notify remaining items in range
        handleSelection(positionStart, itemCount);
    }

    public void removeItems(List<? extends Object> list) {
        if (selectedPositions == null || selectedPositions.isEmpty()) return;

        //Reverse-sort the list, start from last position for efficiency
        Collections.sort(selectedPositions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        int positionStart = 0, itemCount = 0;
        int lastPosition = selectedPositions.get(0);

        for (Integer position : selectedPositions) {
            if (lastPosition - itemCount == position) {
                itemCount++;
                positionStart = position;
            } else {
                if (itemCount > 0) {
                    removeRange(list, positionStart, itemCount);
                }

                positionStart = lastPosition = position;
                itemCount = 1;
            }
        }

        //Clear also the selection
        clearSelection();

        //Remove last range
        if (itemCount > 0) {
            removeRange(list, positionStart, itemCount);
        }
    }

    public void removeRange(List<? extends Object> list, @IntRange(from = 0) int positionStart, @IntRange(from = 0) int itemCount) {
        int initialCount = getItemCount();

        if (positionStart < 0 || (positionStart + itemCount) > initialCount) {
            return;
        }

        for (int position = positionStart; position < positionStart + itemCount; position++) {
            //T.remove(positionStart);
            list.remove(positionStart);
        }

        notifyItemRangeRemoved(positionStart, itemCount);
    }

    private void handleSelection(int positionStart, int itemCount) {
        if (itemCount > 0) notifyItemRangeChanged(positionStart, itemCount);
    }

    public int getSelectedItemCount() {
        return selectedPositions.size();
    }

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public void onSaveInstanceState(Bundle outState) {
        CommonUtil.writeLog(menuName, "selectedPositions.size() == " + selectedPositions.size());
        if(outState != null && selectedPositions.size() != 0) {
            outState.putIntegerArrayList(menuName + "_selectedPositions", selectedPositions);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        /*
        if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setSelection(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        */

        if (savedInstanceState != null) {
            ArrayList<Integer> saved = savedInstanceState.getIntegerArrayList(menuName + "_" + "selectedPositions");

            if(saved != null) {
                selectedPositions = saved;
            }

            isSelectMode = selectedPositions.size() != 0;
            CommonUtil.writeLog(menuName, "selectedPositions.size() == " + selectedPositions.size());
        }
    }
}

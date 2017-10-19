package com.infoline.doctorcha.core.itemtouchhelper;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.infoline.doctorcha.core.util.CommonUtil;

import java.util.Arrays;

/**
 * Created by Administrator on 2015-07-05.
 */
public class ILItemTouchHelperCallback extends ItemTouchHelper.Callback {
    public static final float ALPHA_FULL = 1.0f;

    private final ILItemTouchHelperAdapter adapter;

    private Integer[] draggableViewTypes = null;
    private Integer[] swipableViewTypes = null;

    public ILItemTouchHelperCallback(ILItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    //1. SelectEnabled와 DragEnabled은 상호 독립적이다.
    //2. 즉, select를 못한다고 drag도 못하는 것은 아니다라는 말
    //3. 다소 번거롭지만 두개는 통합될 수 없다. 고민하지 마라

    private boolean isSelectable = false;
    private boolean isDraggable = false;
    private boolean isSwipable = false;

    public void setDraggableViewTypes(@Nullable Integer[] draggableViewTypes) {
        if(!(draggableViewTypes == null || draggableViewTypes.length == 0)) {
            this.isDraggable = true;
            this.draggableViewTypes = draggableViewTypes;
        }
    }

    public void setSwipableViewTypes(@Nullable Integer[] swipableViewTypes) {
        if(!(swipableViewTypes == null || swipableViewTypes.length == 0)) {
            this.isSwipable = true;
            this.swipableViewTypes = swipableViewTypes;
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isDraggable;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isSwipable;
    }

    /*
    용도 확인할 것
    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.8f;
    }
    */

    @Override
    public int getMovementFlags(RecyclerView rv, RecyclerView.ViewHolder vh) {
        //ViewPostImeInputStage ACTION_DOWN
        //TouchHelper: getMovementFlags
        //TouchHelper: getMovementFlags
        //TouchHelper: onSelectedChanged
        //GestureDetector: onLongPress
        //TouchHelper:  DrawOver가 시작된다
        //Recycler: Longclick
        //TouchHelper: onSelectedChanged
        //TouchHelper: clearView


        Log.d("===>TouchHelper", "getMovementFlags-"+vh.getItemViewType());

        int dragFlags = 0;
        int swipeFlags = 0;
        int viewType = vh.getItemViewType();

        if(isDraggable) {
            if(Arrays.asList(draggableViewTypes).contains(viewType)) {
                if (rv.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                }
            }
        }

        if(isSwipable) {
            if(Arrays.asList(swipableViewTypes).contains(viewType)) {
                //GridLayoutManager는 사용자 설정에 관계없이 swip를 불허한다
                if (!(rv.getLayoutManager() instanceof GridLayoutManager)) {
                    swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                }
            }
        }

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        //1. 이곳에서 해당 타겟의 Collection.swipe를 거부할 경우 스크롤시 이상한 현상이 발생한다.
        //2. onMoved로 옮기고 나니 스크롤시 이상한 현상이 없어졌으나 4개정도의 ViewHolder를 통과하면 Drag가 자동으로 스톱되어 버린다.
        return adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onMoved(RecyclerView rv, RecyclerView.ViewHolder source, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        adapter.onItemMoved(fromPos, toPos);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView rv, RecyclerView.ViewHolder vh, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive);

        // Fade out the view as it is swiped out of the parent's bounds
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = vh.itemView;
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) itemView.getWidth();
            itemView.setAlpha(alpha);
            vh.itemView.setTranslationX(dX);
        }
        else {


        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView rv, RecyclerView.ViewHolder vh, float dX, float dY, int actionState, boolean isCurrentlyActive)
    {
        super.onChildDrawOver(c, rv, vh, dX, dY, actionState, isCurrentlyActive);
    }

    private int mCachedMaxScrollSpeed = -1; // default
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000; // default

    @Override
    public int interpolateOutOfBoundsScroll(RecyclerView rv, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
        final int maxScroll = getMaxDragScroll(rv);
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll * sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;

        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f; //3f
        }
        else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }

        final int value = (int) (cappedScroll * sDragScrollInterpolator.getInterpolation(timeRatio));

        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }

    private int getMaxDragScroll(RecyclerView rv) {
        if (mCachedMaxScrollSpeed == -1) {
            mCachedMaxScrollSpeed = rv.getResources().getDimensionPixelSize(android.support.v7.recyclerview.R.dimen.item_touch_helper_max_drag_scroll_per_frame);
        }
        return mCachedMaxScrollSpeed;
    }

    private final Interpolator sDragScrollInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            //return t * t * t * t * t; // default return value, but it's too late for me
            return (int)Math.pow(2, (double) t); // optional whatever you like
        }
    };

    private final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder vh, int actionState) {
        Log.d("===>TouchHelper", "onSelectedChanged");
        //RecyclerView.OnItemTouchListener가 별도로 걸려 있을 경우
        //1. onSelectedChanged --> onLongPress --> DrawOver .........순으로 trigger


        //ACTION_STATE_IDLE일 경우 vh == null
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            adapter.onSelectedChanged(vh, actionState);

            CommonUtil.playVibrate(vh.itemView.getContext(), 30);

            vh.itemView.setAlpha(.8f); //--> foreground와의 중첩여부 주의할 것
            vh.itemView.setScaleX(.95f);
            vh.itemView.setScaleY(.95f);

            // ViewHolder가 ILItemTouchHelperViewHolder를 구현했을 경우
            if (vh instanceof ILItemTouchHelperViewHolder) {
                ILItemTouchHelperViewHolder itemViewHolder = (ILItemTouchHelperViewHolder) vh;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(vh, actionState);
    }

    @Override
    public void clearView(RecyclerView rv, RecyclerView.ViewHolder vh) {
        Log.d("===>TouchHelper", "clearView");
        //clearView 이후에도 onChildDraw, onChildDrawOver에 각 한 번씩 trigger된다

        super.clearView(rv, vh);

        vh.itemView.setAlpha(1f);
        vh.itemView.setScaleX(1f);
        vh.itemView.setScaleY(1f);

        if (vh instanceof ILItemTouchHelperViewHolder) {
            // ViewHolder가 ILItemTouchHelperViewHolder를 구현했을 경우
            ILItemTouchHelperViewHolder itemViewHolder = (ILItemTouchHelperViewHolder) vh;
            itemViewHolder.onItemClear();
        }

        adapter.clearView(vh);
    }

    public interface ILItemTouchHelperAdapter {
        boolean onItemMove(int fromPos, int toPos);
        void onItemMoved(int fromPos, int toPos);
        void onSelectedChanged(RecyclerView.ViewHolder vh, int actionState);
        void clearView(RecyclerView.ViewHolder vh);

        void onItemDismiss(int pos);
        //void onItemClick(View view , int pos);
    }

    public interface ILItemTouchHelperViewHolder {
        void onItemSelected();
        void onItemClear();
    }
}

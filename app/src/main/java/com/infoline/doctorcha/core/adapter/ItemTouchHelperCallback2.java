package com.infoline.doctorcha.core.adapter;

import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by DELL on 2016-03-07.
 */
public class ItemTouchHelperCallback2 extends ItemTouchHelper.Callback {
    private static final float ALPHA_FULL = 1.0f;

    private AdapterCallback mItemTouchCallback;
    private boolean mIsLongPressDragEnabled = false;
    private boolean mIsSwipeEnabled = false;
    private float mSwipeThreshold = 1.0f;
    private int mSwipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

    public ItemTouchHelperCallback2(AdapterCallback itemTouchCallback) {
        this.mItemTouchCallback = itemTouchCallback;
    }

    public void setLongPressDragEnabled(boolean isLongPressDragEnabled) {
        this.mIsLongPressDragEnabled = isLongPressDragEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mIsLongPressDragEnabled;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.mIsSwipeEnabled = isSwipeEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return mIsSwipeEnabled;
    }

    public void setSwipeThreshold(@FloatRange(from = 0.0, to = 1.0) float threshold) {
        this.mSwipeThreshold = threshold;
    }

    public void setSwipeFlags(int swipeFlags) {
        this.mSwipeFlags = swipeFlags;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return mSwipeThreshold;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (!mItemTouchCallback.shouldMove(viewHolder.getAdapterPosition(), target.getAdapterPosition())) {
            return false;
        }
        //Notify the adapter of the move
        mItemTouchCallback.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Notify the adapter of the swipe dismissal
        mItemTouchCallback.onItemSwiped(viewHolder.getAdapterPosition(), direction);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = mSwipeFlags;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ViewHolderCallback) {
                //Let the ViewHolder to know that this item is swiping or dragging
                ViewHolderCallback viewHolderCallback = (ViewHolderCallback) viewHolder;
                viewHolderCallback.onActionStateChanged(viewHolder.getAdapterPosition(), actionState);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //Force full Alpha
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        if (viewHolder instanceof ViewHolderCallback) {
            //Tell the view holder it's time to restore the idle state
            ViewHolderCallback viewHolderCallback = (ViewHolderCallback) viewHolder;
            viewHolderCallback.onItemReleased(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public interface AdapterCallback {
        boolean shouldMove(int fromPosition, int toPosition);
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemSwiped(int position, int direction);
    }

    public interface ViewHolderCallback {
        void onActionStateChanged(int position, int actionState);
        void onItemReleased(int position);
    }

}

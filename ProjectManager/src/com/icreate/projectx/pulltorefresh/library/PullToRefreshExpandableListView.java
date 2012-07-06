package com.icreate.projectx.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ExpandableListView;

import com.icreate.projectx.pulltorefresh.library.internal.EmptyViewMethodAccessor;

public class PullToRefreshExpandableListView extends PullToRefreshAdapterViewBase<ExpandableListView> {

	public PullToRefreshExpandableListView(Context context) {
		super(context);
	}

	public PullToRefreshExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshExpandableListView(Context context, Mode mode) {
		super(context, mode);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalExpandableListView) getRefreshableView()).getContextMenuInfo();
	}

	@Override
	protected final ExpandableListView createRefreshableView(Context context, AttributeSet attrs) {
		ExpandableListView lv = new InternalExpandableListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	class InternalExpandableListView extends ExpandableListView implements EmptyViewMethodAccessor {

		public InternalExpandableListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshExpandableListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}
}

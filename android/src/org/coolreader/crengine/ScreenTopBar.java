/*
 * CoolReader for Android
 * Copyright (C) 2026 Cool Reader Lite contributors
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, Inc., either version 2
 * of the License, or (at your option) any later version.
 */

package org.coolreader.crengine;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.coolreader.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScreenTopBar extends LinearLayout {
	public static class MenuItem {
		final CharSequence title;
		final int iconResId;
		final Runnable action;

		public MenuItem(CharSequence title, int iconResId, Runnable action) {
			this.title = title;
			this.iconResId = iconResId;
			this.action = action;
		}
	}

	private final BaseActivity activity;
	private final ImageButton backButton;
	private final TextView titleView;
	private final FrameLayout endSlot;
	private final ImageButton moreButton;
	private final ProgressBar progressBar;
	private List<MenuItem> menuItems = Collections.emptyList();
	private CharSequence fullTitle = "";
	private PopupWindow popup;

	public ScreenTopBar(android.content.Context context) {
		this(context, null);
	}

	public ScreenTopBar(android.content.Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScreenTopBar(android.content.Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		activity = findActivity(context);
		if (activity == null)
			throw new IllegalArgumentException("ScreenTopBar requires BaseActivity context");
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		int slotSize = activity.getPreferredItemHeight();
		backButton = createButton(R.string.dlg_button_back);
		addView(backButton, new LayoutParams(slotSize, slotSize));

		titleView = new TextView(context);
		titleView.setSingleLine(true);
		titleView.setGravity(Gravity.CENTER_VERTICAL);
		titleView.setTextAppearance(context, R.style.TextAppearance_Medium);
		titleView.setTextColor(resolveColor(R.attr.textColorToolBarLabel));
		int titlePadding = dp(8);
		titleView.setPadding(titlePadding, 0, titlePadding, 0);
		addView(titleView, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));

		endSlot = new FrameLayout(context);
		moreButton = createButton(R.string.btn_toolbar_more);
		int moreIconResId = Utils.resolveResourceIdByAttr(activity,
				R.attr.cr3_button_more_drawable, R.drawable.cr3_button_more);
		Utils.setTintedIcon(moreButton, moreIconResId, R.attr.textColorToolBarLabel);
		moreButton.setOnClickListener(v -> showMenu());
		endSlot.addView(moreButton, new FrameLayout.LayoutParams(slotSize, slotSize, Gravity.CENTER));

		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(GONE);
		int progressSize = dp(24);
		endSlot.addView(progressBar, new FrameLayout.LayoutParams(progressSize, progressSize, Gravity.CENTER));
		addView(endSlot, new LayoutParams(slotSize, slotSize));

		setBackAction(null);
		setMenuItems(Collections.emptyList());
		refreshStyle();
	}

	private ImageButton createButton(int contentDescriptionId) {
		ImageButton button = new ImageButton(getContext());
		button.setBackgroundResource(Utils.resolveResourceIdByAttr(activity,
				R.attr.cr3_toolbar_button_background_drawable, R.drawable.cr3_toolbar_button_background));
		button.setContentDescription(getResources().getString(contentDescriptionId));
		button.setFocusable(false);
		button.setFocusableInTouchMode(false);
		button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		button.setPadding(dp(6), dp(6), dp(6), dp(6));
		return button;
	}

	public void refreshStyle() {
		activity.applyTopBarStyle(this);
		int backIconResId = Utils.resolveResourceIdByAttr(activity,
				R.attr.cr3_button_prev_drawable, R.drawable.cr3_button_prev);
		Utils.setTintedIcon(backButton, backIconResId, R.attr.textColorToolBarLabel);
		int moreIconResId = Utils.resolveResourceIdByAttr(activity,
				R.attr.cr3_button_more_drawable, R.drawable.cr3_button_more);
		Utils.setTintedIcon(moreButton, moreIconResId, R.attr.textColorToolBarLabel);
		titleView.setTextColor(resolveColor(R.attr.textColorToolBarLabel));
	}

	public void setTitle(int titleResId) {
		setTitle(getResources().getText(titleResId));
	}

	public void setTitle(CharSequence title) {
		fullTitle = title != null ? title : "";
		updateDisplayedTitle();
	}

	public void setBackAction(Runnable action) {
		backButton.setVisibility(action != null ? VISIBLE : GONE);
		backButton.setOnClickListener(action != null ? v -> action.run() : null);
	}

	public void setMenuItems(MenuItem... items) {
		setMenuItems(items != null ? Arrays.asList(items) : Collections.emptyList());
	}

	public void setMenuItems(List<MenuItem> items) {
		menuItems = items != null ? new ArrayList<>(items) : Collections.emptyList();
		updateEndSlot();
	}

	public void setReaderActions(ReaderAction[] actions, CRToolBar.OnActionHandler handler) {
		ArrayList<MenuItem> items = new ArrayList<>();
		if (actions != null) {
			for (ReaderAction action : actions) {
				int iconResId = action.iconId != 0 ? action.iconId : R.drawable.cr3_browser_book;
				items.add(new MenuItem(getResources().getText(action.nameId), iconResId,
						() -> handler.onActionSelected(action)));
			}
		}
		setMenuItems(items);
	}

	public void setLoading(boolean loading) {
		progressBar.setVisibility(loading ? VISIBLE : GONE);
		moreButton.setVisibility(!loading && !menuItems.isEmpty() ? VISIBLE : GONE);
		endSlot.setVisibility(loading || !menuItems.isEmpty() ? VISIBLE : GONE);
		if (loading && popup != null)
			popup.dismiss();
	}

	public boolean showMenu() {
		if (progressBar.getVisibility() == VISIBLE || menuItems.isEmpty())
			return false;
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
			return true;
		}

		int width = Math.min(getResources().getDisplayMetrics().widthPixels - dp(32), dp(320));
		LinearLayout rows = new LinearLayout(getContext());
		rows.setOrientation(VERTICAL);
		rows.setPadding(0, dp(8), 0, dp(8));
		for (MenuItem item : menuItems)
			rows.addView(createMenuRow(item), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		ScrollView scroll = new ScrollView(getContext());
		scroll.setFillViewport(false);
		scroll.addView(rows, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		GradientDrawable background = new GradientDrawable();
		background.setColor(activity.getSystemBarBackgroundColor());
		background.setCornerRadius(dp(16));
		scroll.setBackground(background);

		rows.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		Rect visibleFrame = new Rect();
		getWindowVisibleDisplayFrame(visibleFrame);
		int[] location = new int[2];
		moreButton.getLocationOnScreen(location);
		int maxHeight = Math.max(dp(48), visibleFrame.bottom - location[1] - moreButton.getHeight() - dp(16));
		int height = Math.min(rows.getMeasuredHeight(), maxHeight);

		popup = new PopupWindow(scroll, width, height, true);
		GradientDrawable popupBackground = new GradientDrawable();
		popupBackground.setColor(activity.getSystemBarBackgroundColor());
		popupBackground.setCornerRadius(dp(16));
		popup.setBackgroundDrawable(popupBackground);
		popup.setOutsideTouchable(true);
		popup.setClippingEnabled(true);
		popup.setElevation(dp(8));
		popup.setOnDismissListener(() -> popup = null);
		popup.showAsDropDown(moreButton, moreButton.getWidth() - width, 0);
		return true;
	}

	private View createMenuRow(MenuItem item) {
		LinearLayout row = new LinearLayout(getContext());
		row.setOrientation(HORIZONTAL);
		row.setGravity(Gravity.CENTER_VERTICAL);
		row.setMinimumHeight(dp(48));
		row.setPadding(dp(16), dp(8), dp(16), dp(8));
		TypedValue selectableBackground = new TypedValue();
		if (getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
				selectableBackground, true))
			row.setBackgroundResource(selectableBackground.resourceId);

		ImageView icon = new ImageView(getContext());
		icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		int iconResId = item.iconResId != 0 ? item.iconResId : R.drawable.cr3_browser_book;
		Utils.setTintedIcon(icon, iconResId, R.attr.textColorToolBarLabel);
		row.addView(icon, new LayoutParams(dp(24), dp(24)));

		TextView label = new TextView(getContext());
		label.setText(item.title);
		label.setTextAppearance(getContext(), R.style.TextAppearance_Medium);
		label.setTextColor(resolveColor(R.attr.textColorToolBarLabel));
		label.setMaxLines(2);
		label.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams labelParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
		labelParams.leftMargin = dp(16);
		row.addView(label, labelParams);

		row.setOnClickListener(v -> {
			PopupWindow currentPopup = popup;
			if (currentPopup != null)
				currentPopup.dismiss();
			if (item.action != null)
				item.action.run();
		});
		return row;
	}

	private void updateEndSlot() {
		boolean hasMenu = !menuItems.isEmpty();
		moreButton.setVisibility(hasMenu ? VISIBLE : GONE);
		progressBar.setVisibility(GONE);
		endSlot.setVisibility(hasMenu ? VISIBLE : GONE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		updateDisplayedTitle();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		updateDisplayedTitle();
	}

	private void updateDisplayedTitle() {
		int availableWidth = titleView.getWidth() - titleView.getPaddingLeft() - titleView.getPaddingRight();
		String text = fullTitle.toString();
		if (availableWidth <= 0 || titleView.getPaint().measureText(text) <= availableWidth) {
			setDisplayedTitle(text);
			return;
		}
		String suffix = "...";
		int low = 0;
		int high = text.length();
		while (low < high) {
			int mid = (low + high + 1) / 2;
			if (titleView.getPaint().measureText(text.substring(0, mid) + suffix) <= availableWidth)
				low = mid;
			else
				high = mid - 1;
		}
		setDisplayedTitle(text.substring(0, low) + suffix);
	}

	private void setDisplayedTitle(CharSequence title) {
		if (!TextUtils.equals(titleView.getText(), title))
			titleView.setText(title);
	}

	private int resolveColor(int attrId) {
		TypedArray values = getContext().getTheme().obtainStyledAttributes(new int[] {attrId});
		int color = values.getColor(0, 0xFF303030);
		values.recycle();
		return color;
	}

	private int dp(int value) {
		return Math.round(value * getResources().getDisplayMetrics().density);
	}

	private static BaseActivity findActivity(Context context) {
		Context current = context;
		while (current instanceof ContextWrapper) {
			if (current instanceof BaseActivity)
				return (BaseActivity) current;
			Context base = ((ContextWrapper) current).getBaseContext();
			if (base == current)
				break;
			current = base;
		}
		return current instanceof BaseActivity ? (BaseActivity) current : null;
	}
}

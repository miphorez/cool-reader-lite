/*
 * CoolReader for Android
 * Copyright (C) 2011-2014 Vadim Lopatin <coolreader.org@gmail.com>
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.coolreader.crengine;

import org.coolreader.CoolReader;
import org.coolreader.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class OPDSCatalogEditDialog extends BaseDialog {

	private final CoolReader mActivity;
	private final LayoutInflater mInflater;
	private final FileInfo mItem;
	private final EditText nameEdit;
	private final EditText urlEdit;
	private final EditText usernameEdit;
	private final EditText passwordEdit;
	private final Runnable mOnUpdate;

	public OPDSCatalogEditDialog(CoolReader activity, FileInfo item, Runnable onUpdate) {
		super(activity, activity.getString((item.id == null) ? R.string.dlg_catalog_add_title
				: R.string.dlg_catalog_edit_title), true,
				false);
		mActivity = activity;
		mItem = item;
		mOnUpdate = onUpdate;
		mInflater = LayoutInflater.from(getContext());
		View view = mInflater.inflate(R.layout.catalog_edit_dialog, null);
		nameEdit = (EditText) view.findViewById(R.id.catalog_name);
		urlEdit = (EditText) view.findViewById(R.id.catalog_url);
		usernameEdit = (EditText) view.findViewById(R.id.catalog_username);
		passwordEdit = (EditText) view.findViewById(R.id.catalog_password);
		nameEdit.setText(mItem.filename);
		urlEdit.setText(mItem.getOPDSUrl());
		usernameEdit.setText(mItem.username);
		passwordEdit.setText(mItem.password);
		setThirdButtonImage(Utils.resolveResourceIdByAttr(activity, R.attr.cr3_button_remove_drawable, R.drawable.cr3_button_remove),
				R.string.mi_catalog_delete);
		setView(view);
	}

	@Override
	protected void onPositiveButtonClick() {
		save();
	}
	
	private void save() {
		activity.getDB().saveOPDSCatalog(mItem.id,
				urlEdit.getText().toString(), nameEdit.getText().toString(), 
				usernameEdit.getText().toString(), passwordEdit.getText().toString());
		mOnUpdate.run();
		super.onPositiveButtonClick();
	}

	@Override
	protected void onNegativeButtonClick() {
		super.onNegativeButtonClick();
	}

	@Override
	protected void onThirdButtonClick() {
		mActivity.askDeleteCatalog(mItem);
		super.onThirdButtonClick();
	}

	
}

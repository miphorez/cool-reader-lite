/*
 * CoolReader for Android
 * Copyright (C) 2011,2012 Vadim Lopatin <coolreader.org@gmail.com>
 * Copyright (C) 2018 Yuri Plotnikov <plotnikovya@gmail.com>
 * Copyright (C) 2018,2021 Aleksey Chernov <valexlin@gmail.com>
 * Copyright (C) 2026 Dmitry <13149058+miphorez@users.noreply.github.com>
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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import org.coolreader.BuildConfig;
import org.coolreader.CoolReader;
import org.coolreader.R;

import java.util.ArrayList;
import java.util.Iterator;

public class AboutDialog extends BaseDialog implements TabContentFactory {
	final CoolReader mCoolReader;
	
	private View mAppTab;
	private View mDirsTab;
	private View mLicenseTab;
	private View mInfoTab;

	private void setupLink(TextView textView, int labelResId, String url) {
		SpannableString link = new SpannableString(mCoolReader.getString(labelResId));
		link.setSpan(new URLSpan(url), 0, link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(link);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private View createTabIndicator(LayoutInflater inflater, int imageDrawable) {
		View tabIndicator = inflater.inflate(R.layout.tab_indicator, null);
		ImageView imageView = tabIndicator.findViewById(R.id.tab_icon);
		imageView.setImageResource(imageDrawable);
		return tabIndicator;
	}

	private int getInstalledVersionCode() {
		try {
			PackageInfo packageInfo = mCoolReader.getPackageManager().getPackageInfo(mCoolReader.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			return BuildConfig.VERSION_CODE;
		}
	}
	
	public AboutDialog( CoolReader activity)
	{
		super(activity);
		mCoolReader = activity;

		setTitle(R.string.dlg_about);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		TabHost tabs = (TabHost)inflater.inflate(R.layout.about_dialog, null);
		mAppTab = inflater.inflate(R.layout.about_dialog_app, null);
		((TextView)mAppTab.findViewById(R.id.version)).setText(mCoolReader.getString(R.string.app_name) + " " + mCoolReader.getVersion());
		String versionDetails = mCoolReader.getString(R.string.dlg_about_based_on_version, BuildConfig.UPSTREAM_VERSION_NAME)
				+ "\n"
				+ mCoolReader.getString(R.string.dlg_about_build_details, getInstalledVersionCode(), BuildConfig.GIT_COMMIT, BuildConfig.BUILD_TYPE);
		((TextView)mAppTab.findViewById(R.id.version_details)).setText(versionDetails);
		setupLink(mAppTab.findViewById(R.id.maintainer), R.string.dlg_about_link_github_maintainer, "https://github.com/miphorez");
		setupLink(mAppTab.findViewById(R.id.www), R.string.dlg_about_link_github_lite, "https://github.com/miphorez/cool-reader-lite");
		setupLink(mAppTab.findViewById(R.id.sourceforge), R.string.dlg_about_link_sourceforge, "https://sourceforge.net/projects/crengine");
		setupLink(mAppTab.findViewById(R.id.www1), R.string.dlg_about_link_github_original, "https://github.com/buggins/coolreader/");

		mDirsTab = inflater.inflate(R.layout.about_dialog_dirs, null);
		TextView fonts_dir = mDirsTab.findViewById(R.id.fonts_dirs);

		ArrayList<String> fontsDirs = Engine.getFontsDirs();
		StringBuilder sbuf = new StringBuilder();
		Iterator<String> it = fontsDirs.iterator();
		while (it.hasNext()) {
			String s = it.next();
			sbuf.append(s);
			if (it.hasNext()) {
				sbuf.append("\n");
			}
		}
		fonts_dir.setText(sbuf.toString());

		ArrayList<String> testuresDirs = Engine.getDataDirs(Engine.DataDirType.TexturesDirs);
		sbuf = new StringBuilder();
		it = testuresDirs.iterator();
		while (it.hasNext()) {
			String s = it.next();
			sbuf.append(s);
			if (it.hasNext()) {
				sbuf.append("\n");
			}
		}
		TextView textures_dir = mDirsTab.findViewById(R.id.textures_dirs);
		textures_dir.setText(sbuf.toString());

		ArrayList<String> backgroundsDirs = Engine.getDataDirs(Engine.DataDirType.BackgroundsDirs);
		sbuf = new StringBuilder();
		it = backgroundsDirs.iterator();
		while (it.hasNext()) {
			String s = it.next();
			sbuf.append(s);
			if (it.hasNext()) {
				sbuf.append("\n");
			}
		}
		TextView backgrounds_dir = mDirsTab.findViewById(R.id.backgrounds_dirs);
		backgrounds_dir.setText(sbuf.toString());

		ArrayList<String> hyphDirs = Engine.getDataDirs(Engine.DataDirType.HyphsDirs);
		sbuf = new StringBuilder();
		it = hyphDirs.iterator();
		while (it.hasNext()) {
			String s = it.next();
			sbuf.append(s);
			if (it.hasNext()) {
				sbuf.append("\n");
			}
		}
		TextView hyph_dir = mDirsTab.findViewById(R.id.hyph_dirs);
		hyph_dir.setText(sbuf.toString());

		mLicenseTab = inflater.inflate(R.layout.about_dialog_license, null);
		String license = Engine.getInstance(mCoolReader).loadResourceUtf8(R.raw.license);
		((TextView)mLicenseTab.findViewById(R.id.license)).setText(license);
		mInfoTab = inflater.inflate(R.layout.about_dialog_info, null);
		((TextView)mInfoTab.findViewById(R.id.about_info_text)).setText(
				mCoolReader.getString(R.string.dlg_about_free_software, mCoolReader.getString(R.string.app_name)));
		
		tabs.setup();
		TabHost.TabSpec tsApp = tabs.newTabSpec("App");
		tsApp.setIndicator(createTabIndicator(inflater, R.drawable.cr3_menu_link));
		tsApp.setContent(this);
		tabs.addTab(tsApp);

		TabHost.TabSpec tsDirectories = tabs.newTabSpec("Directories");
		tsDirectories.setIndicator(createTabIndicator(inflater, R.drawable.ic_menu_archive));
		tsDirectories.setContent(this);
		tabs.addTab(tsDirectories);

		TabHost.TabSpec tsLicense = tabs.newTabSpec("License");
		tsLicense.setIndicator(createTabIndicator(inflater, R.drawable.ic_menu_star));
		tsLicense.setContent(this);
		tabs.addTab(tsLicense);
		
		TabHost.TabSpec tsInfo = tabs.newTabSpec("Info");
		tsInfo.setIndicator(createTabIndicator(inflater, R.drawable.ic_menu_emoticons));
		tsInfo.setContent(this);
		tabs.addTab(tsInfo);
		
		setView( tabs );
		
	}

	
	@Override
	public View createTabContent(String tag) {

		if ( "App".equals(tag) )
			return mAppTab;
		else if ( "Directories".equals(tag) )
			return mDirsTab;
		else if ( "License".equals(tag) )
			return mLicenseTab;
		else if ( "Info".equals(tag) )
			return mInfoTab;
		return null;
	}
	
}

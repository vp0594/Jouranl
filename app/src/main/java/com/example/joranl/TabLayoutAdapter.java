package com.example.joranl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabLayoutAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    Context context;

    int[] imgList = {android.R.drawable.ic_menu_my_calendar, android.R.drawable.ic_menu_edit};

    public TabLayoutAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public void addFragment(Fragment fragment, String s) {
        fragmentArrayList.add(fragment);
        stringArrayList.add(s);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public CharSequence getPageTitle(int position) {
        Drawable drawable = ContextCompat.getDrawable(context, imgList[position]);

        drawable.setBounds(0,0 ,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());

        SpannableString spannableString = new SpannableString(""+stringArrayList.get(position));

        ImageSpan imageSpan= new ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM);

        spannableString.setSpan(imageSpan,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;

    }
}

package com.fota.android.widget.sortrecyclerview;

import com.fota.android.moudles.mine.login.bean.CounrtyAreasBean;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CounrtyAreasBean.Area> {

	public int compare(CounrtyAreasBean.Area o1, CounrtyAreasBean.Area o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return -1;
		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return 1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}

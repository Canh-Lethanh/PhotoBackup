package com.clt.photosbackup;

public class TestUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String chkStr = CheckFileUtil.getNewChkFilename("abcd.jpg", 12563325);
		System.out.println(chkStr);
	}

}

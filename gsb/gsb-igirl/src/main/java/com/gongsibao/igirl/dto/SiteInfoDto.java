package com.gongsibao.igirl.dto;

import java.util.ArrayList;
import java.util.List;

public class SiteInfoDto {
    //公司名称
    private String logoUrl;

    //统一信用代码
    private List<String> loopImgs=new ArrayList<String>();

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public List<String> getLoopImgs() {
		return loopImgs;
	}

	public void setLoopImgs(List<String> loopImgs) {
		this.loopImgs = loopImgs;
	}

   

}
package com.yunchuan.tingyanwu.ad.vo;

/**
 * Created by tingyanwu on 2017/10/29.
 */

public class LoginResult {
    /**
     * result : success
     * token : edf682ac6fc075fba297fe4455de729e
     * flag : member
     * pay : 1
     * memberId : 4072
     * memberName : storm56
     */

    private String result;
    private String token;
    private String flag;
    private String pay;
    private int memberId;
    private String memberName;
    private String memberMobile;
    private String memberExpire;
    private String memberHeadimgurl;

    private String memberProvince;
    private String memberCity;
    private String memberDistrict;


    public String getMemberProvince() {
        return memberProvince;
    }

    public void setMemberProvince(String memberProvince) {
        this.memberProvince = memberProvince;
    }

    public String getMemberCity() {
        return memberCity;
    }

    public void setMemberCity(String memberCity) {
        this.memberCity = memberCity;
    }

    public String getMemberDistrict() {
        return memberDistrict;
    }

    public void setMemberDistrict(String memberDistrict) {
        this.memberDistrict = memberDistrict;
    }

    public String getMemberHeadimgurl() {
        return memberHeadimgurl;
    }

    public void setMemberHeadimgurl(String memberHeadimgurl) {
        this.memberHeadimgurl = memberHeadimgurl;
    }

    public String getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(String memberMobile) {
        this.memberMobile = memberMobile;
    }

    public String getMemberExpire() {
        return memberExpire;
    }

    public void setMemberExpire(String memberExpire) {
        this.memberExpire = memberExpire;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}

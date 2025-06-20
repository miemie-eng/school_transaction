package com.example.web_transaction.entity;


import java.util.Date;

public class Product {
    String pname;
    int pid;
    String pdescription;
    String pvalue;
    String ptype;
    String pstate;
    String pimage;
    Date ptime;
    int puid;
   private String ownerName;
    private String ownerPhone;
    public String getOwnerPhone() {
        return ownerName;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPdescription() {
        return pdescription;
    }

    public void setPdescription(String pdescription) {
        this.pdescription = pdescription;
    }

    public String getPvalue() {
        return pvalue;
    }

    public void setPvalue(String pvalue) {
        this.pvalue = pvalue;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPstate() {
        return pstate;
    }

    public void setPstate(String pstate) {
        this.pstate = pstate;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public Date getPtime() {
        return ptime;
    }

    public void setPtime(Date ptime) {
        this.ptime = ptime;
    }

    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }
    public Product() {
    }

    // 添加带参构造方法（根据需要添加）
    public Product(String pname, String pdescription, String pvalue, String ptype,
                   String pstate, String pimage, Date ptime, int puid, String ownerName,String ownerPhone) {
        this.pname = pname;
        this.pdescription = pdescription;
        this.pvalue = pvalue;
        this.ptype = ptype;
        this.pstate = pstate;
        this.pimage = pimage;
        this.ptime = ptime;
        this.puid = puid;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }

}

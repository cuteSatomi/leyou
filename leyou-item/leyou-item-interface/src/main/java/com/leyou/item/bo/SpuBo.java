package com.leyou.item.bo;

import com.leyou.item.pojo.Spu;

/**
 * @Author zzx
 * @Date 2020-09-20 21:42
 */
public class SpuBo extends Spu {

    private String bname;
    private String cname;

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }
}

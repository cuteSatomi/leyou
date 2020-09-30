package com.leyou.common.test;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.common.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author zzx
 * @date 2020-09-30 11:39:36
 */
public class JwtTest {
    public static final String PUBLICKEYPATH = "c:\\tmp\\rsa\\rsa.pub";
    public static final String PRIVATEKEYPATH = "c:\\tmp\\rsa\\rsa.pri";
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(PUBLICKEYPATH, PRIVATEKEYPATH, "234");
    }

    //@Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(PUBLICKEYPATH);
        this.privateKey = RsaUtils.getPrivateKey(PRIVATEKEYPATH);
    }

    @Test
    public void testGenerateToken() throws Exception {
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }
}

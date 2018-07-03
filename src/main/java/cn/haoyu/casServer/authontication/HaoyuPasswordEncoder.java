package cn.haoyu.casServer.authontication;

import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/**
 * Created by haoyu on 2018/4/21.
 */
@Component("haoyuPasswordEncoder")
public class HaoyuPasswordEncoder implements PasswordEncoder {

    private final String encodingAlgorithm;
    private final String encodingSalt;

    @Value("${cas.authn.password.encoding.char:}")
    private String characterEncoding;


    @Autowired
    public HaoyuPasswordEncoder(@Value("${cas.authn.password.encoding.alg:}") final String encodingAlgorithm,
                                @Value("${cas.authn.password.encoding.md5salt:}") final String encodingSalt) {
        this.encodingAlgorithm = encodingAlgorithm;
        this.encodingSalt = encodingSalt;
    }

    public HaoyuPasswordEncoder() {
        this.encodingAlgorithm = "MD5";
        this.encodingSalt = "haoyu";
    }

    @Override
    public String encode(String password) {
        password = password + encodingSalt;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(encodingAlgorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


}

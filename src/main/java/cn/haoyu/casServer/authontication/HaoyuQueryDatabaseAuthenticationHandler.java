package cn.haoyu.casServer.authontication;

import cn.haoyu.casServer.entity.User;
import cn.haoyu.casServer.mapper.UserMapper;
import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * Created by haoyu on 2018/6/16.
 */
@Component("haoyuQueryDatabaseAuthenticationHandler")
public class HaoyuQueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private final Logger logger = LoggerFactory.getLogger(HaoyuQueryDatabaseAuthenticationHandler.class);

    @Autowired
    private UserMapper userMapper;


    public HaoyuQueryDatabaseAuthenticationHandler() {
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential) throws GeneralSecurityException, PreventedException {
        User haoyu = null;
        String inputEncodePwd = this.getPasswordEncoder().encode(credential.getPassword());
        try {
            haoyu = userMapper.findByUsername(credential.getUsername());
        } catch (Exception e) {
            logger.error(":{}", e);
            throw new FailedLoginException("登录失败:" + e.getMessage());
        }
        if (!haoyu.getPassword().equals(inputEncodePwd)) {
            throw new FailedLoginException("用户名或密码错误！");
        }

        return createHandlerResult(credential, this.principalFactory.createPrincipal(haoyu.getHid()), null);
    }

}

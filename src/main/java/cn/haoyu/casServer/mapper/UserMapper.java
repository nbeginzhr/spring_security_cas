package cn.haoyu.casServer.mapper;

import cn.haoyu.casServer.entity.User;
import org.springframework.stereotype.Repository;

/**
 * Created by haoyu on 2018/6/16.
 */
@Repository
public interface UserMapper {

    User findByUsername(String username);

}

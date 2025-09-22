package com.seeback.aicodemother.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.seeback.aicodemother.model.entity.User;
import com.seeback.aicodemother.mapper.UserMapper;
import com.seeback.aicodemother.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author seeback
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}

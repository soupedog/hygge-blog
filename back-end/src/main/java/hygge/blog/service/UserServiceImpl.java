package hygge.blog.service;

import hygge.blog.dao.UserDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.po.User;
import hygge.commons.enums.StringFormatModeEnum;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.commons.exceptions.ParameterRuntimeException;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Service
public class UserServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private UserDao userDao;

    public User findUserByUserId(Integer userId, boolean nullable) {
        User example = User.builder()
                .userId(userId)
                .build();

        Optional<User> userTemp = userDao.findOne(Example.of(example));

        return checkUserResult(userTemp, userId, nullable);
    }

    public User findUserByUid(String uid, boolean nullable) {
        User example = User.builder()
                .uid(uid)
                .build();

        Optional<User> userTemp = userDao.findOne(Example.of(example));

        return checkUserResult(userTemp, uid, nullable);
    }

    public User saveUser(User user) {
        createValidate(user);
        if (parameterHelper.isNotEmpty(userDao.findUserByUserName(user.getUserName()))) {
            throw new LightRuntimeException(String.format("User(%s) create conflict.", user.getUserName()), BlogSystemCode.USER_ALREADY_EXISTS);
        }
        user.setUserType(UserTypeEnum.NORMAL);
        user.setUserSex(parameterHelper.parseObjectOfNullable("userSex", user.getUserSex(), UserSexEnum.SECRET));
        user.setUserState(UserStateEnum.ACTIVE);
        // 取到 id
        User resultTemp = userDao.save(user);
        // 初始化 uid
        resultTemp.setUid("U" + parameterHelper.leftFillString(user.getUserId().toString(), 8, "0", StringFormatModeEnum.DEFAULT));
        return userDao.save(resultTemp);
    }

    public User updateUser(String uid, User user) {
        parameterHelper.stringNotEmpty("uid", (Object) uid);
        updateValidate(user);

        User old = findUserByUid(uid, false);

        if (parameterHelper.isNotEmpty(user.getPassword())) {
            old.setPassword(user.getPassword());
        }
        if (parameterHelper.isNotEmpty(user.getUserName())) {
            old.setUserName(user.getUserName());
        }
        if (parameterHelper.isNotEmpty(user.getUserAvatar())) {
            old.setUserAvatar(user.getUserAvatar());
        }
        if (parameterHelper.isNotEmpty(user.getUserSex())) {
            old.setUserSex(user.getUserSex());
        }
        if (parameterHelper.isNotEmpty(user.getBiography())) {
            old.setBiography(user.getBiography());
        }
        if (parameterHelper.isNotEmpty(user.getBirthday())) {
            old.setBirthday(user.getBirthday());
        }
        if (parameterHelper.isNotEmpty(user.getEmail())) {
            old.setEmail(user.getEmail());
        }
        if (parameterHelper.isNotEmpty(user.getPhone())) {
            old.setPhone(user.getPhone());
        }

        return userDao.save(old);
    }

    private User checkUserResult(Optional<User> userTemp, Integer info, boolean nullable) {
        if (!nullable && userTemp.isEmpty()) {
            throw new LightRuntimeException(String.format("User(%d) was not found.", info), BlogSystemCode.USER_NOT_FOUND);
        }

        return userTemp.orElse(null);
    }

    private User checkUserResult(Optional<User> userTemp, String info, boolean nullable) {
        if (!nullable && userTemp.isEmpty()) {
            throw new LightRuntimeException(String.format("User(%s) was not found.", info), BlogSystemCode.USER_NOT_FOUND);
        }

        return userTemp.orElse(null);
    }

    private void createValidate(User user) {
        parameterHelper.notEmpty("user", user);
        parameterHelper.stringNotEmpty("password", (Object) user.getPassword());
        parameterHelper.stringNotEmpty("userName", (Object) user.getUserName());
        parameterHelper.stringNotEmpty("userAvatar", (Object) user.getUserAvatar());
    }

    private void updateValidate(User user) {
        parameterHelper.notEmpty("user", user);

        if (parameterHelper.isAllEmpty(
                user.getPassword(),
                user.getUserName(),
                user.getUserAvatar(),
                user.getUserSex(),
                user.getBiography(),
                user.getBirthday(),
                user.getEmail(),
                user.getPhone())) {
            throw new ParameterRuntimeException("Valid modification information must not be empty.");
        }
    }
}

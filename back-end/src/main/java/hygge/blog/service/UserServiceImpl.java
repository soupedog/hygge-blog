package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.UserDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.enums.UserSexEnum;
import hygge.blog.domain.enums.UserStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.po.User;
import hygge.commons.enums.ColumnTypeEnum;
import hygge.commons.enums.StringFormatModeEnum;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.utils.UtilsCreator;
import hygge.utils.bo.ColumnInfo;
import hygge.utils.definitions.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@Service
@SuppressWarnings("java:S1192")
public class UserServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilsCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private UserDao userDao;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo("biography", null, ColumnTypeEnum.STRING, true, false, 1, 500));
        forUpdate.add(new ColumnInfo("birthday", null, ColumnTypeEnum.LONG, true, false, 0, Long.MAX_VALUE));
        forUpdate.add(new ColumnInfo("email", null, ColumnTypeEnum.STRING, true, false, 1, 500));
        forUpdate.add(new ColumnInfo("password", null, ColumnTypeEnum.STRING, true, false, 6, 50));
        forUpdate.add(new ColumnInfo("phone", null, ColumnTypeEnum.STRING, true, false, 11, 20));
        forUpdate.add(new ColumnInfo("userAvatar", null, ColumnTypeEnum.STRING, true, false, 0, 500));
        forUpdate.add(new ColumnInfo("userName", null, ColumnTypeEnum.STRING, true, false, 1, 100));
        forUpdate.add(new ColumnInfo("userSex", null, ColumnTypeEnum.STRING, true, false, 1, 100));
    }

    public User findUserByUserId(Integer userId, boolean nullable) {
        User example = User.builder()
                .userId(userId)
                .build();

        Optional<User> userTemp = userDao.findOne(Example.of(example));

        return checkUserResult(userTemp, userId, nullable);
    }

    public User findUserByUserName(String userName) {
        parameterHelper.stringNotEmpty("userName", (Object) userName);

        User example = User.builder()
                .userName(userName)
                .build();

        Optional<User> userTemp = userDao.findOne(Example.of(example));

        return userTemp.orElse(null);
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
        user.setUserSex(parameterHelper.parseObjectOfNullable("userSex", user.getUserSex(), UserSexEnum.SECRET));
        user.setUserType(UserTypeEnum.NORMAL);
        user.setUserState(UserStateEnum.ACTIVE);

        // 取到 id
        User resultTemp = userDao.save(user);
        // 初始化 uid
        resultTemp.setUid("U" + parameterHelper.leftFillString(user.getUserId().toString(), 8, "0", StringFormatModeEnum.DEFAULT));
        return userDao.save(resultTemp);
    }

    public User updateUser(String uid, Map<String, Object> data) {
        parameterHelper.stringNotEmpty("uid", (Object) uid);
        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        User old = findUserByUid(uid, false);

        checkUserRightOrHimself(old, UserTypeEnum.ROOT);

        User newOne = MapToAnyMapper.INSTANCE.mapToUser(finalData);
        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);

        return userDao.save(old);
    }

    public void notGuest() {
        if (HyggeRequestTracker.getContext().isGuest()) {
            throw new LightRuntimeException(BlogSystemCode.INSUFFICIENT_PERMISSIONS.getPublicMessage(), BlogSystemCode.INSUFFICIENT_PERMISSIONS);
        }
    }

    public void checkUserRight(User targetUser, UserTypeEnum... expectedUserType) {
        if (targetUser == null) {
            for (UserTypeEnum item : expectedUserType) {
                if (UserTypeEnum.NORMAL.equals(item)) {
                    return;
                }
            }
        } else {
            for (UserTypeEnum item : expectedUserType) {
                if (targetUser.getUserType().equals(item)) {
                    return;
                }
            }
        }
        throw new LightRuntimeException(BlogSystemCode.INSUFFICIENT_PERMISSIONS.getPublicMessage(), BlogSystemCode.INSUFFICIENT_PERMISSIONS);
    }

    public void checkUserRightOrHimself(User targetUser, UserTypeEnum... expectedUserType) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User loginUser = context.getCurrentLoginUser();

        // 验证是否是本人
        if (loginUser != null) {
            // 非访客
            if (loginUser.getUserId().equals(targetUser.getUserId())) {
                return;
            }
        } else {
            // 访客
            if (targetUser == null) {
                return;
            }
        }

        checkUserRight(targetUser, expectedUserType);
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
}

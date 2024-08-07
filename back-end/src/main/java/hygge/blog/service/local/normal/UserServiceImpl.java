package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.enums.UserSexEnum;
import hygge.blog.domain.local.enums.UserStateEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.UserDao;
import hygge.commons.constant.enums.StringFormatModeEnum;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.util.template.HyggeJsonUtilContainer;
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
public class UserServiceImpl extends HyggeJsonUtilContainer {
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private UserDao userDao;
    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, false, "biography", null).toStringColumn(1, 500));
        // mapstruct 在后续步骤会将 Long 转化为 Timestamp
        forUpdate.add(new ColumnInfo(true, false, "birthday", null, 0L, Long.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "email", null).toStringColumn(1, 500));
        forUpdate.add(new ColumnInfo(true, false, "password", null).toStringColumn(6, 50));
        forUpdate.add(new ColumnInfo(true, false, "phone", null).toStringColumn(11, 20));
        forUpdate.add(new ColumnInfo(true, false, "userAvatar", null).toStringColumn(1, 500));
        forUpdate.add(new ColumnInfo(true, false, "userName", null).toStringColumn(1, 100));
        forUpdate.add(new ColumnInfo(true, false, "userSex", null).toStringColumn(1, 100));
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
            throw new LightRuntimeException(String.format("User(%s) already exists.", user.getUserName()), BlogSystemCode.USER_ALREADY_EXISTS);
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

        checkUserRight(loginUser, expectedUserType);
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

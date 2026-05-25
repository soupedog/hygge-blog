package hygge.blog.service.local.normal;

import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.enums.AccessConditionTypeEnum;
import hygge.blog.domain.local.enums.UserSexEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.AccessCondition;
import hygge.blog.domain.local.po.Permission;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.PermissionDao;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author Xavier
 * @date 2026/5/24
 */
@Service
public class PermissionServiceImpl extends HyggeJsonUtilContainer {
    public static final Permission _PUBLIC = Permission.builder()
            .permissionId(AccessConditionTypeEnum.PUBLIC.getIndex())
            .name("公开可见")
            .build();

    private final AccessConditionServiceImpl accessConditionService;
    private final BlogGroupServiceImpl blogGroupService;
    private final PermissionDao permissionDao;

    @Autowired
    public PermissionServiceImpl(AccessConditionServiceImpl accessConditionService, BlogGroupServiceImpl blogGroupService, PermissionDao permissionDao) {
        this.accessConditionService = accessConditionService;
        this.blogGroupService = blogGroupService;
        this.permissionDao = permissionDao;
    }

    public Integer getPersonalPermissionIdOfUser(User targetUser) {
        // userId 数字取负数代表仅当前用户可见
        return -targetUser.getUserId();
    }

    public Permission savePermission(Permission permission) {
        parameterHelper.stringNotEmpty("name", (Object) permission.getName());
        collectionHelper.collectionNotEmpty("AcIdList", permission.getAcIdList());

        List<AccessCondition> accessConditionList = accessConditionService.findAccessConditionsByAcIdCollection(permission.getAcIdList());
        if (accessConditionList.size() != permission.getAcIdList().size()) {
            throw new LightRuntimeException("请确保目标 acId 已存在。");
        }

        return permissionDao.save(permission);
    }


    boolean isPermissionPassed(Integer permissionId, User targetUser, String secretKey) {
        Permission permission = findPermissionByPermissionId(permissionId, true);
        if (permission == null) {
            return false;
        }

        return getPermissionIdIfPassed(permission.getPermissionId(), targetUser, secretKey) != null;
    }

    public List<Integer> getActivePermissionIdListOfUser(User targetUser, String secretKey) {
        List<Integer> result = new ArrayList<>();
        boolean isLoginUser = targetUser != null;

        if (isLoginUser) {
            // 添加当前用户仅自己可见的 Permission
            result.add(getPersonalPermissionIdOfUser(targetUser));
        }

        // 公开访问权限默认给所有用户添加
        result.add(_PUBLIC.getPermissionId());

        List<Permission> permission_all = permissionDao.findAll();

        for (Permission item : permission_all) {
            // 权限的创建者，直接授权
            if (isLoginUser && item.getUserId().equals(targetUser.getUserId())) {
                result.add(item.getPermissionId());
                continue;
            }

            Integer permissionId = getPermissionIdIfPassed(item.getPermissionId(), targetUser, secretKey);

            if (permissionId != null) {
                result.add(permissionId);
            }
        }

        return result;
    }

    /**
     * 用户是否有对应授权，如果有则返回 permissionId，否则返回 null
     */
    private Integer getPermissionIdIfPassed(Integer permissionId, User targetUser, String secretKey) {
        List<AccessCondition> accessCondition_all = findAccessConditionListByPermissionId(permissionId);
        // isRequirement 为 true 的排前面
        accessCondition_all.sort(Comparator.comparing(AccessCondition::isRequirement).reversed());

        boolean isLoginUser = targetUser != null;
        Integer result = null;

        for (AccessCondition item : accessCondition_all) {
            switch (item.getType()) {
                case SECRET_KEY -> {
                    if (item.getExtendString().equals(secretKey)) {
                        result = permissionId;
                    }
                }
                case GROUP -> {
                    if (isLoginUser && blogGroupService.isUserInGroup(item.getExtendString(), targetUser.getUserId())) {
                        result = permissionId;
                    }
                }
                case ROLE -> {
                    UserTypeEnum expectType = UserTypeEnum.parse(item.getExtendString());
                    if (isLoginUser && expectType.equals(targetUser.getUserType())) {
                        result = permissionId;
                    }
                }
                case SEX -> {
                    UserSexEnum expectType = UserSexEnum.parse(item.getExtendString());
                    if (isLoginUser && expectType.equals(targetUser.getUserSex())) {
                        result = permissionId;
                    }
                }
            }

            // 必要条件且当前未通过，一票否决判定为无权限
            if (item.isRequirement() && result == null) {
                return null;
            }
        }

        return result;
    }

    public List<AccessCondition> findAccessConditionListByPermissionId(Integer permissionId) {
        Permission permission = findPermissionByPermissionId(permissionId, true);

        if (permission == null) {
            return Collections.emptyList();
        }

        return accessConditionService.findAccessConditionsByAcIdCollection(permission.getAcIdList());
    }

    public Permission findPermissionByPermissionId(Integer permissionId, boolean nullable) {
        if (permissionId == null) {
            return null;
        }

        Permission example = Permission.builder()
                .permissionId(permissionId)
                .build();

        Optional<Permission> userTemp = permissionDao.findOne(Example.of(example));

        return checkResult(userTemp, permissionId, nullable);
    }

    private Permission checkResult(Optional<Permission> temp, Object info, boolean nullable) {
        if (!nullable && temp.isEmpty()) {
            throw new LightRuntimeException(String.format("%s(%s) was not found.", Permission.class.getSimpleName(), parameterHelper.string(info)), BlogSystemCode.PERMISSION_NOT_FOUND);
        }

        return temp.orElse(null);
    }
}

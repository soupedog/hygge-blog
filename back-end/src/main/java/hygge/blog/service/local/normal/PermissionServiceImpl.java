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

    public Integer formatPermissionIdFromFrontEnd(User targetUser, Integer permissionId) {
        if (permissionId != null && permissionId < 0) {
            if (targetUser != null && targetUser.getUserId() != null) {
                return -targetUser.getUserId();
            }
        }
        return permissionId;
    }

    public Integer formatPermissionIdToFrontEnd(Integer permissionId) {
        if (permissionId != null && permissionId < 0) {
            return AccessConditionTypeEnum.PERSONAL.getIndex();
        }
        return permissionId;
    }

    /**
     * 可能返回 null，仅当用户为 null 时
     */
    public Integer getPersonalPermissionIdOfUser(User targetUser) {
        if (targetUser == null) {
            return null;
        }

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

    public boolean isPermissionPassed(Integer permissionId, User targetUser, String secretKey) {
        // 如果是负数权限代表仅自己可见，且不会在数据库中记录，内存中处理即可
        if (permissionId != null && permissionId < 0) {
            Integer userPermissionId = getPersonalPermissionIdOfUser(targetUser);
            if (permissionId.equals(userPermissionId)) {
                return true;
            }
        }

        if (_PUBLIC.getPermissionId().equals(permissionId)) {
            return true;
        }

        Permission permission = findPermissionByPermissionId(permissionId, true);
        if (permission == null) {
            return false;
        }

        return getPermissionIdIfPassed(permission, targetUser, secretKey) != null;
    }

    public ArrayList<Permission> getActivePermissionListOfUser(User targetUser, String secretKey) {
        ArrayList<Permission> result = new ArrayList<>();
        boolean isLoginUser = targetUser != null;

        if (isLoginUser) {
            // 添加当前用户仅自己可见的 Permission
            result.add(Permission.builder()
                    .permissionId(getPersonalPermissionIdOfUser(targetUser))
                    .name("仅自己可见")
                    .build());
        }
        // 公开访问权限默认给所有用户添加
        result.add(_PUBLIC);
        List<Permission> permission_all = permissionDao.findAll();

        for (Permission item : permission_all) {
            Integer permissionId = getPermissionIdIfPassed(item, targetUser, secretKey);

            if (permissionId != null) {
                result.add(item);
            }
        }

        return result;
    }

    public List<Integer> getActivePermissionIdListOfUser(User targetUser, String secretKey) {
        List<Permission> resultTemp = getActivePermissionListOfUser(targetUser, secretKey);
        return collectionHelper.filterNonemptyItemAsArrayList(false, resultTemp, Permission::getPermissionId);
    }

    /**
     * permission 是 database 来的数据，因此该方法内 permissionId 无负数，无需检测尽自己可见类型
     */
    private Integer getPermissionIdIfPassed(Permission permission, User targetUser, String secretKey) {
        if (permission == null) {
            return null;
        }

        boolean isLoginUser = targetUser != null;

        // 权限的创建者，直接授权
        if (isLoginUser && permission.isOwnerOfTargetUser(targetUser.getUserId())) {
            return permission.getPermissionId();
        }

        List<AccessCondition> accessCondition_all = findAccessConditionListByPermissionId(permission);
        // isRequirement 为 true 的排前面，他们是必要条件
        accessCondition_all.sort(Comparator.comparing(AccessCondition::isRequirement).reversed());

        Integer result = null;

        for (AccessCondition item : accessCondition_all) {
            switch (item.getType()) {
                case SECRET_KEY -> {
                    if (item.getExtendString().equals(secretKey)) {
                        result = permission.getPermissionId();
                    }
                }
                case GROUP -> {
                    if (isLoginUser && blogGroupService.isUserInGroup(item.getExtendString(), targetUser.getUserId())) {
                        result = permission.getPermissionId();
                    }
                }
                case ROLE -> {
                    UserTypeEnum expectType = UserTypeEnum.parse(item.getExtendString());
                    if (isLoginUser && expectType.equals(targetUser.getUserType())) {
                        result = permission.getPermissionId();
                    }
                }
                case SEX -> {
                    UserSexEnum expectType = UserSexEnum.parse(item.getExtendString());
                    if (isLoginUser && expectType.equals(targetUser.getUserSex())) {
                        result = permission.getPermissionId();
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

    /**
     * 用户是否有对应授权，如果有则返回 permissionId，否则返回 null
     */
    public Integer getPermissionIdIfPassed(Integer permissionId, User targetUser, String secretKey) {
        Permission permission = findPermissionByPermissionId(permissionId, true);
        return getPermissionIdIfPassed(permission, targetUser, secretKey);
    }

    public List<AccessCondition> findAccessConditionListByPermissionId(Permission permission) {
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

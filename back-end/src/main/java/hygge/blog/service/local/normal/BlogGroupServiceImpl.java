package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.dto.GroupBindInfo;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.BlogGroup;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.BlogGroupDao;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/22
 */
@Service
public class BlogGroupServiceImpl extends HyggeJsonUtilContainer {
    private final BlogGroupDao blogGroupDao;
    private final UserServiceImpl userService;

    public BlogGroupServiceImpl(BlogGroupDao blogGroupDao, UserServiceImpl userService) {
        this.blogGroupDao = blogGroupDao;
        this.userService = userService;
    }

    @Transactional
    public BlogGroup createCreateBlogGroup(BlogGroup blogGroup) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        parameterHelper.stringNotEmpty("groupName", (Object) blogGroup.getGroupName());

        BlogGroup old = blogGroupDao.findBlogGroupByGroupName(blogGroup.getGroupName());
        if (old != null) {
            throw new LightRuntimeException(String.format("BlogGroup(%s) already exists.", blogGroup.getGroupName()), BlogSystemCode.BLOG_GROUP_ALREADY_EXISTS);
        }

        blogGroup.setGid(randomHelper.getUniversallyUniqueIdentifier(true));
        blogGroup.setUserId(currentUser.getUserId());

        BlogGroup result = blogGroupDao.save(blogGroup);

        if (!admission(GroupBindInfo.builder().gid(result.getGid()).uid(currentUser.getUid()).build())) {
            throw new LightRuntimeException(BlogSystemCode.BLOG_GROUP_BIND_CHANGE_EXCEPTION.getPublicMessage(), BlogSystemCode.BLOG_GROUP_BIND_CHANGE_EXCEPTION);
        }
        return result;
    }

    public boolean admission(GroupBindInfo groupBindInfo) {
        parameterHelper.stringNotEmpty("gid", (Object) groupBindInfo.getGid());
        parameterHelper.stringNotEmpty("uid", (Object) groupBindInfo.getUid());

        BlogGroup blogGroup = blogGroupDao.findBlogGroupByGid(groupBindInfo.getGid());
        User user = userService.findUserByUid(groupBindInfo.getUid(), true);
        if (blogGroup != null && user != null) {
            User owner = userService.findUserByUserId(blogGroup.getUserId(), false);
            userService.checkUserRightOrHimself(owner, UserTypeEnum.ROOT);

            List<User> members = blogGroup.getMembers();
            if (members == null) {
                members = new ArrayList<>();
                members.add(user);
                blogGroup.setMembers(members);
                blogGroup = blogGroupDao.save(blogGroup);
            } else {
                if (members.stream().noneMatch(item -> item.getUid().equals(groupBindInfo.getUid()))) {
                    members.add(user);
                    blogGroup = blogGroupDao.save(blogGroup);
                }
            }

            return blogGroup.getMembers().stream().anyMatch(item -> item.getUid().equals(groupBindInfo.getUid()));
        }
        throw new LightRuntimeException(BlogSystemCode.BLOG_GROUP_BIND_CHANGE_EXCEPTION.getPublicMessage(), BlogSystemCode.BLOG_GROUP_BIND_CHANGE_EXCEPTION);
    }

    public boolean eviction(GroupBindInfo groupBindInfo) {
        parameterHelper.stringNotEmpty("gid", (Object) groupBindInfo.getGid());
        parameterHelper.stringNotEmpty("uid", (Object) groupBindInfo.getUid());

        BlogGroup blogGroup = blogGroupDao.findBlogGroupByGid(groupBindInfo.getGid());
        User user = userService.findUserByUid(groupBindInfo.getUid(), true);
        if (blogGroup != null && user != null) {
            User owner = userService.findUserByUserId(blogGroup.getUserId(), false);
            userService.checkUserRightOrHimself(owner, UserTypeEnum.ROOT);

            return blogGroupDao.eviction(user.getUserId(), blogGroup.getGroupId()) > 0;
        }
        return false;
    }
}

package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.BlogGroupDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.dto.GroupBindInfo;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.po.BlogGroup;
import hygge.blog.domain.po.User;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xavier
 * @date 2022/7/22
 */
@Service
public class BlogGroupServiceImpl extends HyggeWebUtilContainer {
    @Autowired
    private BlogGroupDao blogGroupDao;
    @Autowired
    private UserServiceImpl userService;

    @Transactional
    public BlogGroup createCreateBlogGroup(BlogGroup blogGroup) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        parameterHelper.stringNotEmpty("groupName", (Object) blogGroup.getGroupName());

        BlogGroup old = blogGroupDao.findBlogGroupByGroupName(blogGroup.getGroupName());
        if (old != null) {
            throw new LightRuntimeException(String.format("BlogGroup(%s) create conflict.", blogGroup.getGroupName()), BlogSystemCode.BLOG_GROUP_ALREADY_EXISTS);
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

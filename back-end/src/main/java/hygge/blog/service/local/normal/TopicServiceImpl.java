package hygge.blog.service.local.normal;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.common.mapper.MapToAnyMapper;
import hygge.blog.common.mapper.OverrideMapper;
import hygge.blog.domain.local.bo.BlogSystemCode;
import hygge.blog.domain.local.enums.TopicStateEnum;
import hygge.blog.domain.local.enums.UserTypeEnum;
import hygge.blog.domain.local.po.Topic;
import hygge.blog.domain.local.po.User;
import hygge.blog.repository.database.TopicDao;
import hygge.commons.exception.LightRuntimeException;
import hygge.util.UtilCreator;
import hygge.util.bo.ColumnInfo;
import hygge.util.definition.DaoHelper;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Service
public class TopicServiceImpl extends HyggeJsonUtilContainer {
    private static final DaoHelper daoHelper = UtilCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    private final UserServiceImpl userService;
    private final TopicDao topicDao;

    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo(true, false, "topicName", null).toStringColumn(1, 500));
        forUpdate.add(new ColumnInfo(true, false, "uid", null).toStringColumn(0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "orderVal", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo(true, false, "topicState", null).toStringColumn(0, 50));
    }

    @Autowired
    public TopicServiceImpl(UserServiceImpl userService, TopicDao topicDao) {
        this.userService = userService;
        this.topicDao = topicDao;
    }

    public Topic createTopic(Topic topic) {
        parameterHelper.stringNotEmpty("topicName", (Object) topic.getTopicName());
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Topic old = topicDao.findTopicByTopicName(topic.getTopicName());

        if (old != null) {
            throw new LightRuntimeException(String.format("Topic(%s) already exists.", topic.getTopicName()), BlogSystemCode.TOPIC_ALREADY_EXISTS);
        }

        topic.setTid(randomHelper.getUniversallyUniqueIdentifier(true));
        topic.setUserId(currentUser.getUserId());
        topic.setOrderVal(parameterHelper.integerFormatOfNullable("orderVal", topic.getOrderVal(), 0));
        topic.setTopicState(parameterHelper.parseObjectOfNullable("topicState", topic.getTopicState(), TopicStateEnum.ACTIVE));
        return topicDao.save(topic);
    }

    public Topic updateTopic(String tid, Map<String, Object> data) {
        HyggeRequestContext context = HyggeRequestTracker.getContext();
        User currentUser = context.getCurrentLoginUser();
        userService.checkUserRight(currentUser, UserTypeEnum.ROOT);

        Topic old = findTopicByTid(tid, false);

        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        String topicName = (String) finalData.get("topicName");
        if (topicName != null) {
            nameConflictCheck(topicName);
        }

        String uid = (String) finalData.get("uid");
        if (uid != null) {
            User newOne = userService.findUserByUid(uid, false);
            finalData.put("userId", newOne.getUserId());
        }

        Topic newOne = MapToAnyMapper.INSTANCE.mapToTopic(finalData);
        OverrideMapper.INSTANCE.overrideToAnother(newOne, old);
        return topicDao.save(old);
    }

    public List<Topic> findAllTopic() {
        return topicDao.findAll(Example.of(Topic.builder().topicState(TopicStateEnum.ACTIVE).build()), Sort.by(Sort.Order.desc("orderVal")));
    }

    public void nameConflictCheck(String topicName) {
        Topic old = topicDao.findTopicByTopicName(topicName);
        if (old != null) {
            throw new LightRuntimeException(String.format("Topic(%s) already exists.", topicName), BlogSystemCode.ARTICLE_CATEGORY_ALREADY_EXISTS);
        }
    }

    public Topic findTopicByTid(String tid, boolean nullable) {
        Topic result = topicDao.findTopicByTid(tid);
        return checkTopicResult(result, tid, nullable);
    }

    public Topic findTopicByTopicId(Integer topicId, boolean nullable) {
        Topic result = topicDao.findById(topicId).orElse(null);
        return checkTopicResult(result, topicId, nullable);
    }

    private Topic checkTopicResult(Topic topicTemp, String info, boolean nullable) {
        if (!nullable && topicTemp == null) {
            throw new LightRuntimeException(String.format("Topic(%s) was not found.", info), BlogSystemCode.TOPIC_NOT_FOUND);
        }
        return topicTemp;
    }

    private Topic checkTopicResult(Topic topicTemp, Integer info, boolean nullable) {
        if (!nullable && topicTemp == null) {
            throw new LightRuntimeException(String.format("Topic(%d) was not found.", info), BlogSystemCode.TOPIC_NOT_FOUND);
        }
        return topicTemp;
    }
}

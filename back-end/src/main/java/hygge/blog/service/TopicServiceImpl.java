package hygge.blog.service;

import hygge.blog.common.HyggeRequestContext;
import hygge.blog.common.HyggeRequestTracker;
import hygge.blog.dao.TopicDao;
import hygge.blog.domain.bo.BlogSystemCode;
import hygge.blog.domain.enums.TopicStateEnum;
import hygge.blog.domain.enums.UserTypeEnum;
import hygge.blog.domain.mapper.MapToAnyMapper;
import hygge.blog.domain.mapper.OverrideMapper;
import hygge.blog.domain.po.Topic;
import hygge.blog.domain.po.User;
import hygge.commons.enums.ColumnTypeEnum;
import hygge.commons.exceptions.LightRuntimeException;
import hygge.utils.UtilsCreator;
import hygge.utils.bo.ColumnInfo;
import hygge.utils.definitions.DaoHelper;
import hygge.web.template.HyggeWebUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/23
 */
@Service
public class TopicServiceImpl extends HyggeWebUtilContainer {
    private static final DaoHelper daoHelper = UtilsCreator.INSTANCE.getDefaultInstance(DaoHelper.class);
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private TopicDao topicDao;

    private static final Collection<ColumnInfo> forUpdate = new ArrayList<>();

    static {
        forUpdate.add(new ColumnInfo("topicName", null, ColumnTypeEnum.STRING, true, false, 1, 500));
        forUpdate.add(new ColumnInfo("uid", null, ColumnTypeEnum.STRING, true, false, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("orderVal", null, ColumnTypeEnum.INTEGER, true, false, 0, Integer.MAX_VALUE));
        forUpdate.add(new ColumnInfo("topicState", null, ColumnTypeEnum.STRING, true, false, 6, 50));
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

        Topic old = topicDao.findTopicByTid(tid);

        if (old == null) {
            throw new LightRuntimeException(String.format("Topic(%s) was not found.", tid), BlogSystemCode.TOPIC_NOT_FOUND);
        }

        HashMap<String, Object> finalData = daoHelper.filterOutTheFinalColumns(data, forUpdate);

        String topicName = (String) finalData.get("topicName");
        if (topicName != null) {
            Topic existOne = topicDao.findTopicByTopicName(topicName);
            if (existOne != null) {
                throw new LightRuntimeException(String.format("Topic(%s) already exists.", existOne.getTopicName()), BlogSystemCode.TOPIC_ALREADY_EXISTS);
            }
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

    public Topic findTopicByTid(String tid, boolean nullable) {
        Topic result = topicDao.findTopicByTid(tid);
        return checkTopicResult(result, tid, nullable);
    }

    private Topic checkTopicResult(Topic topicTemp, String info, boolean nullable) {
        if (!nullable && topicTemp == null) {
            throw new LightRuntimeException(String.format("Topic(%s) was not found.", info), BlogSystemCode.TOPIC_NOT_FOUND);
        }
        return topicTemp;
    }
}

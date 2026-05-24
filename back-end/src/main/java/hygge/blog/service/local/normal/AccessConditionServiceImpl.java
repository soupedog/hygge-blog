package hygge.blog.service.local.normal;

import hygge.blog.domain.local.enums.AccessConditionTypeEnum;
import hygge.blog.domain.local.po.AccessCondition;
import hygge.blog.repository.database.AccessConditionDao;
import hygge.util.template.HyggeJsonUtilContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Xavier
 * @date 2026/5/24
 */
@Service
public class AccessConditionServiceImpl extends HyggeJsonUtilContainer {
    public static final AccessCondition _PUBLIC = AccessCondition.builder()
            .acId(AccessConditionTypeEnum.PUBLIC.getIndex())
            .type(AccessConditionTypeEnum.PUBLIC)
            .build();
    public static final AccessCondition _PERSONAL = AccessCondition.builder()
            .acId(AccessConditionTypeEnum.PERSONAL.getIndex())
            .type(AccessConditionTypeEnum.PERSONAL)
            .build();

    private final AccessConditionDao accessConditionDao;

    @Autowired
    public AccessConditionServiceImpl(AccessConditionDao accessConditionDao) {
        this.accessConditionDao = accessConditionDao;
    }

    public List<AccessCondition> findAccessConditionsByAcIdCollection(Collection<Integer> acIdCollection) {
        if (acIdCollection == null || acIdCollection.isEmpty()) {
            return Collections.emptyList();
        }
        return accessConditionDao.findAccessConditionsByAcIdIn(acIdCollection);
    }

    public List<AccessCondition> findAll() {
        return accessConditionDao.findAll();
    }
}

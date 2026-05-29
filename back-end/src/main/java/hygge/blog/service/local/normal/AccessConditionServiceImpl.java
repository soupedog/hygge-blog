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
    private final AccessConditionDao accessConditionDao;

    public AccessConditionServiceImpl(AccessConditionDao accessConditionDao) {
        this.accessConditionDao = accessConditionDao;
    }

    public AccessCondition saveAccessCondition(AccessCondition accessCondition) {
        parameterHelper.notEmpty("accessCondition", accessCondition);

        if (!AccessConditionTypeEnum.PUBLIC.equals(accessCondition.getType())) {
            parameterHelper.stringNotEmpty("ExtendString", (Object) accessCondition.getExtendString());
        }
        return accessConditionDao.save(accessCondition);
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

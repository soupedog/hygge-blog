import {useContext} from 'react';
import {Badge, Card, Collapse, type CollapseProps} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import clsx from 'clsx';

const gridStyle: React.CSSProperties = {
    // 移动端适配
    width: '20%',
    padding: '10px',
    textAlign: 'center',
};

export default function HomeCategoryCollapse() {
    const {
        categoryCollapsed,
        setKeywordType,
        categoryList,
        searchTabPageSize,
        searchPostSummaryByCid,
    } = useContext(HomeContext);

    const items: CollapseProps['items'] = [
        {
            // 默认展开 default 面板
            key: 'default',
            label: '文章类别目录',
            children:
                <Card size={'small'}>
                    {categoryList.map((item, index) => {
                        if (item.articleCount < 1) {
                            return null;
                        }
                        return (
                            <Card.Grid key={'card_' + item.categoryName} className={clsx(['pointer'])} style={gridStyle}
                                       onClick={() => {
                                           setKeywordType(HomeKeywordType.POST);
                                           searchPostSummaryByCid({cid: item.cid, currentPage: 1, pageSize: searchTabPageSize});
                                       }}
                            >
                                <Badge.Ribbon key={'card_ribbon' + item.categoryName} style={{top: '-10px'}} text={item.articleCount} color='red'>
                                    <div style={{padding: '0 15px 0 15px'}}>
                                        {item.categoryName}
                                    </div>
                                </Badge.Ribbon>
                            </Card.Grid>
                        )
                    })}
                </Card>
        }
    ];

    return (
        <Collapse activeKey={[categoryCollapsed ? 'collapsed' : 'default']} items={items}/>
    );
}

import {useContext, useMemo} from 'react';
import {Badge, Card, Collapse, type CollapseProps} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import {HomeKeywordType} from '../../enums/EnumKeeper.ts';
import clsx from 'clsx';
import {detectDevice} from '@al01/detectdevice/dist';

const device = detectDevice();

const gridStyle: React.CSSProperties = {
    // 移动端简单适配
    width: device.isDesktop ? '20%' : '25%',
    padding: '10px',
    textAlign: 'center',
};

export default function HomeCategoryCollapse() {
    const {
        categoryCollapsed,
        setKeywordType,
        currentCategoryInfo,
        searchResultCurrentPage, setSearchResultCurrentPage,
        searchResultPageSize, setSearchResultPageSize,
        setSearchCategoryInfo,
        searchPostSummaryByCid,
        setSearchResultOrderEnable,
        activeTap, setActiveTap,
    } = useContext(HomeContext);

    const items: CollapseProps['items'] = useMemo(() => {
        return [
            {
                // 默认展开 default 面板
                key: 'default',
                label: '文章类别目录',
                children:
                    <Card size={'small'}>
                        {currentCategoryInfo.map((item, index) => {
                            if (item.articleCount < 1) {
                                return null;
                            }
                            return (
                                <Card.Grid key={'card_' + item.categoryName} className={clsx(['pointer'])} style={gridStyle}
                                           onClick={() => {
                                               setSearchResultOrderEnable(true);
                                               setKeywordType(HomeKeywordType.POST);

                                               setSearchCategoryInfo(item.cid);
                                               setSearchResultCurrentPage(1);
                                               setSearchResultPageSize(5);
                                               // TODO 分页信息非初始值，修改分页信息 HomeTabSearchContent 的 useEffect 会自动触发查询
                                               searchPostSummaryByCid({cid: item.cid, currentPage: 1, pageSize: 5});

                                               setActiveTap('搜索结果');
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
    }, [activeTap]);

    return (
        <Collapse activeKey={[categoryCollapsed ? 'collapsed' : 'default']} items={items} style={{backgroundColor: '#FFF'}}/>
    );
}

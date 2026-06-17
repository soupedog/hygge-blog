import {useContext} from 'react';
import {Timeline} from 'antd';
import {HomeContext} from '../context/HomeContext.tsx';
import {TimeHelper} from '../../util/TimeHelper.ts';
import {TimeType} from '../../enums/EnumKeeper.ts';

export default function HomeTabAnnouncementContent() {
    const {
        announcementInfoList,
    } = useContext(HomeContext);

    const dynamicTabs = announcementInfoList.map(item => (
        {
            color: item.color,
            title: TimeHelper.formatTimeStampToString(item.createTs, TimeType.yyyy_mm_dd),
            content: item.paragraphList.join('\n'),
        }
    ));

    const items = [
        ...dynamicTabs,
        {
            loading: true,
            content: 'To be continued...',
        },
    ];

    return (
        <Timeline titleSpan='25%' items={items} mode='start' reverse orientation='vertical'/>
    );
}
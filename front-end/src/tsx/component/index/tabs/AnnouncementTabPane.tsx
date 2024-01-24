import React from 'react';
import {Timeline} from "antd";
import {AnnouncementDto} from "../../../rest/ApiClient";
import {TimeHelper} from "../../../util/UtilContainer";

function AnnouncementTabPane({announcementDtoList}: { announcementDtoList: AnnouncementDto[] }) {
    return (
        <Timeline items={createTimelineItems(announcementDtoList)}
                  mode={"left"}
                  reverse={true}
                  pending="To be continued..."
        />
    );
}

function createTimelineItems(announcementList: AnnouncementDto[]) {
    let result = new Array<any>();

    announcementList.forEach(item => {
        result.push(
            {
                color: item.color,
                label: TimeHelper.formatTimeStampToString(item.createTs),
                children: <>
                    {
                        item.paragraphList.map((paragraph, index) => {
                            return (
                                <p key={"p_" + item.announcementId + "_" + index}>{paragraph}</p>
                            )
                        })
                    }
                </>,
            }
        );
    });
    return result;
}

export default AnnouncementTabPane;
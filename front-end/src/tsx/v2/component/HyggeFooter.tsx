import React, {createContext, useMemo, useState} from 'react';
import {Footer} from "antd/es/layout/layout";
import {Typography} from "antd";

const {Paragraph, Text} = Typography;
// 获取当前年份
let currentYear = new Date().getFullYear();

function HyggeFooter() {
    return (
        <Footer className={"textCenter"} key={"hygge_footer"}>
            <Paragraph strong={true}>
                Made with
                <Text>
                    <a className="dependentLink" target="_blank"
                       href="https://www.typescriptlang.org/"> TypeScript</a>
                </Text>
                <Text>
                    &nbsp;&amp;&nbsp;<a className="dependentLink" target="_blank"
                                        href="https://react.docschina.org"> React</a>
                </Text>
                <Text>
                    &nbsp;&amp;&nbsp;<a className="dependentLink" target="_blank"
                                        href="https://ant-design.gitee.io">Ant Design</a>
                </Text>
            </Paragraph>
            <Paragraph strong={true}>
                Copyright© 2019-{currentYear} 我的小宅子 Power by Xavier
            </Paragraph>
            <Paragraph>
                <a className="textItem policeLink" target="_blank"
                   href="https://beian.miit.gov.cn">津ICP备18004196号-1&nbsp;</a>
                <a className="textItem policeLink" target="_blank"
                   href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=12010402000667">
                    <img src="https://www.xavierwang.cn/static/icon-police.png"/>
                    <span>&nbsp;津公网安备12010402000667号</span>
                </a>
            </Paragraph>
        </Footer>
    );
}

export default HyggeFooter;
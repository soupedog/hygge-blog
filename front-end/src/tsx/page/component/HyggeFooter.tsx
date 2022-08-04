import * as React from "react"
import {LogHelper} from '../../utils/UtilContainer';
import {Layout, Typography} from "antd";

const {Footer} = Layout;
const {Text, Paragraph} = Typography;

// 描述该组件 props 数据类型
export interface HyggeFooterProps {
}

// 描述该组件 states 数据类型
export interface HyggeFooterStatus {
}

export class HyggeFooter extends React.Component<HyggeFooterProps, HyggeFooterStatus> {
    constructor(props: HyggeFooterProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "HyggeFooter", msg: "初始化成功"});
    }

    render() {
        let currentYear = new Date().getFullYear();

        return (
            <Footer className={"textCenter"} key={"hygge_footer"}>
                <Paragraph strong={true}>
                    Made with
                    <Text>
                        <a className="dependentLink" target="_blank" href="https://www.typescriptlang.org/"> TypeScript</a>
                    </Text>
                    <Text>
                        &nbsp;&amp;&nbsp;<a className="dependentLink" target="_blank"
                                            href="https://react.docschina.org"> React</a>
                    </Text>
                    <Text>
                        &nbsp;&amp;&nbsp;<a className="dependentLink" target="_blank"
                                            href="https://ant.design/index-cn">Ant Design</a>
                    </Text>
                </Paragraph>
                <Paragraph strong={true}>
                    Copyright© 20019-{currentYear} 我的小宅子 Power by Xavier
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
}

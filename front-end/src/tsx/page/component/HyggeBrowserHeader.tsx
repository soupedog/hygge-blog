import * as React from "react"
import {LogHelper, WindowsEventHelper} from '../../utils/UtilContainer';
import {Layout, Tooltip} from "antd";
import clsx from "clsx";
import {RollbackOutlined} from "@ant-design/icons";
import HyggeUserMenu from "./HyggeUserMenu";
import {ReactRouter, withRouter} from "../../utils/ReactRouterHelper";
import {UserService} from "../../rest/ApiClient";

const {Header} = Layout;

// 描述该组件 props 数据类型
export interface HyggeBrowserHeaderProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface HyggeBrowserHeaderState {
    headerTransparent: boolean,
}

class HyggeBrowserHeader extends React.Component<HyggeBrowserHeaderProps, HyggeBrowserHeaderState> {
    constructor(props: HyggeBrowserHeaderProps) {
        super(props);
        this.state = {
            headerTransparent: true
        };
        LogHelper.info({className: "HyggeBrowserHeader", msg: "初始化成功"});
    }

    render() {
        return (
            <Header style={{position: 'fixed', zIndex: 999, width: '100%'}}
                    className={clsx({
                        "backgroundTransparent": this.state.headerTransparent
                    })}>
                <div className={"floatToLeft"}>
                    <Tooltip placement="bottom" title={"返回首页"}>
                        <RollbackOutlined onClick={() => {
                            let finalPath = "/";
                            if (this.props.router.searchParams.has("secretKey")) {
                                finalPath = finalPath + "?secretKey=" + this.props.router.searchParams.get("secretKey")
                            }
                            this.props.router.navigate(finalPath);
                        }} style={{color: "#fff", fontWeight: "bold", fontSize: "24px", lineHeight: "64px"}}/>
                    </Tooltip>
                </div>
                <div className={"floatToRight"}>
                    <HyggeUserMenu currentUser={UserService.getCurrentUser()}/>
                </div>
            </Header>
        );
    }

    componentDidMount() {
        let _react = this;

        WindowsEventHelper.addCallback_Scroll({
            name: "APPBar 透明判定", delta: 50, callbackFunction: function ({currentScrollY}) {
                if (currentScrollY > 336) {
                    _react.setState({headerTransparent: false});
                } else {
                    _react.setState({headerTransparent: true});
                }
            }
        });
        WindowsEventHelper.start_OnScroll();
    }
}

export default withRouter(HyggeBrowserHeader)

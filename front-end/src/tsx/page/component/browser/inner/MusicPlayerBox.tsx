import * as React from "react"
import {LogHelper, PropertiesHelper} from "../../../../utils/UtilContainer";
import {ArticleConfiguration} from "../../../../rest/ApiClient";
// @ts-ignore
import APlayer from 'APlayer';

// 描述该组件 props 数据类型
export interface MusicPlayerBoxProps {
    configuration: ArticleConfiguration,
}

// 描述该组件 states 数据类型
export interface MusicPlayerBoxStatus {
    ap?: any;
}

export class MusicPlayerBox extends React.Component<MusicPlayerBoxProps, MusicPlayerBoxStatus> {
    constructor(props: MusicPlayerBoxProps) {
        super(props);
        this.state = {};
        LogHelper.info({className: "MusicPlayerBox", msg: "初始化成功"});
    }

    render() {
        switch (this.props.configuration.backgroundMusicType) {
            case "DEFAULT":
                return (
                    <div id={"playerContainer"}></div>
                );
            case "WANG_YI_YUN":
                return (
                    <iframe width={"100%"}
                            height={"105px"}
                            src={this.props.configuration.src}></iframe>
                )
            default:
                return null;
        }
    }

    componentDidMount() {
        let _react = this;
        if (_react.props.configuration.backgroundMusicType == "DEFAULT") {
            let ap = new APlayer({
                lrcType: PropertiesHelper.isStringNotEmpty(_react.props.configuration.lrc) ? 1 : null,
                container: document.getElementById('playerContainer'),
                audio: [{
                    autoplay: true,
                    name: _react.props.configuration.name,
                    artist: _react.props.configuration.artist,
                    url: _react.props.configuration.src,
                    cover: _react.props.configuration.coverSrc,
                    lrc: _react.props.configuration.lrc
                }]
            });
            this.setState({ap: ap});
        }
    }
}

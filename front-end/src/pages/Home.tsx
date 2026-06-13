import {useEffect} from 'react';
import {Col, Row} from "antd";

import './Home.less'
import './Home.scss'

export interface HomeProps {
    readonly text: string
}

export default function Home({text}: HomeProps) {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
        document.title = "主页";
    }, [text]);

    return (
        <>
            <Row gutter={[0, 0]}>
                <Col span={8}>
                    <div className="text1">
                        css
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text2">
                        less
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text3">
                        scss
                    </div>
                </Col>
            </Row>
            <Row gutter={[0, 0]}>
                <Col span={8}>
                    <div className="text1">
                        css
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text2">
                        less
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text3">
                        scss
                    </div>
                </Col>
            </Row>
            <Row gutter={[0, 0]}>
                <Col span={8}>
                    <div className="text1">
                        css
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text2">
                        less
                    </div>
                </Col>
                <Col span={8}>
                    <div className="text3">
                        scss
                    </div>
                </Col>
            </Row>
        </>
    );
}

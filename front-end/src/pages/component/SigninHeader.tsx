import * as React from 'react';
import {Header} from 'antd/es/layout/layout';
import {Spin} from 'antd';

export interface SimpleHeaderProps {
    readonly title: string;
    readonly isAnyPending: boolean;
}


const simpleHeaderSpinStyle: React.CSSProperties = {
    float: 'right',
    margin: '1rem 0 1rem 0',
}

export default function SigninHeader({title, isAnyPending}: SimpleHeaderProps) {

    return (
        <Header>
            <div className={'hygge-header-title'}>{title}</div>
            <Spin spinning={isAnyPending} size='large' style={simpleHeaderSpinStyle}/>
        </Header>
    );
}

import * as React from 'react';
import {Header} from 'antd/es/layout/layout';
import {Spin} from 'antd';

export interface SimpleHeaderProps {
    readonly title: string;
    readonly isAnyPending: boolean;
}

const simpleHeaderTitleStyle: React.CSSProperties = {
    float: 'left',
    width: '6.25rem',
    height: '2rem',
    margin: '1rem',
    background: 'rgba(255, 255, 255, 0.3)',
    fontSize: '1.125rem',
    textAlign: 'center',
    fontWeight: 'bold',
    lineHeight: '2rem',
    color: 'white',
    borderRadius: '0.25rem'
}

const simpleHeaderSpinStyle: React.CSSProperties = {
    float: 'right',
    margin: '1rem 0 1rem 0',
}

export default function SimpleHeader({title, isAnyPending}: SimpleHeaderProps) {

    return (
        <Header>
            <div style={simpleHeaderTitleStyle}>{title}</div>
            <Spin spinning={isAnyPending} size='large' style={simpleHeaderSpinStyle}/>
        </Header>
    );
}

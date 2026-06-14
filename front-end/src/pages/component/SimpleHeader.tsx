import {Header} from 'antd/es/layout/layout';
import * as React from 'react';

export interface SimpleHeaderProps {
    readonly title: string
}

const headerTitleStyle: React.CSSProperties = {
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

export default function SimpleHeader({title}: SimpleHeaderProps) {
    return (
        <Header>
            <div style={headerTitleStyle}>{title}</div>
        </Header>
    );
}

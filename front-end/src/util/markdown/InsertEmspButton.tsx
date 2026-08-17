// components/InsertEmspButton.tsx
import React from 'react';
import {type ExposeParam, NormalToolbar} from 'md-editor-rt';
import {BorderOutlined} from '@ant-design/icons';

interface InsertEmspButtonProps {
    title?: string;
    editorRef?: React.RefObject<ExposeParam | undefined>;
}

const InsertEmspButton: React.FC<InsertEmspButtonProps> = ({
                                                               title = '插入空格',
                                                               editorRef,
                                                           }) => {
    const handleClick = () => {
        const editor = editorRef?.current;
        // 使用官方示例的 insert 方法
        editor!.insert((selectedText) => ({
            targetValue: `${selectedText}&emsp;&emsp;`,
            select: false,
            deviationStart: 0,
            deviationEnd: 0,
        }));
    };

    return (
        <NormalToolbar
            title={title}
            onClick={handleClick}
            children={
                <>
                    <div style={{
                        width: '24px',
                        height: '24px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}>
                        <BorderOutlined/>
                    </div>
                    <div className={'md-editor-toolbar-item-name'}>插入空格</div>
                </>
            }
        />
    );
};

export default InsertEmspButton;
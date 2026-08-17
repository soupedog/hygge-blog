import React from 'react';
import {type ExposeParam, NormalToolbar} from 'md-editor-rt';

interface MySupButtonProps {
    title?: string;
    editorRef?: React.RefObject<ExposeParam | undefined>;
}

const MySubButton: React.FC<MySupButtonProps> = ({
                                                     title = '下标',
                                                     editorRef,
                                                 }) => {
    const handleClick = () => {
        const editor = editorRef?.current;
        // 使用官方示例的 insert 方法
        editor!.insert((selectedText) => ({
            targetValue: `<sub>${selectedText}</sub>`,
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
                    <svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'
                         className='lucide lucide-subscript md-editor-icon' aria-hidden='true'>
                        <path d='m4 5 8 8'></path>
                        <path d='m12 5-8 8'></path>
                        <path d='M20 19h-4c0-1.5.44-2 1.5-2.5S20 15.33 20 14c0-.47-.17-.93-.48-1.29a2.11 2.11 0 0 0-2.62-.44c-.42.24-.74.62-.9 1.07'></path>
                    </svg>
                    <div className={'md-editor-toolbar-item-name'}>下标</div>
                </>
            }
        />
    );
};

export default MySubButton;
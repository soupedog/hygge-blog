import React from 'react';
import {type ExposeParam, NormalToolbar} from 'md-editor-rt';

interface MySupButtonProps {
    title?: string;
    editorRef?: React.RefObject<ExposeParam | undefined>;
}

const MySupButton: React.FC<MySupButtonProps> = ({
                                                     title = '上标',
                                                     editorRef,
                                                 }) => {
    const handleClick = () => {
        const editor = editorRef?.current;
        // 使用官方示例的 insert 方法
        editor!.insert((selectedText) => ({
            targetValue: `<sup>${selectedText}</sup>`,
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
                         className='lucide lucide-superscript md-editor-icon' aria-hidden='true'>
                        <path d='m4 19 8-8'></path>
                        <path d='m12 19-8-8'></path>
                        <path d='M20 12h-4c0-1.5.442-2 1.5-2.5S20 8.334 20 7.002c0-.472-.17-.93-.484-1.29a2.105 2.105 0 0 0-2.617-.436c-.42.239-.738.614-.899 1.06'></path>
                    </svg>
                    <div className={'md-editor-toolbar-item-name'}>上标</div>
                </>
            }
        />
    );
};

export default MySupButton;
import React, {useEffect} from 'react';

function MarkIcon() {

    useEffect(() => {
        // 依赖静态值表示仅初始化时调用一次
    }, []);

    return (
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
             viewBox="0 0 24 24" fill="none" stroke="currentColor"
             strokeWidth="2" strokeLinecap="round"
             strokeLinejoin="round"
             className="lucide lucide-highlighter md-editor-icon"
             aria-hidden="true">
            <path d="m9 11-6 6v3h9l3-3"></path>
            <path d="m22 12-4.6 4.6a2 2 0 0 1-2.8 0l-5.2-5.2a2 2 0 0 1 0-2.8L14 4"></path>
        </svg>
    );
}

export default MarkIcon;
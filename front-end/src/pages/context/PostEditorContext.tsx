import {createContext, type ReactNode, useState} from 'react';

export interface PostEditorContextState {
    text: string;
    setText: (input: string) => void;
}

export const PostEditorContext = createContext<PostEditorContextState>({} as PostEditorContextState);

export const PostEditorContextProvider = ({children}: { children: ReactNode }) => {
    const [text, setText] = useState('test');

    return (
        <PostEditorContext value={{
            text: text, setText: setText
        }}>
            {children}
        </PostEditorContext>
    );
}

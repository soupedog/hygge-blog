import {useMutation} from '@tanstack/react-query';
import {UserClient} from './ApiClient.ts';
import {message} from 'antd';

const apiZIndex = 20001;

// 注意：这是一个自定义 Hook 函数，不是类方法
export function useUserService() {
    const signInMutation = useMutation({
        mutationFn: UserClient.signIn,
        // 完整有 4 个参数，此处没用全
        onSuccess: (data) => {
            message.success({content: '登录成功！', duration: 2, style: {zIndex: apiZIndex}});
        }
    });

    return {
        signIn: signInMutation,
    };
}
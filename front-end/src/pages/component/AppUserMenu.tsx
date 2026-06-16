import {CloseCircleOutlined, EditOutlined, PaperClipOutlined} from '@ant-design/icons';
import {useParams} from 'react-router-dom';
import {Avatar, Button, Dropdown, type MenuProps, message} from 'antd';
import {UserClient} from '../../util/ApiClient.ts';
import UrlHelper from '../../util/UrlHelper.ts';
import PropertiesHelper from '../../util/PropertiesHelper.ts';

const items: MenuProps['items'] = [
    {
        label: '编辑文章',
        key: 'editPost',
        icon: <EditOutlined/>,
    },
    {
        label: '编辑句子收藏',
        key: 'editQuote',
        icon: <EditOutlined/>,
    },
    {
        label: '文件管理',
        key: 'fileGlance',
        icon: <PaperClipOutlined/>,
    },
    {
        label: '文件操作',
        key: 'fileOperation',
        icon: <PaperClipOutlined/>,
    },
    {
        label: '登出',
        key: 'signOut',
        icon: <CloseCircleOutlined/>,
    },
];

export default function AppUserMenu() {
    const user = UserClient.getCurrentUser();
    const {pid} = useParams();

    const onClick: MenuProps['onClick'] = ({key}) => {
        switch (key) {
            case 'editPost':
                if (PropertiesHelper.isStringNotEmpty(pid)) {
                    UrlHelper.navigateTo({path: `/manage/editor/post?pid=${pid}`});
                } else {
                    UrlHelper.navigateTo({path: `/manage/editor/post`});
                }
                break;
            case 'editQuote':
                UrlHelper.navigateTo({path: `/manage/editor/quote`});
                break;
            case 'fileGlance':
                UrlHelper.navigateTo({path: `/manage/file/glance`});
                break;
            case 'fileOperation':
                UrlHelper.navigateTo({path: `/manage/file/operate`});
                break;
            case 'signOut':
                UserClient.removeCurrentUser();
                message.success('登出成功，1 s 内将跳转回首页。');
                UrlHelper.navigateTo({path: `/`, delayTime: 1000});
                break;
        }
    };

    if (user == null) {
        return (
            <div>
                <Button type='primary' onClick={() => {
                    UrlHelper.navigateTo({path: `/signin`});
                }}>
                    登录
                </Button>
            </div>
        );
    } else {
        return (
            <Dropdown menu={{items, onClick}}>
                <Avatar className={'pointer'} size={44} src={user.userAvatar}/>
            </Dropdown>
        );
    }

}

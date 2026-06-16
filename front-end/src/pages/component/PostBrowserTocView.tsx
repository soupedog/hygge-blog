import * as React from 'react';
import {useEffect, useState} from 'react';
import {Affix, Card, message, Tree, type TreeProps} from 'antd';
import {DownOutlined} from '@ant-design/icons';
import {type TreeNodeInfo} from '../../util/markdown/MdHelper.ts';

export interface PostBrowserTocViewProps {
    readonly tocTree: Array<TreeNodeInfo>;
}

// 目录选中自动跳转函数
const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
    // @ts-ignore
    let item: TreeNodeInfo = info.node;

    // 用标签类型 + data-line 属性做筛选
    let element = document.querySelector(item.nodeName + '[data-line="' + item.dataLine + '"]');

    if (element != undefined) {
        // 滚动到锚点元素的顶部(offsetTop 是数字类型，你可以在此基础上追加偏移量)

        window.scrollTo({
            // @ts-ignore
            top: element.offsetTop + 540,
            behavior: 'smooth'
        });

        // 拿到 dom 元素可以直接使用此方法滚动到目标位置(无法追加偏移量)
        // element.scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'});
    } else {
        message.warning('未找到对应跳转锚点');
    }
};

// 获取树中所有节点的key（用于全展开）
const getAllKeys = (treeData: any[]): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (nodes: any[]) => {
        nodes.forEach(node => {
            keys.push(node.key);
            if (node.children && node.children.length > 0) {
                traverse(node.children);
            }
        });
    };
    traverse(treeData);
    return keys;
};

// 获取指定层级的节点keys（比如只展开前几层）
const getKeysByLevel = (treeData: any[], maxLevel: number, currentLevel: number = 1): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (nodes: any[], level: number) => {
        nodes.forEach(node => {
            if (level <= maxLevel) {
                keys.push(node.key);
                if (node.children && node.children.length > 0) {
                    traverse(node.children, level + 1);
                }
            }
        });
    };
    traverse(treeData, currentLevel);
    return keys;
};

// 计算树的总节点数
const countTreeNodes = (treeData: any[]): number => {
    let count = 0;
    const traverse = (nodes: any[]) => {
        nodes.forEach(node => {
            count++;
            if (node.children && node.children.length > 0) {
                traverse(node.children);
            }
        });
    };
    traverse(treeData);
    return count;
};

export default function PostBrowserTocView({tocTree}: PostBrowserTocViewProps) {
    const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);

    useEffect(() => {
        if (!tocTree || tocTree.length === 0) return;

        const totalCount = countTreeNodes(tocTree);
        let keys: React.Key[];

        if (totalCount <= 10) {
            // 10个以内全部展开
            keys = getAllKeys(tocTree);
        } else {
            // 超过10个，只展开前 1 层（可以根据需要调整）
            keys = getKeysByLevel(tocTree, 1);
        }

        setExpandedKeys(keys);
    }, [tocTree]);

    const onExpand = (keys: React.Key[]) => {
        setExpandedKeys(keys);
    };

    return (
        <Card
            style={{
                height: '100%',
                backgroundColor: '#F0F2F5'
            }}
            styles={{body: {padding: 8}}}
            className={'postBrowserToc'}>
            <Affix style={{
                // zIndex: 9999,
                position: 'absolute',
                top: 180,
                right: 0,
                width: '100%'
            }} offsetTop={180}>
                <Card style={{marginLeft: '0.5rem'}} styles={{body: {padding: 8}}}>
                    <div className='tocTitle'>目录</div>
                    <Tree
                        expandedKeys={expandedKeys}
                        showLine={true}
                        treeData={tocTree as any}
                        switcherIcon={<DownOutlined/>}
                        onSelect={onSelect}
                        onExpand={onExpand}
                    />
                </Card>
            </Affix>
        </Card>
    );
}

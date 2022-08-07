import {LogHelper, PropertiesHelper} from "./UtilContainer";

export interface AntdTreeNodeInfo {
    index: number | null;
    title: string;
    nodeName: string;
    level: number | null;
    parentNodeIndex: number | null;
    children: Array<AntdTreeNodeInfo>;
}

export interface ErrorCallbackFunction {
    (msg: string | null): void;
}

export interface CreateTocTreeInputParam {
    currentTOCArray: Array<AntdTreeNodeInfo>,
    allTocNodeMap: Map<number, AntdTreeNodeInfo>,
    errorCallback: ErrorCallbackFunction | null;
}

export class MdHelper {
    // 根据目录数组生成目录树
    static getTocTree(inputParam: CreateTocTreeInputParam) {
        let tocTree: AntdTreeNodeInfo[] = [];
        let rootNodeLevel: number | null = 1;
        // 初始化上一个节点对象
        let prevNode: AntdTreeNodeInfo | null = null;

        inputParam.currentTOCArray.forEach((item, index) => {
            // 创建当前节点对象
            let currentNodeName = item.nodeName;
            item.level = MdHelper.getTitleLevel(currentNodeName, inputParam.errorCallback);
            item.children = [];

            // 首个节点
            if (index == 0) {
                // 首个节点默认为根节点，初始其父节点为空
                item.parentNodeIndex = null;
                rootNodeLevel = item.level;
                prevNode = item;
                tocTree.push(item);
                // means continue
                return;
            }

            // 非首个节点
            if (item.level == rootNodeLevel) {
                // 当前节点为根节点，初始其父节点为空
                item.parentNodeIndex = null;
                prevNode = item;
                tocTree.push(item);
            } else if (item.level! > rootNodeLevel!) {
                // @ts-ignore 当前节点是子节点
                if (item.level == prevNode.level) {
                    // @ts-ignore 当前节点是上一个节点的兄弟节点，分支同辈扩散
                    let parentOfPrevNode = inputParam.allTocNodeMap.get(prevNode.parentNodeIndex);
                    // @ts-ignore
                    item.parentNodeIndex = parentOfPrevNode.index;
                    // @ts-ignore
                    parentOfPrevNode.children.push(item);
                    prevNode = item;
                    // @ts-ignore
                } else if (item.level > prevNode.level) {
                    // @ts-ignore 当前节点是上一个节点的子节点，分支继续深入
                    item.parentNodeIndex = prevNode.index;
                    // @ts-ignore
                    prevNode.children.push(item);
                    prevNode = item;
                } else {
                    // 当前节点是上一个节点的长辈节点，分支按长辈扩散
                    let prevNodeSeniorNode = MdHelper.getNodeIndexByLevel({
                        startNode: prevNode!,
                        targetLevel: item.level!,
                        allTocNodeMap: inputParam.allTocNodeMap
                    });

                    if (prevNodeSeniorNode != null) {
                        let parentOfPrevNodeSeniorNode = inputParam.allTocNodeMap.get(prevNodeSeniorNode.parentNodeIndex!)!;
                        item.parentNodeIndex = parentOfPrevNodeSeniorNode.index;
                        parentOfPrevNodeSeniorNode.children.push(item);
                        prevNode = item;
                    } else {
                        MdHelper.defaultCallErrorCallback(inputParam.errorCallback, "非标准的目录关系，创建目录失败-2");
                    }
                }
            } else {
                MdHelper.defaultCallErrorCallback(inputParam.errorCallback, "非标准的目录关系，创建目录失败-1");
            }
        });

        return tocTree;
    }

    // 从起始节点向根节点遍历，寻找首个符合目标级别的节点 index
    static getNodeIndexByLevel({
                                   startNode,
                                   targetLevel,
                                   allTocNodeMap
                               }: { startNode: AntdTreeNodeInfo, targetLevel: number, allTocNodeMap: Map<number, AntdTreeNodeInfo> }): AntdTreeNodeInfo | null {
        let parentNodeId = startNode.parentNodeIndex;
        if (parentNodeId == null) {
            // 未找到结果
            return null;
        } else {
            let parentNodeIndex: AntdTreeNodeInfo = allTocNodeMap.get(parentNodeId)!;

            if (parentNodeIndex.level == targetLevel) {
                return parentNodeIndex;
            } else {
                return MdHelper.getNodeIndexByLevel({
                    startNode: parentNodeIndex,
                    targetLevel: targetLevel,
                    allTocNodeMap: allTocNodeMap
                });
            }
        }
    }

    static getTitleLevel(nodeName: string, errorCallback: ErrorCallbackFunction | null) {
        switch (nodeName) {
            case "H1":
                return 1;
            case "H2":
                return 2;
            case "H3":
                return 3;
            case "H4":
                return 4;
            case "H5":
                return 5;
            default:
                MdHelper.defaultCallErrorCallback(errorCallback, "仅支持五级目录");
                return null;
        }
    }

    static defaultCallErrorCallback(errorCallback: ErrorCallbackFunction | null, msg: string | null) {
        if (errorCallback != null) {
            if (PropertiesHelper.isStringNotEmpty(msg)) {
                errorCallback(msg);
            } else {
                errorCallback(null);
            }
        } else {
            LogHelper.error({className: "MdHelper", msg: "Fail to call errorCallback of getTOCTree(). " + msg});
        }
    }
}

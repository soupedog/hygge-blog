import {LogHelper, PropertiesHelper} from "../../../util/UtilContainer";

export interface AntdTreeNodeInfo {
    // 不可重复的唯一标识
    key?: string;
    // 标题
    title: string;
    children: Array<AntdTreeNodeInfo>;
}

export interface TreeNodeInfo extends AntdTreeNodeInfo {
    index: number;
    id: string;
    dataLine: string;
    // html 标签名称
    nodeName: string;
    // 根据 nodeName 一对一唯一对应而来的级别(值越小位级越高)
    level: number | undefined;
    parentNodeIndex: number | undefined;
}

export interface ErrorCallbackFunction {
    (msg?: string): void;
}

export interface CreateTocTreeInputParam {
    currentTOCArray: Array<TreeNodeInfo>,
    allTocNodeMap: Map<number, TreeNodeInfo>,
    errorCallback?: ErrorCallbackFunction;
}

export class MdHelper {
    // 根据目录数组生成目录树
    static initTitleTree(inputParam: CreateTocTreeInputParam) {
        let tocTree = new Array<TreeNodeInfo>();
        let rootNodeLevel: number = 1;
        // 初始化上一个节点对象
        let prevNode: TreeNodeInfo | undefined = undefined;

        inputParam.currentTOCArray.forEach((item, index) => {
            // 完整初始化当前节点对象各个属性
            let currentNodeName = item.nodeName;
            item.key = item.index + "_" + item.nodeName + "_" + item.dataLine;
            item.level = MdHelper.getTitleLevel(currentNodeName, inputParam.errorCallback)!;

            // 首个节点
            if (item.index == 0) {
                // 首个节点默认为根节点，初始其父节点为空
                item.parentNodeIndex = undefined;
                rootNodeLevel = item.level;

                tocTree.push(item);
                // 处理完毕，为下一轮初始化上一个节点
                prevNode = item;
                // means continue
                return;
            }

            // 非首个节点
            if (item.level == rootNodeLevel) {
                // 当前节点为根节点，初始其父节点为空
                item.parentNodeIndex = undefined;

                tocTree.push(item);
                // 处理完毕，为下一轮初始化上一个节点
                prevNode = item;
            } else if (item.level > rootNodeLevel) {
                // 当前节点是子节点
                if (item.level == prevNode!.level) {
                    // 当前节点是上一个节点的兄弟节点，分支同辈扩散
                    let parentOfPrevNode = inputParam.allTocNodeMap.get(prevNode!.parentNodeIndex!)!;
                    item.parentNodeIndex = parentOfPrevNode.index;

                    parentOfPrevNode.children.push(item);
                    // 处理完毕，为下一轮初始化上一个节点
                    prevNode = item;
                } else if (item.level > prevNode!.level!) {
                    // 当前节点是上一个节点的子节点，分支继续深入
                    item.parentNodeIndex = prevNode!.index;

                    prevNode!.children.push(item);
                    // 处理完毕，为下一轮初始化上一个节点
                    prevNode = item;
                } else {
                    // 当前节点是上一个节点的长辈节点，分支按长辈扩散
                    let prevNodeSeniorNode = MdHelper.getNodeIndexByLevel({
                        startNode: prevNode!,
                        targetLevel: item.level,
                        allTocNodeMap: inputParam.allTocNodeMap
                    });

                    if (prevNodeSeniorNode != undefined) {
                        let parentOfPrevNodeSeniorNode = inputParam.allTocNodeMap.get(prevNodeSeniorNode.parentNodeIndex!)!;
                        item.parentNodeIndex = parentOfPrevNodeSeniorNode.index;

                        parentOfPrevNodeSeniorNode.children.push(item);
                        // 处理完毕，为下一轮初始化上一个节点
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
                               }: {
        startNode: TreeNodeInfo,
        targetLevel: number,
        allTocNodeMap: Map<number, TreeNodeInfo>
    }): TreeNodeInfo | undefined {
        let parentNodeId = startNode.parentNodeIndex;
        if (parentNodeId == undefined) {
            // 未找到结果
            return undefined;
        } else {
            let parentNodeIndex: TreeNodeInfo = allTocNodeMap.get(parentNodeId)!;

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

    static getTitleLevel(nodeName: string, errorCallback?: ErrorCallbackFunction) {
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
            case "H6":
                return 6;
            default:
                MdHelper.defaultCallErrorCallback(errorCallback, "仅支持六级目录");
                return undefined;
        }
    }

    static defaultCallErrorCallback(errorCallback?: ErrorCallbackFunction, msg?: string) {
        if (errorCallback != undefined) {
            if (PropertiesHelper.isStringNotEmpty(msg)) {
                errorCallback(msg);
            } else {
                errorCallback();
            }
        } else {
            LogHelper.error({className: "MdHelper", msg: "Fail to call errorCallback of getTOCTree(). " + msg});
        }
    }
}

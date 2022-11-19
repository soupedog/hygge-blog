import * as React from "react"
import {LogHelper, PropertiesHelper, UrlHelper} from '../utils/UtilContainer';
import {
    AllOverviewInfo,
    ArticleDto,
    ArticleService,
    FileService,
    HomePageService,
    HyggeResponse,
    UserService
} from "../rest/ApiClient";
import {ReactRouter, withRouter} from "../utils/ReactRouterHelper";
import zhCN from "antd/lib/locale/zh_CN";
import {
    Button,
    ConfigProvider,
    Form,
    FormInstance,
    Input,
    Layout,
    message,
    Radio,
    Select,
    SelectProps,
    Space,
    TreeSelect
} from "antd";
import {HyggeFooter} from "./component/HyggeFooter";

import "./../../css/editArticle.less"
import "vditor/dist/index.css"
import Vditor from "vditor";

const {Header, Content} = Layout;

// 描述该组件 props 数据类型
export interface EditArticleContainerProps {
    router: ReactRouter;
}

// 描述该组件 states 数据类型
export interface EditArticleContainerState {
    aid?: string;
    backgroundMusicType?: string;
    currentArticle?: ArticleDto;
    categoryTreeData?: any[];
    backgroundImageData?: SelectProps['options'];
    mdController?: Vditor;
}

class EditArticleContainer extends React.Component<EditArticleContainerProps, EditArticleContainerState> {
    formRef = React.createRef<FormInstance>();

    constructor(props: EditArticleContainerProps) {
        super(props);
        this.state = {
            currentArticle: undefined,
            backgroundMusicType: "NONE",
            categoryTreeData: [],
            backgroundImageData: []
        };

        let currentUser = UserService.getCurrentUser();
        if (currentUser == null) {
            message.warn("未登录用户无权访问，2 秒内自动为您跳转至主页")
            UrlHelper.openNewPage({inNewTab: false, delayTime: 2000})
        }
        LogHelper.info({className: "EditArticleContainer", msg: "初始化成功"});
    }

    render() {
        let _react = this;
        return (
            <ConfigProvider locale={zhCN}>
                <Layout className="layout">
                    <Header>
                        <div className="page-title floatToLeft" style={{width: 200}}>我的小宅子---文章编辑</div>
                    </Header>
                    <Content className="mainContent"
                             style={{padding: '0 50px', minHeight: window.innerHeight - 182}}>
                        <div id="preview" style={{marginTop: "10px", marginBottom: "20px"}}/>
                        <Form
                            ref={this.formRef}
                            name="hygge_login"
                            style={{
                                maxWidth: "80%",
                                margin: "40px auto 0 auto"
                            }}
                            onFinish={(value) => {
                                value.content = _react.state.mdController?.getValue();
                                if (value.action == "update") {
                                    if (PropertiesHelper.isStringNotEmpty(value.aid)) {
                                        ArticleService.updateArticle(value.aid, value, () => {
                                                message.success("修改文章成功");
                                            }
                                        );
                                    } else {
                                        message.warn("修改文章时 aid 不可为空");
                                    }
                                } else if (value.action == "add") {
                                    ArticleService.createArticle(value, (data) => {
                                            _react.updateForm(data!, _react);
                                            message.success("添加文章成功");
                                        }
                                    );
                                }
                                console.log(value);
                            }}
                        >
                            <Form.Item name={['aid']} label="文章编号" rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['cid']} label="文章类型" rules={[{required: true}]}>
                                <TreeSelect
                                    style={{width: '100%'}}
                                    dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
                                    treeData={this.state.categoryTreeData}
                                    placeholder="请选择文章类型"
                                    treeDefaultExpandAll
                                    onChange={() => {
                                    }}
                                />
                            </Form.Item>
                            <Form.Item name={['imageSrc']} label="文章主图"
                                       rules={[{required: true}]}>
                                <Select
                                    showSearch
                                    placeholder="请选择背景图片"
                                    optionFilterProp="children"
                                    options={this.state.backgroundImageData}
                                >
                                </Select>
                            </Form.Item>
                            <Form.Item name={['title']} label="标题" rules={[{required: true}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['summary']} label="摘要" rules={[{required: true}]}>
                                <Input.TextArea rows={4}/>
                            </Form.Item>
                            <Form.Item name={['articleState']} label="文章状态"
                                       rules={[{required: true}]}
                                       initialValue={"ACTIVE"}
                            >
                                <Radio.Group>
                                    <Radio value={"ACTIVE"}>正式发布</Radio>
                                    <Radio value={"DRAFT"}>草稿</Radio>
                                    <Radio value={"PRIVATE"}>私人模式</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item name={['configuration', 'backgroundMusicType']} label="背景音乐类型"
                                       rules={[{required: true}]}
                                       initialValue={"NONE"}>
                                <Radio.Group onChange={(event) => {
                                    this.setState({backgroundMusicType: event.target.value});
                                }}>
                                    <Radio value={"NONE"}>无音乐</Radio>
                                    <Radio value={"DEFAULT"}>外链背景音乐</Radio>
                                    <Radio value={"WANG_YI_YUN"}>网易云音乐</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item name={['configuration', 'mediaPlayType']} label="背景音乐播放模式"
                                       rules={[{required: this.state.backgroundMusicType == "DEFAULT" || this.state.backgroundMusicType == "WANG_YI_YUN"}]}
                                       initialValue={"SUGGEST_AUTO_PLAY"}>
                                <Radio.Group>
                                    <Radio value={"FORCE_AUTO_PLAY"}>强制自动播放</Radio>
                                    <Radio value={"FORCE_NOT_AUTO_PLAY"}>强制非自动播放</Radio>
                                    <Radio value={"SUGGEST_AUTO_PLAY"}>建议自动播放(优先客户端本地配置)</Radio>
                                    <Radio value={"SUGGEST_NOT_AUTO_PLAY"}>建议非自动播放(优先客户端本地配置)</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item name={['configuration', 'src']} label="背景音乐源链接"
                                       rules={[{required: this.state.backgroundMusicType == "DEFAULT" || this.state.backgroundMusicType == "WANG_YI_YUN"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'coverSrc']} label="背景音乐封面图片源链接"
                                       rules={[{required: this.state.backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'name']} label="背景音乐名称"
                                       rules={[{required: this.state.backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'artist']} label="背景音乐作者"
                                       rules={[{required: this.state.backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'lrc']} label="背景音乐歌词"
                                       rules={[{required: false}]}>
                                <Input.TextArea rows={4}/>
                            </Form.Item>
                            <Form.Item style={{display: "none"}} name={['action']} label="操作类型"
                                       rules={[{required: true}]}
                                       initialValue={"add"}>
                                <Radio.Group onChange={(event) => {
                                    this.setState({backgroundMusicType: event.target.value});
                                }}>
                                    <Radio value={"add"}>添加文章</Radio>
                                    <Radio value={"update"}>修改文章</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item>
                                <Space size={"large"} align={"end"}>
                                    <Button type="primary" htmlType="submit" onClick={() => {
                                        _react.formRef.current?.setFieldsValue({
                                            action: 'add'
                                        });
                                    }}>
                                        添加文章
                                    </Button>
                                    <Button type="primary" htmlType="submit" danger onClick={() => {
                                        _react.formRef.current?.setFieldsValue({
                                            action: 'update'
                                        });
                                    }}>
                                        修改文章
                                    </Button>
                                    <Button type="ghost" htmlType="button" onClick={() => {

                                        if (_react.props.router.params.aid != null) {
                                            ArticleService.findArticleByAid(_react.props.router.params.aid, (data) => {
                                                if (data != null) {
                                                    _react.updateRootStatus({
                                                        currentArticle: data.main
                                                    })

                                                    if (_react.state.mdController != null) {
                                                        // 更新 MD 编辑器
                                                        _react.state.mdController?.setValue(data.main?.content!);
                                                    }
                                                    _react.updateForm(data, _react);
                                                    message.success("文章信息拉取成功");
                                                }
                                            });
                                        }
                                    }}>
                                        查询文章
                                    </Button>
                                    <Button type="ghost" htmlType="button" onClick={() => {
                                        // 里面自动会刷新 文件信息
                                        _react.updateCategory();
                                    }}>
                                        更新配置信息
                                    </Button>
                                </Space>
                            </Form.Item>
                        </Form>
                    </Content>
                    <HyggeFooter/>
                </Layout>
            </ConfigProvider>
        );
    }

    componentDidMount() {
        document.title = "文章编辑器";
        let _react = this;
        let vditor = new Vditor('preview', {
            cdn: UrlHelper.getVditorCdn(),
            height: 600,
            mode: "sv",
            value: "",
            toolbarConfig: {
                pin: true,
            },
            preview: {
                hljs: {
                    style: "native",
                    lineNumber: true
                },
            },
            cache: {
                enable: false,
            },
            upload: {
                url: UrlHelper.getBaseApiUrl() + "/main/file?type=ARTICLE",
                headers: UserService.getHeader({}),
                // 200 MB
                max: 209715200,
                fieldName: "files",
                multiple: true,
                success: (editor: HTMLPreElement, msg: string) => {
                    let response = JSON.parse(msg);
                    if (response.code == 200) {
                        response.main?.map((fileName: string) => {
                            // @ts-ignore
                            _react.state.mdController?.insertValue("[" + fileName.name + "](" + UrlHelper.getBaseStaticSourceUrl() + fileName.src + ")\r\n");
                        });
                    } else {
                        message.warning(response.msg);
                    }
                },
                error: (msg: string) => {
                    message.error(msg);
                    LogHelper.error({
                        className: "EditArticleContainer",
                        msg: "Fail to upload file",
                        isJson: false
                    });
                }
            },
            after() {
                // 更新 MD 控制器
                _react.setState({mdController: vditor});
                ArticleService.findArticleByAid(_react.props.router.params.aid, (data) => {
                    if (data != null) {
                        _react.updateRootStatus({
                            currentArticle: data.main
                        })
                        if (_react.state.mdController != null && data.main != null) {
                            // 更新 MD 编辑器
                            _react.state.mdController?.setValue(data.main.content);

                            _react.updateForm(data, _react);
                        }
                        message.info("文章信息已尝试拉取");
                    }
                });
            },
        });

        this.updateCategory();
    }

    updateForm(data: HyggeResponse<ArticleDto>, _react: this) {
        let article: ArticleDto = data.main!;
        _react.formRef.current?.setFieldsValue({
            aid: article.aid,
            configuration: {
                backgroundMusicType: article.configuration?.backgroundMusicType,
                mediaPlayType: article.configuration?.mediaPlayType,
                src: article.configuration?.src,
                coverSrc: article.configuration?.coverSrc,
                name: article.configuration?.name,
                artist: article.configuration?.artist,
                lrc: article.configuration?.lrc
            },
            cid: article.cid,
            title: article.title,
            imageSrc: article.imageSrc,
            summary: article.summary,
            articleState: article.articleState
        });
    }

    updateCategory() {
        let _react = this;
        HomePageService.fetch((data) => {
            let response: AllOverviewInfo | undefined = data?.main
            let categoryContainer: any = [];

            response?.topicOverviewInfoList.forEach((topicOverviewInfo) => {

                let childrenList: any = [];

                topicOverviewInfo.categoryListInfo.forEach((item) => {
                    if (item.categoryType == "DEFAULT") {
                        childrenList.push({
                            title: item.categoryName + " --- " + item.articleCount,
                            value: item.cid,
                        });
                    }
                });

                let parent = {
                    title: topicOverviewInfo.topicInfo.topicName + " --- " + topicOverviewInfo.totalCount,
                    value: topicOverviewInfo.topicInfo.tid,
                    disabled: true,
                    children: childrenList
                };

                categoryContainer.push(parent)
            });

            _react.updateRootStatus({categoryTreeData: categoryContainer});

            message.info("类别信息已尝试拉取");

            _react.updateFileInfo();
        })
    }

    updateFileInfo() {
        let _react = this;
        let type = ["ARTICLE_COVER"]

        let container: SelectProps['options'] = [];

        FileService.findFileInfo(type, (data) => {
            data?.main?.forEach((item) => {
                container?.push({
                    label: item.name + " --- " + item.fileSize + " mb",
                    value: UrlHelper.getBaseStaticSourceUrl() + item.src,
                });
            });

            _react.updateRootStatus({backgroundImageData: container});

            message.info("封面已尝试拉取");
        });
    }

    updateRootStatus(deltaInfo: EditArticleContainerState) {
        this.setState(deltaInfo);
    }
}

export default withRouter(EditArticleContainer)
import React, {useEffect, useState} from 'react';
import {Button, Form, Input, Layout, message, Radio, Select, SelectProps, Space, TreeSelect} from "antd";
import {ArticleEditorContext} from '../../page/ArticleEditor';
import {Content} from "antd/es/layout/layout";
import {useParams} from "react-router-dom";
import {
    AllOverviewInfo,
    ArticleDto,
    ArticleService,
    FileService,
    HomePageService,
    HyggeResponse
} from "../../rest/ApiClient";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {PropertiesHelper, UrlHelper} from "../../util/UtilContainer";


function ArticleEditorForm({updateContent}: { updateContent: Function }) {
    const [articleForm] = Form.useForm();
    const {aid} = useParams();

    const [backgroundMusicType, updateBackgroundMusicType] = useState(undefined);
    const [categoryInfoList, updateCategoryInfoList] = useState([]);
    const [backgroundImageInfoList, updateBackgroundImageInfoList] = useState([]);

    useEffect(() => {
        refreshCategoryInfoList(updateCategoryInfoList);
        refreshImageInfoList(updateBackgroundImageInfoList);

        if (aid) {
            ArticleService.findArticleByAid(aid, (data) => {
                if (data?.main == null) {
                    message.warning("目标文章不存在", 2000);
                } else {
                    refreshFormInfo(updateContent, data, articleForm);
                }
            })
        }

        // 依赖静态值表示仅初始化时调用一次
    }, []);


    return (
        <ArticleEditorContext.Consumer>
            {({content}) => (
                <Layout>
                    <Content>
                        <Form
                            form={articleForm}
                            style={{
                                maxWidth: "80%",
                                margin: "40px auto 0 auto"
                            }}
                            onFinish={(value) => {
                                value.content = content;
                                if (value.action == "update") {
                                    if (PropertiesHelper.isStringNotEmpty(value.aid)) {
                                        ArticleService.updateArticle(value.aid, value, () => {
                                                message.success("修改文章成功");
                                            }
                                        );
                                    } else {
                                        message.warning("修改文章时 aid 不可为空");
                                    }
                                } else if (value.action == "add") {
                                    ArticleService.createArticle(value, (data) => {
                                            refreshFormInfo(updateContent, data!, articleForm);
                                            message.success("添加文章成功");
                                        }
                                    );
                                }
                            }}
                        >
                            <Form.Item name={['aid']} label="文章编号" rules={[{required: false}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['cid']} label="文章类型" rules={[{required: true}]}>
                                <TreeSelect
                                    style={{width: '100%'}}
                                    styles={{
                                        popup: {root: {maxHeight: 400, overflow: 'auto'}},
                                    }}
                                    treeData={categoryInfoList}
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
                                    options={backgroundImageInfoList}
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
                                    updateBackgroundMusicType(event.target.value);
                                }}>
                                    <Radio value={"NONE"}>无音乐</Radio>
                                    <Radio value={"DEFAULT"}>外链背景音乐</Radio>
                                    <Radio value={"WANG_YI_YUN"}>网易云音乐</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item name={['configuration', 'mediaPlayType']} label="背景音乐播放模式"
                                       rules={[{required: backgroundMusicType == "DEFAULT" || backgroundMusicType == "WANG_YI_YUN"}]}
                                       initialValue={"SUGGEST_AUTO_PLAY"}>
                                <Radio.Group>
                                    <Radio value={"FORCE_AUTO_PLAY"}>强制自动播放</Radio>
                                    <Radio value={"FORCE_NOT_AUTO_PLAY"}>强制非自动播放</Radio>
                                    <Radio value={"SUGGEST_AUTO_PLAY"}>建议自动播放(优先客户端本地配置)</Radio>
                                    <Radio value={"SUGGEST_NOT_AUTO_PLAY"}>建议非自动播放(优先客户端本地配置)</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item name={['configuration', 'src']} label="背景音乐源链接"
                                       rules={[{required: backgroundMusicType == "DEFAULT" || backgroundMusicType == "WANG_YI_YUN"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'coverSrc']} label="背景音乐封面图片源链接"
                                       rules={[{required: backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'name']} label="背景音乐名称"
                                       rules={[{required: backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'artist']} label="背景音乐作者"
                                       rules={[{required: backgroundMusicType == "DEFAULT"}]}>
                                <Input/>
                            </Form.Item>
                            <Form.Item name={['configuration', 'lrc']} label="背景音乐歌词"
                                       rules={[{required: false}]}>
                                <Input.TextArea rows={4}/>
                            </Form.Item>
                            <Form.Item style={{display: "none"}} name={['action']} label="操作类型"
                                       rules={[{required: true}]}
                                       initialValue={"add"}>
                                <Radio.Group>
                                    <Radio value={"add"}>添加文章</Radio>
                                    <Radio value={"update"}>修改文章</Radio>
                                </Radio.Group>
                            </Form.Item>
                            <Form.Item>
                                <Space size={"large"} align={"end"}>
                                    <Button type="primary" htmlType="submit" onClick={() => {
                                        articleForm.setFieldsValue({
                                            action: 'add'
                                        });
                                    }}>
                                        添加文章
                                    </Button>
                                    <Button type="primary" htmlType="submit" danger onClick={() => {
                                        articleForm.setFieldsValue({
                                            action: 'update'
                                        });
                                    }}>
                                        修改文章
                                    </Button>
                                    <Button type="dashed" htmlType="button" onClick={() => {
                                        if (aid) {
                                            ArticleService.findArticleByAid(aid, (data) => {
                                                if (data?.main == null) {
                                                    message.info("目标文章不存在", 2000);
                                                    UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
                                                } else {
                                                    refreshFormInfo(updateContent, data, articleForm);
                                                }
                                            })
                                        } else {
                                            message.warning("未指定查询目标", 2000);
                                        }
                                    }}>
                                        查询文章
                                    </Button>
                                    <Button type="dashed" htmlType="button" onClick={() => {
                                        refreshCategoryInfoList(updateCategoryInfoList);
                                        refreshImageInfoList(updateBackgroundImageInfoList);
                                    }}>
                                        更新配置信息
                                    </Button>
                                </Space>
                            </Form.Item>
                        </Form>
                    </Content>
                </Layout>
            )}
        </ArticleEditorContext.Consumer>
    );

    function refreshCategoryInfoList(updateCategoryInfoList: Function) {
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

            updateCategoryInfoList(categoryContainer);

            message.info("类别信息已尝试拉取");
        })
    }

    function refreshImageInfoList(updateBackgroundImageInfoList: Function) {
        let type = ["ARTICLE_COVER"]

        let container: SelectProps['options'] = [];

        FileService.findFileInfo(type, (data) => {
            data?.main?.forEach((item) => {
                container?.push({
                    label: item.name + " --- " + item.fileSize + " mb",
                    value: UrlHelper.getBaseStaticSourceUrl() + item.src,
                });
            });
            updateBackgroundImageInfoList(container);
            message.info("封面已尝试拉取");
        });
    }

    function refreshFormInfo(updateContent: Function, data: HyggeResponse<ArticleDto>, form: FormInstance<any>) {
        let article: ArticleDto = data.main!;

        updateContent(article.content);

        form.setFieldsValue({
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
}

export default ArticleEditorForm;
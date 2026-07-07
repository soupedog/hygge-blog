import {useContext, useEffect, useState} from 'react';
import {Button, Col, Flex, Form, Input, InputNumber, message, Radio, Row, Select, Space} from 'antd';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {PostEditorContext} from '../context/PostEditorContext.tsx';
import clsx from 'clsx';
import type {PostAddUpdateInput} from '../../util/ApiClient.ts';

export default function PostEditorForm() {
    const {
        topicInfoList,
        fileOptions,
        pid, setPid,
        postForm,
        formMode, setFormMode,
        editorContent, setEditorContent,
        backgroundMusicType, setBackgroundMusicType,
        setQueryModalOpen,
        onPidChange,
        addPost, modifyPost,
    } = useContext(PostEditorContext);


    const [categoryOpinionList, setCategoryOpinionList] = useState<Array<any>>([]);

    const isAddMode = formMode == 'add';
    const isQueryMode = formMode == 'query';
    const isUpdateMode = formMode == 'update';

    const isNoBGM = backgroundMusicType == 'NONE';
    const isDefaultBGM = backgroundMusicType == 'DEFAULT';
    const isWyyBGM = backgroundMusicType == 'WANG_YI_YUN';

    useEffect(() => {
        const nextCategoryOpinionList = topicInfoList.map(item => {
            const options: any[] = [];

            item.categoryListInfo.map(category => {
                if (category.categoryType != 'PATH') {
                    // path 类型不添加至可选列表
                    options.push({
                        label: category.categoryName,
                        value: category.cid
                    });
                }
            });

            return {
                label: item.topicInfo.topicName,
                title: item.topicInfo.topicName,
                options: options,
            };
        });

        setCategoryOpinionList(nextCategoryOpinionList);

    }, [JSON.stringify(topicInfoList)]);

    return (
        <Form<PostAddUpdateInput>
            name='hygge_post_editor'
            // 不再记录历史信息
            autoComplete={'off'}
            form={postForm}
            style={{padding: '4rem'}}

            onFinish={(value) => {
                // @ts-ignore
                value.title = PropertiesHelper.stringOfNullable({target: value.title, defaultValue: null});
                // @ts-ignore
                value.summary = PropertiesHelper.stringOfNullable({target: value.summary, defaultValue: null});
                // @ts-ignore
                value.content = PropertiesHelper.stringOfNullable({target: editorContent, defaultValue: null});

                if (isNoBGM) {
                    // @ts-ignore
                    value.configuration = {backgroundMusicType: 'NONE'};
                }

                if (isWyyBGM) {
                    // @ts-ignore
                    value.configuration = {
                        backgroundMusicType: 'NONE',
                        // @ts-ignore
                        mediaPlayType: PropertiesHelper.stringOfNullable({target: value.configuration.mediaPlayType, defaultValue: 'SUGGEST_AUTO_PLAY'}),
                        src: PropertiesHelper.stringOfNullable({target: value.configuration.src, defaultValue: null})!
                    };
                }

                if (isDefaultBGM) {
                    // @ts-ignore
                    value.configuration.src = PropertiesHelper.stringOfNullable({target: value.configuration.src, defaultValue: null});
                    // @ts-ignore
                    value.configuration.coverSrc = PropertiesHelper.stringOfNullable({target: value.configuration.coverSrc, defaultValue: null});
                    // @ts-ignore
                    value.configuration.name = PropertiesHelper.stringOfNullable({target: value.configuration.name, defaultValue: null});
                    // @ts-ignore
                    value.configuration.artist = PropertiesHelper.stringOfNullable({target: value.configuration.artist, defaultValue: null});
                    // @ts-ignore
                    value.configuration.lrc = PropertiesHelper.stringOfNullable({target: value.configuration.lrc, defaultValue: null});
                }

                if (value.action == 'update') {
                    modifyPost(value);
                } else if (value.action == 'add') {
                    addPost(value);
                }
            }}
        >
            <Row gutter={'4rem'}>
                <Col offset={1} span={12}>
                    <Form.Item name={['aid']} label='博文编号' rules={[{required: isQueryMode || isUpdateMode}]}>
                        <Input value={pid} onChange={(event) => {
                            onPidChange(event.target.value);
                        }}/>
                    </Form.Item>
                </Col>
                <Col span={5}>
                    <Form.Item name={['orderGlobal']} label='全局排序' rules={[{required: false}]}>
                        <InputNumber
                            min={0}           // 最小值
                            max={999999999999999}         // 最大值
                            step={1}          // 步长
                            precision={0}     // 小数位数，0 表示整数
                            placeholder='排序值'
                        />
                    </Form.Item>
                </Col>
                <Col span={5}>
                    <Form.Item name={['orderCategory']} label='类别排序' rules={[{required: false}]}>
                        <InputNumber
                            min={0}           // 最小值
                            max={999999999999999}         // 最大值
                            step={1}          // 步长
                            precision={0}     // 小数位数，0 表示整数
                            placeholder='排序值'
                        />
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col offset={1} span={7}>
                    <Form.Item name={['cid']} label='文章类别'
                               rules={[{required: true}]}>
                        <Select
                            showSearch={{
                                optionFilterProp: 'label'
                            }}
                            placeholder='请选择类别'
                            options={categoryOpinionList}
                        />
                    </Form.Item>
                </Col>
                <Col span={7}>
                    <Form.Item name={['coverFileNo']} label='封面图'
                               rules={[{required: true}]}>
                        <Select
                            showSearch={{
                                optionFilterProp: 'label'
                            }}
                            placeholder='请选择封面'
                            options={fileOptions}
                        />
                    </Form.Item>
                </Col>
                <Col span={7}>
                    <Form.Item name={['articleState']} label='博文状态'
                               rules={[{required: true}]}
                               initialValue={'ACTIVE'}
                    >
                        <Radio.Group>
                            <Radio value={'DRAFT'}>草稿</Radio>
                            <Radio value={'PRIVATE'}>私有</Radio>
                            <Radio value={'ACTIVE'}>公开</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col offset={1} span={22}>
                    <Form.Item name={['title']} label='标题' rules={[{required: isAddMode || isUpdateMode}]}>
                        <Input/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col offset={1} span={22}>
                    <Form.Item name={['summary']} label='摘要' rules={[{required: false}]}>
                        <Input.TextArea rows={2}/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col offset={1} span={22}>
                    <Form.Item name={['configuration', 'backgroundMusicType']} label='背景音乐类型'
                               rules={[{required: true}]}
                               initialValue={'NONE'}>
                        <Radio.Group onChange={(event) => {
                            setBackgroundMusicType(event.target.value);
                        }}>
                            <Radio value={'NONE'}>无音乐</Radio>
                            <Radio value={'DEFAULT'}>外链背景音乐</Radio>
                            <Radio value={'WANG_YI_YUN'}>网易云音乐</Radio>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM})} name={['configuration', 'mediaPlayType']} label='背景音乐播放模式'
                               rules={[{required: !isNoBGM}]}
                               initialValue={'SUGGEST_AUTO_PLAY'}>
                        <Radio.Group>
                            <Radio value={'FORCE_AUTO_PLAY'}>强制自动播放</Radio>
                            <Radio value={'FORCE_NOT_AUTO_PLAY'}>强制非自动播放</Radio>
                            <Radio value={'SUGGEST_AUTO_PLAY'}>建议自动播放(优先客户端本地配置)</Radio>
                            <Radio value={'SUGGEST_NOT_AUTO_PLAY'}>建议非自动播放(优先客户端本地配置)</Radio>
                        </Radio.Group>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM})} name={['configuration', 'src']} label='背景音乐源链接'
                               rules={[{required: !isNoBGM}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM || isWyyBGM})} name={['configuration', 'coverSrc']} label='背景音乐封面图片源链接'
                               rules={[{required: isDefaultBGM}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM || isWyyBGM})} name={['configuration', 'name']} label='背景音乐名称'
                               rules={[{required: isDefaultBGM}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM || isWyyBGM})} name={['configuration', 'artist']} label='背景音乐作者'
                               rules={[{required: isDefaultBGM}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item className={clsx({'display-none': isNoBGM || isWyyBGM})} name={['configuration', 'lrc']} label='背景音乐歌词'
                               rules={[{required: false}]}>
                        <Input.TextArea rows={4}/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={'4rem'}>
                <Form.Item className={'display-none'} name={['action']} label='操作类型'
                           rules={[{required: true}]}
                           initialValue={'add'}>
                    <Radio.Group>
                        <Radio value={'add'}>添加博文</Radio>
                        <Radio value={'update'}>修改博文</Radio>
                        <Radio value={'query'}>查询博文</Radio>
                    </Radio.Group>
                </Form.Item>
                <Col offset={1} span={22}>
                    <Form.Item>
                        <Flex justify={'center'} style={{alignItems: 'center'}}>
                            <Space size={'large'} align={'end'}>
                                <Button type='primary' htmlType='button' onClick={() => {
                                    setFormMode('add');
                                    postForm.setFieldsValue({
                                        action: 'add'
                                    });
                                    postForm.submit();
                                }}>
                                    添加博文
                                </Button>
                                <Button type='primary' htmlType='button' danger onClick={() => {
                                    setFormMode('update');
                                    postForm.setFieldsValue({
                                        action: 'update'
                                    });
                                    postForm.submit();
                                }}>
                                    修改博文
                                </Button>

                                <Button type='dashed' color={'purple'} htmlType='submit' onClick={() => {
                                    // 表单提交绑定到该按钮，防止误触回车，即便触发也只是查询操作，没有数据变更
                                    setFormMode('query');
                                    postForm.setFieldsValue({
                                        action: 'query'
                                    });
                                    if (PropertiesHelper.isStringNotEmpty(pid)) {
                                        setQueryModalOpen(true);
                                    } else {
                                        message.warning('博文编号不可为空！')
                                    }
                                }}>
                                    查询博文
                                </Button>
                            </Space>
                        </Flex>
                    </Form.Item>
                </Col>
            </Row>
        </Form>
    );
}

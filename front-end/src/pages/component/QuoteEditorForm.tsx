import {useContext, useState} from 'react';
import {Button, Col, Flex, Form, Input, InputNumber, message, Radio, Row, Select, Space} from 'antd';
import PropertiesHelper from '../../util/PropertiesHelper.ts';
import {QuoteEditorContext} from '../context/QuoteEditorContext.tsx';

export default function QuoteEditorForm() {
    const {
        fileOptions,
        quoteForm,
        quoteId,
        onQuoteIdChange,
        setQueryModalOpen,
        addQuote,
        modifyQuote,
    } = useContext(QuoteEditorContext);

    const [formMode, setFormMode] = useState<'query' | 'add' | 'update'>(quoteId ? 'query' : 'add');

    const isAddMode = formMode == 'add';
    const isQueryMode = formMode == 'query';
    const isUpdateMode = formMode == 'update';

    return (
        <Form
            name='hygge_quote_editor'
            // 不再记录历史信息
            autoComplete={'off'}
            form={quoteForm}
            style={{padding: '4rem'}}

            onFinish={(value) => {
                value.coverFileNo = PropertiesHelper.stringOfNullable({target: value.coverFileNo, defaultValue: null});
                value.source = PropertiesHelper.stringOfNullable({target: value.source, defaultValue: null});
                value.portal = PropertiesHelper.stringOfNullable({target: value.portal, defaultValue: null});
                value.content = PropertiesHelper.stringOfNullable({target: value.content, defaultValue: null});
                value.remarks = PropertiesHelper.stringOfNullable({target: value.remarks, defaultValue: null});

                if (value.action == 'update') {
                    modifyQuote(value);
                } else if (value.action == 'add') {
                    addQuote(value);
                }
            }}
        >
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Col span={6}>
                    <Form.Item name={['quoteId']} label='句子编号' rules={[{required: isQueryMode || isUpdateMode}]}>
                        <Input value={quoteId} onChange={(event) => {
                            onQuoteIdChange(event.target.value);
                        }}/>
                    </Form.Item>
                </Col>
                <Col span={4}>
                    <Form.Item name={['orderVal']} label='排序' rules={[{required: false}]}>
                        <InputNumber
                            min={0}           // 最小值
                            max={999999999999999}         // 最大值
                            step={1}          // 步长
                            precision={0}     // 小数位数，0 表示整数
                            placeholder='排序值'
                        />
                    </Form.Item>
                </Col>
                <Col span={6}>
                    <Form.Item name={['coverFileNo']} label='句子封面图'
                               initialValue={'无图片'}
                               rules={[{required: false}]}>
                        <Select
                            showSearch={{
                                optionFilterProp: 'label'
                            }}
                            placeholder='请选择封面'
                            options={[
                                {
                                    value: '',
                                    label: '无图片'
                                },
                                ...fileOptions
                            ]}
                        />
                    </Form.Item>
                </Col>
                <Col span={6}>
                    <Form.Item name={['quoteState']} label='句子状态'
                               rules={[{required: true}]}
                               initialValue={'ACTIVE'}
                    >
                        <Radio.Group>
                            <Radio value={'ACTIVE'}>启用</Radio>
                            <Radio value={'INACTIVE'}>禁用</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Col span={22}>
                    <Form.Item name={['portal']} label='传送门' rules={[{required: false}]}>
                        <Input.TextArea rows={1}/>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Col span={22}>
                    <Form.Item name={['source']} label='出处' rules={[{required: false}]}>
                        <Input.TextArea rows={1}/>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Col span={22}>
                    <Form.Item name={['content']} label='内容' rules={[{required: isAddMode}]}>
                        <Input.TextArea rows={4}/>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Col span={22}>
                    <Form.Item name={['remarks']} label='备注' rules={[{required: false}]}>
                        <Input.TextArea rows={2}/>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
            <Row gutter={'4rem'}>
                <Col span={1}></Col>
                <Form.Item className={'display-none'} name={['action']} label='操作类型'
                           rules={[{required: true}]}
                           initialValue={'add'}>
                    <Radio.Group>
                        <Radio value={'add'}>添加句子</Radio>
                        <Radio value={'update'}>修改句子</Radio>
                        <Radio value={'query'}>查询句子</Radio>
                    </Radio.Group>
                </Form.Item>
                <Col span={22}>
                    <Form.Item>
                        <Flex justify={'center'} style={{alignItems: 'center'}}>
                            <Space size={'large'} align={'end'}>
                                <Button type='primary' htmlType='button' onClick={() => {
                                    quoteForm.setFieldsValue({
                                        action: 'add'
                                    });
                                    setFormMode('add');
                                    quoteForm.submit();
                                }}>
                                    添加句子
                                </Button>
                                <Button type='primary' htmlType='button' danger onClick={() => {
                                    quoteForm.setFieldsValue({
                                        action: 'update'
                                    });
                                    setFormMode('update');
                                    quoteForm.submit();
                                }}>
                                    修改句子
                                </Button>
                                <Button type='dashed' color={'purple'} htmlType='submit' onClick={() => {
                                    setFormMode('query');
                                    if (PropertiesHelper.isStringNotEmpty(quoteId)) {
                                        quoteForm.setFieldsValue({
                                            action: 'query'
                                        });
                                        setQueryModalOpen(true);
                                    } else {
                                        message.warning('句子编号不可为空！')
                                    }
                                }}>
                                    查询句子
                                </Button>
                            </Space>
                        </Flex>
                    </Form.Item>
                </Col>
                <Col span={1}></Col>
            </Row>
        </Form>
    );
}

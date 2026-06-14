import {appConfiguration} from '../../configuration/app.configuration.ts';
import {Footer} from 'antd/es/layout/layout';
import {Typography} from 'antd';

const {Paragraph, Text} = Typography;

export default function AppFooter() {
    const icpInfo: string = appConfiguration.icpInfo;
    const codePrefix: string = appConfiguration.codePrefix;
    const code: string = appConfiguration.code;
    // 获取当前年份
    const currentYear = new Date().getFullYear();

    return (
        <Footer style={{textAlign: 'center'}}>
            <Paragraph strong={true}>
                <Text>
                    © 2019-{currentYear} 我的小宅子 Power by Xavier
                </Text>
                <Text style={{margin: '0 0.5rem', color: '#e8e8e8'}}>
                    |
                </Text>
                <Text>
                    Made with
                </Text>
                <Text>
                    <a className='dependentLink' target='_blank'
                       href='https://www.typescriptlang.org/'> TypeScript</a>
                </Text>
                <Text>
                    &nbsp;&amp;&nbsp;<a className='dependentLink' target='_blank'
                                        href='https://zh-hans.react.dev/'> React</a>
                </Text>
                <Text>
                    &nbsp;&amp;&nbsp;<a className='dependentLink' target='_blank'
                                        href='https://ant-design.antgroup.com/index-cn'>Ant Design</a>
                </Text>
            </Paragraph>

            <Paragraph strong={true}>
                <a className='dependentLink'
                   href={`http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=${code}`}
                   target='_blank'
                   rel='noopener noreferrer'
                >
                    🛡️ {codePrefix}{code}
                </a>

                <Text style={{margin: '0 0.5rem', color: '#e8e8e8'}}>
                    |
                </Text>

                <a className='dependentLink'
                   href='https://beian.miit.gov.cn/'
                   target='_blank'
                   rel='noopener noreferrer'
                >
                    {icpInfo}
                </a>
            </Paragraph>
        </Footer>
    );
}

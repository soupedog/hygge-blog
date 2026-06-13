import {Footer} from "antd/lib/layout/layout";

export default function AppFooter() {
    return (
        <Footer style={{textAlign: 'center'}}>Ant Design ©{new Date().getFullYear()} Created by Ant UED</Footer>
    );
}

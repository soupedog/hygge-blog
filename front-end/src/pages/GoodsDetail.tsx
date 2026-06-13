import {useNavigate, useParams} from "react-router-dom";
import {Button} from "antd";

export default function GoodsDetail() {
    const {gid} = useParams();
    const navigate = useNavigate();

    return (
        <div className={"textCenter"}>
            当前商品是：{gid} 号商品
            <p>
                <Button type="primary" onClick={() => {
                    // replace : false 该跳转不要记录(跳转后无法通过后退键返回跳转前的页面)
                    navigate("/goods/detail/" + Math.floor(Math.random() * 99999 + 1));
                }}>
                    随机浏览一个商品
                </Button>
            </p>
        </div>
    );
}

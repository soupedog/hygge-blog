import {Params, useLocation, useNavigate, useParams,} from "react-router-dom";
import * as React from "react";
import {Location} from "history";
import {NavigateFunction} from "react-router/lib/hooks";


export interface ReactRouter {
    location: Location;
    navigate: NavigateFunction;
    params: Readonly<Params<string>>;
}

// @ts-ignore
export function withRouter(Component) {
    function ComponentWithRouterProp(props: any) {
        let location = useLocation();
        let navigate = useNavigate();
        let params = useParams();
        // @ts-ignore
        return <Component {...props} router={{location, navigate, params}}/>
    }

    return ComponentWithRouterProp;
}

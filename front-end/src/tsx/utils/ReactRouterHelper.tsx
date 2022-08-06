import {Params, URLSearchParamsInit, useLocation, useNavigate, useParams, useSearchParams,} from "react-router-dom";
import * as React from "react";
import {Location} from "history";
import {NavigateFunction} from "react-router/lib/hooks";


export interface ReactRouter {
    location: Location;
    navigate: NavigateFunction;
    params: Readonly<Params<string>>;
    searchParams: URLSearchParams;
    setSearchParams: (nextInit: URLSearchParamsInit, navigateOptions?: ({replace?: boolean | undefined, state?: any} | undefined)) => void;
}

// @ts-ignore
export function withRouter(Component) {
    function ComponentWithRouterProp(props: any) {
        let location = useLocation();
        let navigate = useNavigate();
        let params = useParams();
        let [searchParams, setSearchParams] = useSearchParams();
        // @ts-ignore
        console.log(Component.name+"------")
        return <Component {...props} router={{location, navigate, params, searchParams, setSearchParams}}/>
    }

    return ComponentWithRouterProp;
}

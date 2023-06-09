/**
 * Copyright JS Foundation and other contributors, http://js.foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

$(function () {

    var protocol = window.location.protocol;
    var host = window.location.host;
    var tokenKey = "X-Access-Token";

    var getToken = function () {
        let token = getQueryVariable(tokenKey);
        if (token) {
            return token;
        }
        return localStorage.getItem(tokenKey) || localStorage.getItem("x-access-token");
    }

    var conf = {
        apiRootUrl: protocol + "//" + host + "/api/rule-editor",
        wsUrl: (protocol === 'https:' ? "wss" : "ws") + "//" + host + "/api",
        apiBasePath: protocol + "//" + host + "/api",
        tokenValue: getToken(),
        tokenKey: tokenKey,
        wsTokenKey:":X_Access_Token"
    };

    console.log(conf);
    RED.init(conf);
});

//封装axios
import axios from 'axios'
import router from "@/router";
import { ElMessage } from 'element-plus';
import { useElementBounding } from '@vueuse/core';

const TOKEN_INVALID = 'token 认证失败，请重新登陆！'

const ENTWORK_ERROR = '网络请求异常，请稍后重试！'

// axios.defaults.headers['Content-Type'] = 'apxplication/json; charset=utf-8';

const request = axios.create({
    baseURL: 'http://localhost:9090', //全局统一加上'/api'前缀，即所有接口都会加上这个前缀，在页面写接口是不需要再加'/api'，否则会报错
    timeout: 5000
})

//可以在请求发送前对请求做一些处理，统一加token，对参数统一加密

request.interceptors.request.use(config => {
    if(config && config.headers){
        config.headers['Content-Type'] = 'application/json; charset=utf-8';
    }

    let adminJson = localStorage.getItem("admin") ? JSON.parse(localStorage.getItem("admin") || '') : {};
    if(config && config.headers) {
        config.headers['token'] = adminJson.token
    }
    if(!adminJson) {
        router.push("/login");
    }
    return config
},
error => {
    return Promise.reject(error)
});

//在接口相应后统一处理结果
request.interceptors.response.use(res=> {
    console.log(res)
    let re = res.data;
    //返回文件
    if(res.config.responseType === 'blob') {
        return re;
    }
    //返回字符串数据
    if(typeof re === 'string') {
        re = re ? JSON.parse(re):re //json字符串转为json对象
    }
    //权限不通过时给出提示
    if(re.code === '401') {
        ElMessage.error(TOKEN_INVALID)
        return Promise.reject(TOKEN_INVALID)
    }
    return re
}, error => {
    console.log('err' + error)
    return Promise.reject(error)
})

export default request
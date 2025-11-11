import axios from 'axios'
import router from "@/router";
import { Message } from 'element-ui'

const request = axios.create({
    baseURL: 'http://localhost:9090',
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers = config.headers || {};
    
    // 获取用户信息
    let user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
    if (user) {
        config.headers['token'] = user.token;
    }
    
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        // 处理业务错误码
        if (res.code === '500') {
            Message.error(res.msg || '服务器错误');
            return Promise.reject(new Error(res.msg || '服务器错误'));
        }

        // 处理其他业务错误码
        if (res.code !== '200') {
            Message.error(res.msg || '操作失败');
            return Promise.reject(new Error(res.msg || '操作失败'));
        }

        return res;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    Message.error('登录已过期，请重新登录');
                    router.push("/login");
                    break;
                case 403:
                    Message.error('没有权限访问该资源');
                    break;
                case 404:
                    Message.error('请求的资源不存在');
                    break;
                case 500:
                    Message.error('服务器错误');
                    break;
                default:
                    Message.error(error.response.data?.msg || '未知错误');
            }
        } else {
            Message.error('网络连接失败，请检查网络设置');
        }
        return Promise.reject(error)
    }
)

export default request


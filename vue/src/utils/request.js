import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000  // 后台接口超时时间
})

// 导入用户存储管理工具
import { getToken, clearUserInfo } from './userStorage.js';

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    // 从用户存储管理工具获取token
    const token = getToken();
    if (token) {
        config.headers['token'] = token;
    }
    return config;
}, error => {
    return Promise.reject(error);
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

        // 当token过期或验证失败时给出提示
        if (res.code === '10003') {
            ElMessage.error('登录已过期，请重新登录')
            // 清除本地存储的用户信息
            clearUserInfo();
            // 跳转到登录页面
            router.push('/login')
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
    error => {
        if (error.response) {
            if (error.response.status === 401) {
                // 未授权，可能是token过期或无效
                ElMessage.error('登录已过期，请重新登录')
                // 清除本地存储的用户信息
                clearUserInfo();
                // 跳转到登录页面
                router.push('/login')
            } else if (error.response.status === 404) {
                ElMessage.error('未找到请求接口')
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请稍后重试')
            } else {
                ElMessage.error(`请求失败：${error.response.status}`)
            }
        } else {
            ElMessage.error('网络异常，请检查网络连接')
        }
        return Promise.reject(error)
    }
)

export default request

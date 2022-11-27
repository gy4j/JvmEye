import axios from 'axios';
import {Message} from 'element-ui';
import {setSession} from './SessionUtil';

let axiosWrap = axios;

// 创建axios实例
axiosWrap = axios.create({
  timeout: 60000, // 请求超时时间
});
axiosWrap.defaults.headers.post['Content-Type'] = 'application/json';

axiosWrap.defaults.withCredentials = true;

// request拦截器
axiosWrap.interceptors.request.use(config => {
  return config;
}, error => {
  // Do something with request error
  console.log(error); // for debug
  Promise.reject(error);
});

// respone拦截器
axiosWrap.interceptors.response.use(
  response => {
    let ajaxResult = response.data;
    if (typeof ajaxResult === 'string') {
      ajaxResult = JSON.parse(ajaxResult);
    }
    if (ajaxResult.code) {
      if (ajaxResult.code == 200) {
        if ('status' in ajaxResult.data && ajaxResult.data.status != 0) {
          Message({
            showClose: true,
            message: ajaxResult.data.msg,
            type: 'warning',
            duration: 3 * 1000,
          });
        } else {
          return ajaxResult.data;
        }
      } else if (ajaxResult.code == 401) {
        setSession(null);
        // window.location.href = envUtil.logoutUrl();
        window.location.reload();
        Message({
          showClose: true,
          message: '登录失效',
          type: 'error',
          duration: 3 * 1000,
        });
        return Promise.reject(new Error('登录失效'));
      } else if (ajaxResult.code == 500) {
        Message({
          showClose: true,
          message: ajaxResult.msg,
          type: 'error',
          duration: 3 * 1000,
        });
        return Promise.reject(new Error(ajaxResult.msg));
      }
    } else {
      return ajaxResult.data;
    }
  },
  error => {
    console.log('err' + error);// for debug
    Message({
      showClose: true,
      message: error.message,
      type: 'error',
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  },
);

export default axiosWrap;

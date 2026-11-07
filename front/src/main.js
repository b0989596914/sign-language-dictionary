import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import { createPinia } from 'pinia'

// 引入 Bootstrap 和 BootstrapVueNext 的 CSS 文件
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue-next/dist/bootstrap-vue-next.css";
import { BootstrapVueIcons } from "bootstrap-vue-next";

// 引入 BootstrapVueNext
import BootstrapVueNext from "bootstrap-vue-next";
import $ from 'jquery';
// import axios from 'axios';
// import axios from 'axios';

// axios.interceptors.request.use(config => {
//   console.log("Request URL:", config.url);
//   return config;
// }, error => {
//   return Promise.reject(error);
// });

// import VueVideoPlayer from '@videojs-player/vue'
// import 'video.js/dist/video-js.css'
// import lazyLoad from './directives/lazyLoad';

// 設定全域 baseURL
// axios.defaults.baseURL = '/api';

// 創建 Vue 應用
const app = createApp(App);
app.use(createPinia())

window.$ = window.jQuery = $;  // 確保 Turn.js 使用 jQuery 實例


// 添加全局提供的 `baseURL` 配置
app.provide("global", {
  baseURL: "http://localhost:9001",
});

// app.directive('lazy', lazyLoad);

// 設定網址的全域變數 -> /vocal/api/
// app.provide('apiBaseUrl', '/vocal/api')
// app.provide('apiBaseUrl', '/api')

// 註冊插件
app.use(BootstrapVueNext);
app.use(BootstrapVueIcons);
// app.use(VueVideoPlayer)
app.use(store);
app.use(router);

// 掛載應用
app.mount("#app");

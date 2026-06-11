// constant/index.js

// Vue 3不需要直接导入Vue构造函数
import Enum from './enum'
import EnumModule from './module'

// 导出插件配置对象
const EnumPlugin = {
  install: (app) => {
    Enum.install(app, EnumModule)
  }
}

export default EnumPlugin
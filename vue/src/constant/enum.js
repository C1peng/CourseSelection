/** enum.js
 * 枚举工具类的定义
 * Vue 3版本 - 可以全局使用this.$enum.getDescByValue()等等...
 */

const EnumUtil = {}

// 修改install方法以支持Vue 3
EnumUtil.install = function (app, data) {
  const constantInfo = data || {}
  const Enum = { ...data }
  
  /**
   * 根据枚举值获取描述
   * @param {*} constantName  枚举对象的名字
   * @param {*} value         枚举值
   */
  Enum.getDescByValue = function (constantName, value) {
    if (!constantInfo.hasOwnProperty(constantName)) {
      return ''
    }
    let constantItem = constantInfo[constantName] // 通过传进来的名字拿到所对应的常量项
    for (let item in constantItem) {
      // 循环常量项，使用宽松比较以支持类型转换
      if (constantItem[item].value == value) {
        // 同时支持label和desc属性
        return constantItem[item].label || constantItem[item].desc || ''
      }
    }
  }

  // Vue 3使用app.config.globalProperties替代Vue.prototype
  app.config.globalProperties.$enum = Enum
}

export default EnumUtil
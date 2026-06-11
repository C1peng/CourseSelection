// 工具函数：创建统一格式的枚举项，减少重复代码
function createEnumItem(value, label) {
  return { 
    value, 
    label, 
    desc: label // desc自动设为label的值，兼容enum.js中的getDescByValue方法
  };
}

// 性别常量
export const GENDER = {
  MALE: createEnumItem(1, '男'),
  FEMALE: createEnumItem(2, '女')
};

// 课程类型常量
export const COURSE_TYPE = {
  SELECTION: createEnumItem(0, '理论课'),
  COMPULSORY: createEnumItem(1, '实践课'),
  COMBINATION: createEnumItem(2, '理论-事件课')
};

// 课程状态常量
export const COURSE_STATUS = {
  NORMAL: createEnumItem(0, '未开始'),
  DISABLED: createEnumItem(1, '进行中'),
  ENDED: createEnumItem(2, '已结课')
};

// 学分类型常量
export const CREDIT_TYPE = {
  PUBLIC: createEnumItem('PUBLIC', '公共选修'),
  FOREIGN: createEnumItem('FOREIGN_LANGUAGE', '外语选修'),
  SPORT: createEnumItem('SPORT', '体育选修'),
  ART: createEnumItem('ART', '艺术类选修')
  // OTHER: createEnumItem('OTHER', '其他选修')
};
// 周次类型常量
export const CYCLE_TYPE = {
  SINGLE: createEnumItem('SINGLE', '单周'),
  DOUBLE: createEnumItem('DOUBLE', '双周'),
  ALL: createEnumItem('ALL', '全周')
};



// 星期常量
export const WEEKDAY = {
  MONDAY: createEnumItem('1', '一'),
  TUESDAY: createEnumItem('2', '二'),
  WEDNESDAY: createEnumItem('3', '三'),
  THURSDAY: createEnumItem('4', '四'),
  FRIDAY: createEnumItem('5', '五')
};

// 导出默认对象以保持向后兼容
export default {
  Gender: GENDER,
  COURSETYPE: COURSE_TYPE,
  COURSESTATUS: COURSE_STATUS,
  CREDITTYPE: CREDIT_TYPE,
  WEEKDAY: WEEKDAY
};
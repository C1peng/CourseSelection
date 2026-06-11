// 用户信息存储管理工具

// 存储键名
const USER_STORAGE_KEY = 'xm-user';

/**
 * 存储用户信息到本地
 * @param {Object} userInfo - 用户信息对象
 */
export const saveUserInfo = (userInfo) => {
  if (!userInfo || typeof userInfo !== 'object') {
    console.error('用户信息格式错误');
    return;
  }
  
  try {
    const userStr = JSON.stringify(userInfo);
    localStorage.setItem(USER_STORAGE_KEY, userStr);
  } catch (error) {
    console.error('存储用户信息失败:', error);
  }
};

/**
 * 从本地获取用户信息
 * @returns {Object|null} 用户信息对象或null
 */
export const getUserInfo = () => {
  try {
    const userStr = localStorage.getItem(USER_STORAGE_KEY);
    return userStr ? JSON.parse(userStr) : null;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    return null;
  }
};

/**
 * 从本地获取token
 * @returns {string|null} token字符串或null
 */
export const getToken = () => {
  const userInfo = getUserInfo();
  return userInfo?.token || null;
};

/**
 * 清除本地存储的用户信息
 */
export const clearUserInfo = () => {
  try {
    localStorage.removeItem(USER_STORAGE_KEY);
  } catch (error) {
    console.error('清除用户信息失败:', error);
  }
};

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export const isLoggedIn = () => {
  const token = getToken();
  return !!token;
};

/**
 * 获取当前用户角色
 * @returns {string|null} 角色字符串或null
 */
export const getUserRole = () => {
  const userInfo = getUserInfo();
  return userInfo?.role || null;
};

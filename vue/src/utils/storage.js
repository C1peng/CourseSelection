/**
 * 存储工具类
 * 提供统一的存储接口，支持多用户登录信息独立存储
 */

// 存储键名常量
const STORAGE_KEYS = {
  CURRENT_USER: 'current_user',
  ALL_USERS: 'all_users'
}

/**
 * 存储工具类
 */
const storage = {
  /**
   * 存储当前用户信息
   * @param {Object} userInfo - 用户信息
   */
  setCurrentUser(userInfo) {
    try {
      sessionStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(userInfo))
      this.saveUserInfo(userInfo)
    } catch (error) {
      console.error('存储当前用户信息失败:', error)
    }
  },

  /**
   * 获取当前用户信息
   * @returns {Object|null} 当前用户信息
   */
  getCurrentUser() {
    try {
      const userStr = sessionStorage.getItem(STORAGE_KEYS.CURRENT_USER)
      return userStr ? JSON.parse(userStr) : null
    } catch (error) {
      console.error('获取当前用户信息失败:', error)
      return null
    }
  },

  /**
   * 保存用户信息到本地存储（支持多用户）
   * @param {Object} userInfo - 用户信息
   */
  saveUserInfo(userInfo) {
    try {
      const allUsers = this.getAllUsers()
      const userId = this.getUserId(userInfo)
      
      if (userId) {
        allUsers[userId] = userInfo
        localStorage.setItem(STORAGE_KEYS.ALL_USERS, JSON.stringify(allUsers))
      }
    } catch (error) {
      console.error('保存用户信息失败:', error)
    }
  },

  /**
   * 获取所有用户信息
   * @returns {Object} 所有用户信息
   */
  getAllUsers() {
    try {
      const usersStr = localStorage.getItem(STORAGE_KEYS.ALL_USERS)
      return usersStr ? JSON.parse(usersStr) : {}
    } catch (error) {
      console.error('获取所有用户信息失败:', error)
      return {}
    }
  },

  /**
   * 根据用户信息获取用户唯一标识
   * @param {Object} userInfo - 用户信息
   * @returns {string|null} 用户唯一标识
   */
  getUserId(userInfo) {
    if (!userInfo) return null
    
    // 使用用户名和角色组合作为唯一标识
    if (userInfo.username && userInfo.role) {
      return `${userInfo.username}_${userInfo.role}`
    }
    
    // 如果有id字段，使用id
    if (userInfo.id) {
      return userInfo.id.toString()
    }
    
    return null
  },

  /**
   * 删除指定用户信息
   * @param {Object} userInfo - 用户信息
   */
  removeUserInfo(userInfo) {
    try {
      const allUsers = this.getAllUsers()
      const userId = this.getUserId(userInfo)
      
      if (userId && allUsers[userId]) {
        delete allUsers[userId]
        localStorage.setItem(STORAGE_KEYS.ALL_USERS, JSON.stringify(allUsers))
      }
    } catch (error) {
      console.error('删除用户信息失败:', error)
    }
  },

  /**
   * 清空当前用户信息
   */
  clearCurrentUser() {
    try {
      sessionStorage.removeItem(STORAGE_KEYS.CURRENT_USER)
    } catch (error) {
      console.error('清空当前用户信息失败:', error)
    }
  },

  /**
   * 清空所有存储的用户信息
   */
  clearAllUsers() {
    try {
      localStorage.removeItem(STORAGE_KEYS.ALL_USERS)
      sessionStorage.removeItem(STORAGE_KEYS.CURRENT_USER)
    } catch (error) {
      console.error('清空所有用户信息失败:', error)
    }
  },

  /**
   * 获取当前用户的token
   * @returns {string} token
   */
  getToken() {
    const currentUser = this.getCurrentUser()
    return currentUser?.token || ''
  }
}

export default storage
/** module/index.js
 * 常量枚举池
 * 可以根据文件对枚举所属的模块进行拆分
 * 此文件主要是收集模块常量然后统一暴露出去
 */
 
const EnumModule = {};
 
// 批量导入当前文件所在目录下的.js文件--直接引入
// Vite 3.0+ 使用 import.meta.glob 替代 import.meta.globEager
const modules = import.meta.glob('./*.js', { eager: true });
Object.keys(modules).forEach((fileName) => {
    // 跳过index.js文件自身
    if (fileName.includes('index.js')) return;
    Object.assign(EnumModule, {
        ...modules[fileName].default
    });
});
 
export default EnumModule;
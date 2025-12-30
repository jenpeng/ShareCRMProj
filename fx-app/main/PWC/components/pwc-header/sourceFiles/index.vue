<template>
  <div class="app">
    <pwc-button>默认按钮</pwc-button>
    <pwc-header
      :logo-type="logoType"
      :logo-src="customLogo"
      :site-name="customSiteName"
      :banner-title="customBannerTitle"
      :search-placeholder="searchPlaceholder"
      :show-top-menu="false"
      :show-login-button="true"
      :login-type="loginType"
      :login-icon="loginIcon"
      :locale="locale"
      :background-config="{
        topMenu: 'transparent',
        mainNav: '#ffffff',
        banner: '#ffffff'
      }"
      :nav-area-style="navAreaStyle"
      @login="handleLogin"
      @search="handleSearch"
    />

    <main class="main-content">
      <div class="content-placeholder">
        <h2>页面主要内容区域</h2>
        <p>向下滚动查看头部固定效果</p>
      </div>
    </main>
  </div>
</template>

<script>

import PwcHeader from './PwcHeader.vue'
  
export default {
  name: 'App',
  components: { PwcHeader },
  data() {
    return {
      /* ===== 1. Logo：文字或外链图片 ===== */
      customLogo: 'CSSC',                          // 文字
      // customLogo: 'https://example.com/logo.png', // 外链图片

      customSiteName: 'CSSC',                      // 备用文字（图片失效时显示）
      customBannerTitle: 'Intelligent Service Guardian for Every Voyage',
      searchPlaceholder: 'Search Vessel Name / IMO',

      /* ===== 2. 登录按钮：标准文字 / 标准图标 / 自定义外链图标 ===== */
      loginIcon: '',                               // 空 = 文字按钮
      // loginIcon: 'user',                        // 内置图标名（如组件库支持）
      // loginIcon: 'https://example.com/user.svg',// 外链图标

      /* ===== 3. 导航栏背景：纯色值 or 外链图片 ===== */
      navBg: '#ffffff',                            // 纯色
      // navBg: 'https://example.com/nav-bg.jpg',  // 外链图片

      locale: 'en'
    }
  },
  computed: {
    /* 自动识别类型 */
    logoType() {
      return this.customLogo.startsWith('http') ? 'img' : 'text'
    },
    loginType() {
      if (!this.loginIcon) return 'text'
      return this.loginIcon.startsWith('http') ? 'icon' : 'icon'
    },
    navAreaStyle() {
      return this.navBg.startsWith('http')
        ? { backgroundImage: `url(${this.navBg})`, backgroundSize: 'cover', backgroundPosition: 'center' }
        : { background: this.navBg }
    }
  },
  methods: {
    handleLogin() {
      console.log('登录按钮点击')
    },
    handleSearch(query) {
      console.log('搜索:', query)
    }
  }
}
</script>

<style>
/* 原样式不动 */
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: Arial, sans-serif; background: #f5f5f5; line-height: 1.6; }
.app { min-height: 100vh; }
.main-content { padding: 40px 20px; margin-top: 0; }
.content-placeholder { max-width: 1200px; margin: 0 auto; text-align: center; padding: 100px 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,.1); }
.content-placeholder h2 { color: #333; margin-bottom: 20px; font-size: 2rem; }
.content-placeholder p { color: #666; font-size: 16px; }
</style>
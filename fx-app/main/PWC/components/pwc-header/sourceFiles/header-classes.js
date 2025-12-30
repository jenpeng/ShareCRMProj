/**
 * PWC Header 样式类计算（含扩展）
 */

export const headerClasses = {
  computed: {
    /**
     * 计算头部容器的 CSS 类
     */
    headerClasses() {
      const classes = ['pwc-header']
      if (this.fixed) classes.push('pwc-header--fixed')
      if (this.scrollClass) classes.push(this.scrollClass)
      return classes
    },

    /**
     * 移动端菜单类
     */
    mobileMenuClass() {
      return this.isMobileMenuOpen ? 'pwc-header__mobile-menu--open' : ''
    },

    /**
     * 顶部菜单样式
     */
    topBarStyle() {
      return this.backgroundConfig.topMenu
        ? { background: this.backgroundConfig.topMenu }
        : {}
    },

    /**
     * 横幅区域样式
     */
    bannerStyle() {
      return this.backgroundConfig.banner
        ? { background: this.backgroundConfig.banner }
        : {}
    },

    /**
     * 整体头部样式
     */
    headerStyle() {
      const style = {}
      if (this.backgroundConfig.header) {
        style.background = this.backgroundConfig.header
      }
      return style
    },

    /* ===== 扩展计算属性 ===== */
    navAreaClasses() {
      const base = ['pwc-header__main-nav-area']
      if (this.navAreaClass) base.push(this.navAreaClass)
      return base
    },

    navAreaStyles() {
      const style = { ...this.navAreaStyle }
      if (this.navAreaStyle.backgroundImage) {
        style.backgroundImage = this.navAreaStyle.backgroundImage
        style.backgroundSize = 'cover'
        style.backgroundPosition = 'center'
      }
      return style
    }
  }
}
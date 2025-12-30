/**
 * PWC Header 组件属性定义（含扩展）
 */

export const headerProps = {
  /* ===== 原基础 props ===== */
  logo: {
    type: String,
    default: 'CSSC'
  },
  siteName: {
    type: String,
    default: 'CSSC'
  },
  topMenuItems: {
    type: Array,
    default: () => [
      'Home',
      'About Us',
      'Products',
      'Services',
      'Media Center',
      'Contact Us'
    ]
  },
  bannerTitle: {
    type: String,
    default: 'Intelligent Service Guardian for Every Voyage'
  },
  searchPlaceholder: {
    type: String,
    default: 'Search Vessel Name / IMO'
  },
  fixed: {
    type: Boolean,
    default: false
  },
  theme: {
    type: String,
    default: 'light',
    validator: (value) => ['light', 'dark'].includes(value)
  },
  backgroundConfig: {
    type: Object,
    default: () => ({
      topMenu: '#f8f9fa',
      mainNav: '#ffffff',
      banner: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)'
    })
  },
  showTopMenu: {
    type: Boolean,
    default: true
  },
  showLoginButton: {
    type: Boolean,
    default: true
  },
  showBanner: {
    type: Boolean,
    default: true
  },

  /* ===== 扩展 props ===== */
  logoType: {
    type: String,
    default: 'text',
    validator: (v) => ['text', 'img'].includes(v)
  },
  logoSrc: {
    type: String,
    default: ''
  },
  navAreaStyle: {
    type: Object,
    default: () => ({})
  },
  navAreaClass: {
    type: String,
    default: ''
  },
  loginType: {
    type: String,
    default: 'text',
    validator: (v) => ['text', 'icon', 'slot'].includes(v)
  },
  loginIcon: {
    type: String,
    default: ''
  },
  locale: {
    type: String,
    default: 'zh'
  }
}
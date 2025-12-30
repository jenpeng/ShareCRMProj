/**
 * PWC Header 状态管理 - 简化版
 */

export const headerState = {
  data() {
    return {
      isScrolled: false,
      isMobileMenuOpen: false,
      searchQuery: ''
    }
  },
  
  computed: {
    // 计算滚动状态类
    scrollClass() {
      return this.isScrolled ? 'pwc-header--scrolled' : ''
    },
    
    // 计算移动端菜单类
    mobileMenuClass() {
      return this.isMobileMenuOpen ? 'pwc-header__mobile-menu--open' : ''
    }
  },
  
  methods: {
    /**
     * 切换移动端菜单
     */
    toggleMobileMenu() {
      this.isMobileMenuOpen = !this.isMobileMenuOpen
      this.$emit('mobile-menu-toggle', this.isMobileMenuOpen)
    },
    
    /**
     * 处理滚动事件
     */
    handleScroll() {
      this.isScrolled = window.scrollY > 50
    }
  },
  
  mounted() {
    // 监听滚动事件
    window.addEventListener('scroll', this.handleScroll)
  },
  
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('scroll', this.handleScroll)
  }
}
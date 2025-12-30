<template>
  <header class="pwc-header">
    <!-- 主导航 -->
    <div class="pwc-header__main-nav-area">
      <div class="pwc-header__container">
        <nav class="pwc-header__main-nav">
          <!-- Logo -->
          <div class="pwc-header__logo">{{ siteName || logo }}</div>

          <!-- 桌面横向菜单 -->
          <ul class="pwc-header__nav-list">
            <li
              v-for="(item, idx) in topMenuItems"
              :key="idx"
              class="pwc-header__nav-item"
            >
              {{ item }}
            </li>
          </ul>

          <!-- 登录按钮 -->
          <div class="pwc-header__actions" v-if="showLoginButton">
            <button class="pwc-header__login-btn" @click="handleLogin">
              Log in
            </button>
          </div>

          <!-- 移动端汉堡 -->
          <button class="pwc-header__mobile-toggle" @click="toggleMobileMenu">
            ☰
          </button>
        </nav>
      </div>
    </div>

    <!-- 横幅 -->
    <div class="pwc-header__banner">
      <div class="pwc-header__container">
        <button class="pwc-header__play">PLAY VIDEO</button>
        <h1 class="pwc-header__banner-title">{{ bannerTitle }}</h1>
        <div class="pwc-header__search">
          <input
            type="text"
            class="pwc-header__search-input"
            :placeholder="searchPlaceholder"
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <button class="pwc-header__search-button" @click="handleSearch">
            Search
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { headerProps } from './header-props.js'
import { headerClasses } from './header-classes.js'
import { headerState } from './header-state.js'
import './pwc-header.css'

export default {
  name: 'PwcHeader',
  props: headerProps,
  emits: ['login', 'search'],
  mixins: [headerState],
  methods: {
    handleLogin() { this.$emit('login') },
    handleSearch() {
      if (this.searchQuery.trim()) this.$emit('search', this.searchQuery.trim())
    }
  }
}
</script>
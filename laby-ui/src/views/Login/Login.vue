<template>
  <div :class="prefixCls" class="login-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="decoration-circle decoration-circle-1"></div>
      <div class="decoration-circle decoration-circle-2"></div>
      <div class="decoration-circle decoration-circle-3"></div>
    </div>

    <div class="login-content">
      <!-- 左侧品牌区域 -->
      <div :class="`${prefixCls}__left brand-section`">
        <div class="brand-header">
          <div class="logo-wrapper">
            <img alt="" class="logo-image" src="@/assets/imgs/logo.png" />
            <span class="brand-title">{{ underlineToHump(appStore.getTitle) }}</span>
          </div>
        </div>
        
        <div class="brand-content">
          <TransitionGroup
            appear
            enter-active-class="animate__animated animate__fadeInUp"
            tag="div"
            class="brand-animation-wrapper"
          >
            <div key="1" class="brand-illustration">
              <img alt="" class="illustration-image" src="@/assets/svgs/login-box-bg.svg" />
            </div>
            <div key="2" class="welcome-text">
              <h1 class="welcome-title">{{ t('login.welcome') }}</h1>
              <p class="welcome-subtitle">{{ t('login.message') }}</p>
            </div>
          </TransitionGroup>
        </div>
        
        <div class="brand-footer">
          <div class="feature-cards">
            <div class="feature-card">
              <div class="feature-card-icon">🚀</div>
              <div class="feature-card-title">快速响应</div>
            </div>
            <div class="feature-card">
              <div class="feature-card-icon">🔒</div>
              <div class="feature-card-title">安全可靠</div>
            </div>
            <div class="feature-card">
              <div class="feature-card-icon">⚡</div>
              <div class="feature-card-title">高效便捷</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="form-section">
        <!-- 顶部工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <img alt="" class="mobile-logo" src="@/assets/imgs/logo.png" />
            <span class="mobile-brand">{{ underlineToHump(appStore.getTitle) }}</span>
          </div>
          <div class="toolbar-right">
            <ThemeSwitch />
            <LocaleDropdown />
          </div>
        </div>

        <!-- 登录表单区域 -->
        <Transition appear enter-active-class="animate__animated animate__fadeIn">
          <div class="form-wrapper">
            <div class="form-card">
              <!-- 账号登录 -->
              <LoginForm class="login-form-content" />
              <!-- 手机登录 -->
              <MobileForm class="login-form-content" />
              <!-- 二维码登录 -->
              <QrCodeForm class="login-form-content" />
              <!-- 注册 -->
              <RegisterForm class="login-form-content" />
              <!-- 三方登录 -->
              <SSOLoginVue class="login-form-content" />
              <!-- 忘记密码 -->
              <ForgetPasswordForm class="login-form-content" />
            </div>
          </div>
        </Transition>

        <!-- 底部版权信息 -->
        <div class="footer">
          <p class="footer-text">© 2025 All Rights Reserved</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { underlineToHump } from '@/utils'

import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import { LocaleDropdown } from '@/layout/components/LocaleDropdown'

import { LoginForm, MobileForm, QrCodeForm, RegisterForm, SSOLoginVue, ForgetPasswordForm } from './components'

defineOptions({ name: 'Login' })

const { t } = useI18n()
const appStore = useAppStore()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('login')
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-login;

.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #d946ef 100%);
  
  &.dark {
    background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);
  }
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
  
  &-1 {
    width: 300px;
    height: 300px;
    top: -150px;
    left: -150px;
    animation-delay: 0s;
  }
  
  &-2 {
    width: 400px;
    height: 400px;
    bottom: -200px;
    right: -200px;
    animation-delay: 5s;
  }
  
  &-3 {
    width: 250px;
    height: 250px;
    top: 50%;
    right: 10%;
    animation-delay: 10s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
    opacity: 0.6;
  }
}

.login-content {
  position: relative;
  display: flex;
  width: 100%;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 0;
  z-index: 1;
  overflow: hidden;
  
  @media (max-width: 1200px) {
    padding: 20px 0;
  }
}

.brand-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 50px 60px;
  color: white;
  position: relative;
  
  @media (max-width: 1200px) {
    display: none;
  }
}

.brand-header {
  flex-shrink: 0;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-image {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.brand-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-animation-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
  text-align: center;
}

.brand-illustration {
  .illustration-image {
    width: 380px;
    max-width: 100%;
    filter: drop-shadow(0 10px 40px rgba(0, 0, 0, 0.2));
  }
}

.welcome-text {
  .welcome-title {
    font-size: 44px;
    font-weight: 800;
    margin: 0 0 12px 0;
    line-height: 1.2;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
  }
  
  .welcome-subtitle {
    font-size: 17px;
    font-weight: 400;
    opacity: 0.92;
    margin: 0;
    line-height: 1.6;
  }
}

.brand-footer {
  flex-shrink: 0;
  margin-top: auto;
}

.feature-cards {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.feature-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 16px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.12);
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }
}

.feature-card-icon {
  font-size: 36px;
  margin-bottom: 4px;
}

.feature-card-title {
  font-size: 15px;
  font-weight: 600;
  text-align: center;
}

.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  overflow: hidden;
  
  @media (max-width: 1200px) {
    padding: 20px;
  }
  
  @media (max-width: 640px) {
    padding: 10px;
  }
}

.toolbar {
  position: absolute;
  top: 50px;
  right: 60px;
  display: flex;
  align-items: center;
  gap: 12px;
  
  @media (max-width: 1200px) {
    top: 30px;
    right: 30px;
  }
  
  @media (max-width: 640px) {
    top: 20px;
    right: 20px;
  }
}

.toolbar-left {
  display: none;
}

.mobile-logo {
  width: 48px;
  height: 48px;
  border-radius: 8px;
}

.mobile-brand {
  font-size: 20px;
  font-weight: 700;
  color: white;
  
  .form-section & {
    @media (max-width: 1200px) {
      color: white;
    }
  }
  
  .dark .form-section & {
    color: white;
  }
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  overflow: hidden;
}

.form-card {
  width: 100%;
  max-width: 480px;
  padding: 40px 44px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  
  @media (max-width: 640px) {
    max-width: 90%;
    padding: 32px 28px;
    border-radius: 16px;
  }
  
  @media (max-width: 1200px) {
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(30px);
  }
  
  .dark & {
    background: rgba(15, 23, 42, 0.98);
    backdrop-filter: blur(30px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6);
    border: 1px solid rgba(148, 163, 184, 0.1);
  }
}

.login-form-content {
  width: 100%;
  overflow: hidden;
}

.footer {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  
  @media (max-width: 640px) {
    bottom: 20px;
  }
  
  .footer-text {
    margin: 0;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    
    .dark & {
      color: rgba(255, 255, 255, 0.5);
    }
  }
}

// 原有样式保留
.#{$prefix-cls} {
  &__left {
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: -1;
      width: 100%;
      height: 100%;
      background-image: url('@/assets/svgs/login-bg.svg');
      background-position: center;
      background-repeat: no-repeat;
      content: '';
      opacity: 0.1;
    }
  }
}
</style>

<style lang="scss">
.dark .login-form {
  .el-divider__text {
    background-color: transparent;
  }

  .el-card {
    background-color: transparent;
  }
}

// 优化表单样式
.login-form-content {
  .el-form-item {
    margin-bottom: 18px;
  }
  
  .el-input {
    .el-input__wrapper {
      border-radius: 10px;
      padding: 6px 15px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      transition: all 0.25s ease;
      border: 1px solid transparent;
      
      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
        border-color: rgba(99, 102, 241, 0.3);
      }
      
      &.is-focus {
        box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
        border-color: #6366f1;
      }
    }
  }
  
  .el-button {
    border-radius: 10px;
    height: 42px;
    font-weight: 600;
    letter-spacing: 0.3px;
    transition: all 0.25s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 20px rgba(99, 102, 241, 0.35);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  .el-checkbox {
    .el-checkbox__label {
      font-size: 13px;
    }
  }
  
  .el-link {
    font-size: 13px;
    font-weight: 500;
  }
  
  .el-divider {
    margin: 24px 0;
    
    .el-divider__text {
      background-color: white;
      padding: 0 16px;
      font-size: 13px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.4);
    }
  }
  
  .dark & {
    .el-input__wrapper {
      &.is-focus {
        box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.15);
        border-color: #818cf8;
      }
      
      &:hover {
        border-color: rgba(129, 140, 248, 0.4);
      }
    }
    
    .el-button:hover {
      box-shadow: 0 6px 20px rgba(129, 140, 248, 0.4);
    }
    
    .el-divider__text {
      background-color: rgba(15, 23, 42, 0.98);
      color: rgba(255, 255, 255, 0.5);
    }
  }
}
</style>

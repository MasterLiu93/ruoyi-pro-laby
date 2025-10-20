<template>
  <div class="login-form-title">
    <h2 class="title-text">
      {{ getFormTitle }}
    </h2>
    <div class="title-underline"></div>
  </div>
</template>
<script lang="ts" setup>
import { LoginStateEnum, useLoginState } from './useLogin'

defineOptions({ name: 'LoginFormTitle' })

const { t } = useI18n()

const { getLoginState } = useLoginState()

const getFormTitle = computed(() => {
  const titleObj = {
    [LoginStateEnum.RESET_PASSWORD]: t('sys.login.forgetFormTitle'),
    [LoginStateEnum.LOGIN]: t('sys.login.signInFormTitle'),
    [LoginStateEnum.REGISTER]: t('sys.login.signUpFormTitle'),
    [LoginStateEnum.MOBILE]: t('sys.login.mobileSignInFormTitle'),
    [LoginStateEnum.QR_CODE]: t('sys.login.qrSignInFormTitle'),
    [LoginStateEnum.SSO]: t('sys.login.ssoFormTitle')
  }
  return titleObj[unref(getLoginState)]
})
</script>

<style lang="scss" scoped>
.login-form-title {
  text-align: center;
  margin-bottom: 32px;
  animation: fadeInDown 0.5s ease-out;
}

.title-text {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
  line-height: 1.3;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #d946ef 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.3px;
}

.title-underline {
  width: 50px;
  height: 3px;
  margin: 0 auto;
  background: linear-gradient(90deg, #6366f1 0%, #8b5cf6 50%, #d946ef 100%);
  border-radius: 3px;
  animation: expandWidth 0.6s ease-out;
}

.dark {
  .title-text {
    background: linear-gradient(135deg, #818cf8 0%, #a78bfa 50%, #e879f9 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .title-underline {
    background: linear-gradient(90deg, #818cf8 0%, #a78bfa 50%, #e879f9 100%);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes expandWidth {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 50px;
    opacity: 1;
  }
}
</style>

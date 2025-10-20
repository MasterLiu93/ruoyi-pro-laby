<template>
  <div class="home-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card" shadow="never">
        <el-skeleton :loading="loading" animated>
          <div class="welcome-content">
            <div class="welcome-left">
              <div class="avatar-wrapper">
                <el-avatar :src="avatar" :size="80">
                  <img src="@/assets/imgs/avatar.gif" alt="" />
                </el-avatar>
                <div class="online-badge"></div>
              </div>
              <div class="welcome-info">
                <h2 class="welcome-title">
                  欢迎回来, {{ username }} 👋
                </h2>
                <p class="welcome-subtitle">
                  今天也要元气满满哦 · 已为您准备好今日工作概览
                </p>
              </div>
            </div>
            <div class="welcome-right">
              <div class="stat-item">
                <div class="stat-icon projects">
                  <Icon icon="ph:folders-duotone" :size="24" />
                </div>
                <div class="stat-content">
                  <div class="stat-label">{{ t('workplace.project') }}</div>
                  <CountTo
                    class="stat-value"
                    :start-val="0"
                    :end-val="totalSate.project"
                    :duration="2600"
                  />
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon todos">
                  <Icon icon="ph:check-circle-duotone" :size="24" />
                </div>
                <div class="stat-content">
                  <div class="stat-label">{{ t('workplace.toDo') }}</div>
                  <CountTo
                    class="stat-value"
                    :start-val="0"
                    :end-val="totalSate.todo"
                    :duration="2600"
                  />
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon access">
                  <Icon icon="ph:eye-duotone" :size="24" />
                </div>
                <div class="stat-content">
                  <div class="stat-label">{{ t('workplace.access') }}</div>
                  <CountTo
                    class="stat-value"
                    :start-val="0"
                    :end-val="totalSate.access"
                    :duration="2600"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-skeleton>
      </el-card>
    </div>

    <!-- 主内容区域 -->
    <el-row :gutter="16" class="main-content">
      <!-- 左侧内容 -->
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24">
        <!-- 快捷操作 -->
        <el-card class="shortcut-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">{{ t('workplace.shortcutOperation') }}</span>
            </div>
          </template>
          <el-skeleton :loading="loading" animated>
            <div class="shortcut-grid">
              <div
                v-for="item in shortcut"
                :key="item.name"
                class="shortcut-item"
                @click="handleShortcutClick(item.url)"
              >
                <div class="shortcut-icon" :style="{ backgroundColor: item.color + '15' }">
                  <Icon :icon="item.icon" :size="28" :style="{ color: item.color }" />
                </div>
                <span class="shortcut-name">{{ item.name }}</span>
              </div>
            </div>
          </el-skeleton>
        </el-card>

        <!-- 项目列表 -->
        <el-card class="projects-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">{{ t('workplace.project') }}</span>
              <el-link
                type="primary"
                :underline="false"
                href="https://github.com/labycode"
                target="_blank"
              >
                {{ t('action.more') }}
              </el-link>
            </div>
          </template>
          <el-skeleton :loading="loading" animated>
            <div class="projects-grid">
              <div
                v-for="(item, index) in projects"
                :key="`project-${index}`"
                class="project-item"
                @click="handleProjectClick(item.message)"
              >
                <div class="project-icon" :style="{ backgroundColor: item.color + '15' }">
                  <Icon :icon="item.icon" :size="32" :style="{ color: item.color }" />
                </div>
                <div class="project-info">
                  <h4 class="project-name">{{ item.name }}</h4>
                  <p class="project-desc">{{ item.personal }}</p>
                  <div class="project-footer">
                    <span class="project-time">{{ formatTime(item.time, 'yyyy-MM-dd') }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-skeleton>
        </el-card>

         <!-- 数据统计图表 -->
         <el-card class="chart-card" shadow="never">
           <el-skeleton :loading="loading" animated>
             <el-row :gutter="16">
               <el-col :span="12">
                 <div class="chart-section">
                   <h4 class="chart-section-title">用户来源分布</h4>
                   <Echart :options="pieOptionsData" :height="300" />
                 </div>
               </el-col>
               <el-col :span="12">
                 <div class="chart-section">
                   <h4 class="chart-section-title">每周活跃趋势</h4>
                   <Echart :options="barOptionsData" :height="300" />
                 </div>
               </el-col>
             </el-row>
           </el-skeleton>
         </el-card>
       </el-col>

       <!-- 右侧内容 -->
       <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24">
         <!-- 通知公告 -->
         <el-card class="notice-card full-height" shadow="never">
           <template #header>
             <div class="card-header">
               <span class="card-title">{{ t('workplace.notice') }}</span>
               <el-link type="primary" :underline="false">{{ t('action.more') }}</el-link>
             </div>
           </template>
           <el-skeleton :loading="loading" animated>
             <div class="notice-list">
               <div
                 v-for="(item, index) in notice"
                 :key="`notice-${index}`"
                 class="notice-item"
               >
                 <div class="notice-icon">
                   <Icon icon="ph:bell-duotone" :size="20" />
                 </div>
                 <div class="notice-content">
                   <div class="notice-title">
                     <Highlight :keys="item.keys.map((v) => t(v))">
                       {{ item.title }}
                     </Highlight>
                   </div>
                   <div class="notice-meta">
                     <span class="notice-type">{{ item.type }}</span>
                     <span class="notice-date">{{ formatTime(item.date, 'yyyy-MM-dd') }}</span>
                   </div>
                 </div>
               </div>
             </div>
           </el-skeleton>
         </el-card>
       </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { set } from 'lodash-es'
import { EChartsOption } from 'echarts'
import { formatTime } from '@/utils'
import { useUserStore } from '@/store/modules/user'
import type { WorkplaceTotal, Project, Notice, Shortcut } from './types'
import { pieOptions, barOptions } from './echarts-data'
import { useRouter } from 'vue-router'

defineOptions({ name: 'Index' })

const { t } = useI18n()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const avatar = userStore.getUser.avatar
const username = userStore.getUser.nickname
const pieOptionsData = reactive<EChartsOption>(pieOptions) as EChartsOption

// 获取统计数
let totalSate = reactive<WorkplaceTotal>({
  project: 0,
  access: 0,
  todo: 0
})

const getCount = async () => {
  const data = {
    project: 28,
    access: 8956,
    todo: 6
  }
  totalSate = Object.assign(totalSate, data)
}

// 获取项目数
let projects = reactive<Project[]>([])
const getProject = async () => {
  const data = [
    {
      name: '智能博客平台',
      icon: 'ph:article-duotone',
      message: 'blog.labycode.com',
      personal: '基于AI的智能内容管理系统',
      time: new Date('2024-11-15'),
      color: '#6366f1'
    },
    {
      name: '数据分析中心',
      icon: 'ph:chart-line-up-duotone',
      message: 'analytics.labycode.com',
      personal: '实时数据可视化分析平台',
      time: new Date('2024-12-03'),
      color: '#8b5cf6'
    },
    {
      name: '文件管理系统',
      icon: 'ph:folder-open-duotone',
      message: 'files.labycode.com',
      personal: '企业级文档存储与协作',
      time: new Date('2025-01-08'),
      color: '#06b6d4'
    },
    {
      name: '任务管理工具',
      icon: 'ph:kanban-duotone',
      message: 'tasks.labycode.com',
      personal: '敏捷项目管理与协作平台',
      time: new Date('2025-01-20'),
      color: '#10b981'
    },
    {
      name: '在线教育平台',
      icon: 'ph:student-duotone',
      message: 'edu.labycode.com',
      personal: '互动式在线学习管理系统',
      time: new Date('2025-02-12'),
      color: '#f59e0b'
    },
    {
      name: '客服工单系统',
      icon: 'ph:headset-duotone',
      message: 'support.labycode.com',
      personal: '智能客服与工单处理平台',
      time: new Date('2025-02-28'),
      color: '#ec4899'
    }
  ]
  projects = Object.assign(projects, data)
}

// 获取通知公告
let notice = reactive<Notice[]>([])
const getNotice = async () => {
  const data = [
    {
      title: '平台已集成GPT-4o模型，支持智能内容生成与分析',
      type: 'AI功能升级',
      keys: ['GPT-4o', '智能生成'],
      date: new Date('2025-03-15')
    },
    {
      title: '新增实时协作功能，团队成员可同步编辑文档',
      type: '功能更新',
      keys: ['实时协作', '同步编辑'],
      date: new Date('2025-03-10')
    },
    {
      title: '系统性能优化完成，页面加载速度提升60%',
      type: '性能优化',
      keys: ['性能', '60%'],
      date: new Date('2025-03-05')
    },
    {
      title: '移动端App已上线，支持iOS和Android双平台',
      type: '产品发布',
      keys: ['移动端', 'iOS', 'Android'],
      date: new Date('2025-02-28')
    }
  ]
  notice = Object.assign(notice, data)
}

// 获取快捷入口
let shortcut = reactive<Shortcut[]>([])
const getShortcut = async () => {
  const data = [
    {
      name: '仪表盘',
      icon: 'ph:gauge-duotone',
      url: '/',
      color: '#6366f1'
    },
    {
      name: '内容管理',
      icon: 'ph:notebook-duotone',
      url: '/content/articles',
      color: '#8b5cf6'
    },
    {
      name: '用户中心',
      icon: 'ph:users-three-duotone',
      url: '/users/list',
      color: '#06b6d4'
    },
    {
      name: '数据统计',
      icon: 'ph:chart-bar-duotone',
      url: '/statistics/overview',
      color: '#10b981'
    },
    {
      name: '系统设置',
      icon: 'ph:gear-duotone',
      url: '/system/settings',
      color: '#f59e0b'
    },
    {
      name: '消息通知',
      icon: 'ph:bell-ringing-duotone',
      url: '/messages/inbox',
      color: '#ec4899'
    }
  ]
  shortcut = Object.assign(shortcut, data)
}

// 用户来源
const getUserAccessSource = async () => {
  const data = [
    { value: 1856, name: 'analysis.searchEngines' },
    { value: 782, name: 'analysis.directAccess' },
    { value: 456, name: 'analysis.allianceAdvertising' },
    { value: 298, name: 'analysis.mailMarketing' },
    { value: 164, name: 'analysis.videoAdvertising' }
  ]
  set(
    pieOptionsData,
    'legend.data',
    data.map((v) => t(v.name))
  )
  pieOptionsData!.series![0].data = data.map((v) => {
    return {
      name: t(v.name),
      value: v.value
    }
  })
}

const barOptionsData = reactive<EChartsOption>(barOptions) as EChartsOption

// 周活跃量
const getWeeklyUserActivity = async () => {
  const data = [
    { value: 8642, name: 'analysis.monday' },
    { value: 12356, name: 'analysis.tuesday' },
    { value: 15234, name: 'analysis.wednesday' },
    { value: 18967, name: 'analysis.thursday' },
    { value: 16543, name: 'analysis.friday' },
    { value: 5621, name: 'analysis.saturday' },
    { value: 4893, name: 'analysis.sunday' }
  ]
  set(
    barOptionsData,
    'xAxis.data',
    data.map((v) => t(v.name))
  )
  set(barOptionsData, 'series', [
    {
      name: t('analysis.activeQuantity'),
      data: data.map((v) => v.value),
      type: 'bar'
    }
  ])
}

const getAllApi = async () => {
  await Promise.all([
    getCount(),
    getProject(),
    getNotice(),
    getShortcut(),
    getUserAccessSource(),
    getWeeklyUserActivity()
  ])
  loading.value = false
}

const handleProjectClick = (message: string) => {
  // 可以改为跳转到项目详情页面
  router.push(`/projects/detail?url=${encodeURIComponent(message)}`)
}

const handleShortcutClick = (url: string) => {
  router.push(url)
}

getAllApi()
</script>

<style lang="scss" scoped>
.home-dashboard {
  padding: 16px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);

  .dark & {
    background: #0f172a;
  }
}

// 欢迎区域
.welcome-section {
  margin-bottom: 16px;
}

.welcome-card {
  border-radius: 16px;
  overflow: hidden;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);

  :deep(.el-card__body) {
    padding: 32px;
  }

  .dark & {
    background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  }
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;

  @media (max-width: 1024px) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;

  .online-badge {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 16px;
    height: 16px;
    background: #10b981;
    border: 3px solid white;
    border-radius: 50%;
  }
}

.welcome-info {
  .welcome-title {
    font-size: 24px;
    font-weight: 700;
    color: white;
    margin: 0 0 8px 0;
    line-height: 1.3;
  }

  .welcome-subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
    margin: 0;
  }
}

.welcome-right {
  display: flex;
  gap: 32px;

  @media (max-width: 1024px) {
    width: 100%;
    justify-content: space-between;
  }

  @media (max-width: 640px) {
    flex-direction: column;
    gap: 16px;
  }
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  color: white;

  &.projects {
    background: rgba(255, 255, 255, 0.2);
  }

  &.todos {
    background: rgba(16, 185, 129, 0.3);
  }

  &.access {
    background: rgba(251, 191, 36, 0.3);
  }
}

.stat-content {
  .stat-label {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 4px;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 700;
    color: white;
  }
}

// 主内容区域
.main-content {
  margin-bottom: 16px;
}

// 卡片通用样式
:deep(.el-card) {
  border-radius: 12px;
  border: none;
  margin-bottom: 16px;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  .dark & {
    background: #1e293b;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -8px 0;

  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;

    .dark & {
      color: #f1f5f9;
    }
  }
}

// 快捷操作
.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;

  @media (max-width: 640px) {
    grid-template-columns: repeat(3, 1fr);
  }
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }

  .dark & {
    background: #0f172a;

    &:hover {
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
    }
  }

  .shortcut-icon {
    width: 56px;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    transition: all 0.3s ease;
  }

  .shortcut-name {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    text-align: center;

    .dark & {
      color: #e2e8f0;
    }
  }
}

// 项目列表
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.project-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  .dark & {
    background: #0f172a;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }

  .project-icon {
    width: 56px;
    height: 56px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
  }

  .project-info {
    flex: 1;
    min-width: 0;

    .project-name {
      font-size: 16px;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 8px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      .dark & {
        color: #f1f5f9;
      }
    }

    .project-desc {
      font-size: 13px;
      color: #6b7280;
      margin: 0 0 12px 0;
      line-height: 1.5;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;

      .dark & {
        color: #94a3b8;
      }
    }

    .project-footer {
      .project-time {
        font-size: 12px;
        color: #9ca3af;

        .dark & {
          color: #64748b;
        }
      }
    }
  }
}

// 图表卡片
.chart-card {
  :deep(.el-card__body) {
    padding: 24px;
  }
}

.chart-section {
  .chart-section-title {
    font-size: 15px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 20px 0;
    padding-left: 12px;
    border-left: 3px solid #6366f1;

    .dark & {
      color: #f1f5f9;
    }
  }
}

.full-height {
  height: calc(100% - 16px);
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    overflow-y: auto;
    padding: 0;

    /* 隐藏滚动条 */
    scrollbar-width: thin;
    scrollbar-color: rgba(99, 102, 241, 0.3) transparent;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(99, 102, 241, 0.3);
      border-radius: 3px;

      &:hover {
        background-color: rgba(99, 102, 241, 0.5);
      }
    }
  }
}

// 通知公告
.notice-card {
  :deep(.el-card__body) {
    padding: 0;
  }
}

.notice-list {
  .notice-item {
    display: flex;
    gap: 12px;
    padding: 16px 20px;
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      background: #f9fafb;

      .dark & {
        background: #0f172a;
      }
    }

    &:not(:last-child) {
      border-bottom: 1px solid #f3f4f6;

      .dark & {
        border-bottom-color: #334155;
      }
    }

    .notice-icon {
      width: 36px;
      height: 36px;
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 8px;
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      color: white;
    }

    .notice-content {
      flex: 1;
      min-width: 0;

      .notice-title {
        font-size: 14px;
        font-weight: 500;
        color: #1f2937;
        margin-bottom: 8px;
        line-height: 1.5;

        .dark & {
          color: #f1f5f9;
        }
      }

      .notice-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 12px;

        .notice-type {
          color: #6366f1;
          background: rgba(99, 102, 241, 0.1);
          padding: 2px 8px;
          border-radius: 4px;
        }

        .notice-date {
          color: #9ca3af;

          .dark & {
            color: #64748b;
          }
        }
      }
    }
  }
}
</style>

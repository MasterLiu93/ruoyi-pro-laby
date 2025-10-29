<!--
  库存预警列表页
  
  功能说明：
  1. 库存预警的查询功能
  2. 包含低库存预警和即将过期预警
  3. 实时显示预警信息
  4. 权限控制：wms:inventory-warning:query
  
  @author laby
  @date 2025-10-28
-->
<template>
  <ContentWrap>
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-gray-500 text-sm">低库存预警</div>
              <div class="text-2xl font-bold text-orange-500 mt-2">{{ lowStockCount }}</div>
            </div>
            <Icon icon="ep:warning" class="text-5xl text-orange-300" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-gray-500 text-sm">即将过期预警</div>
              <div class="text-2xl font-bold text-red-500 mt-2">{{ expiringCount }}</div>
            </div>
            <Icon icon="ep:clock" class="text-5xl text-red-300" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-gray-500 text-sm">总预警数</div>
              <div class="text-2xl font-bold text-blue-500 mt-2">{{ totalCount }}</div>
            </div>
            <Icon icon="ep:bell" class="text-5xl text-blue-300" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-row class="mb-3">
      <el-radio-group v-model="activeTab" @change="handleTabChange">
        <el-radio-button value="all">全部预警</el-radio-button>
        <el-radio-button value="lowStock">低库存预警</el-radio-button>
        <el-radio-button value="expiring">即将过期预警</el-radio-button>
      </el-radio-group>
      <el-button class="ml-3" @click="handleRefresh">
        <Icon icon="ep:refresh" class="mr-5px" />刷新
      </el-button>
    </el-row>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="预警类型" prop="warningType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.warningType === 'LOW_STOCK'" type="warning">低库存</el-tag>
          <el-tag v-else-if="scope.row.warningType === 'EXPIRING'" type="danger">即将过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="仓库" prop="warehouseName" width="120" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="scope">
          <div>{{ scope.row.skuCode }}</div>
          <div class="text-gray-500 text-xs">{{ scope.row.goodsName }}</div>
        </template>
      </el-table-column>
      <el-table-column label="批次号" prop="batchNo" width="140" />
      <el-table-column label="当前库存" width="120" align="right">
        <template #default="scope">
          <div>总量：{{ scope.row.quantity }}</div>
          <div class="text-gray-500 text-xs" v-if="scope.row.lockQuantity > 0">
            锁定：{{ scope.row.lockQuantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="可用数量" prop="availableQuantity" width="100" align="right">
        <template #default="scope">
          <span :class="scope.row.availableQuantity < (scope.row.safetyStock || 0) ? 'text-red-500 font-bold' : 'text-green-600'">
            {{ scope.row.availableQuantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="安全库存" prop="safetyStock" width="100" align="right">
        <template #default="scope">
          <span v-if="scope.row.safetyStock" class="text-blue-500">{{ scope.row.safetyStock }}</span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="过期信息" width="150">
        <template #default="scope">
          <div v-if="scope.row.expireDate">
            <div class="text-xs">{{ scope.row.expireDate }}</div>
            <el-tag v-if="scope.row.daysToExpire !== undefined" size="small" :type="scope.row.daysToExpire <= 3 ? 'danger' : 'warning'">
              {{ scope.row.daysToExpire }}天后过期
            </el-tag>
          </div>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="预警说明" min-width="200">
        <template #default="scope">
          <div v-if="scope.row.warningType === 'LOW_STOCK'" class="text-orange-600">
            可用库存（{{ scope.row.availableQuantity }}）低于安全库存（{{ scope.row.safetyStock }}），建议及时补货
          </div>
          <div v-else-if="scope.row.warningType === 'EXPIRING'" class="text-red-600">
            批次 {{ scope.row.batchNo }} 将在 {{ scope.row.daysToExpire }} 天后过期，请及时处理
          </div>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import * as InventoryWarningApi from '@/api/wms/inventory-warning'

defineOptions({ name: 'WmsInventoryWarning' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref([])
const activeTab = ref('all')

// 统计数据
const lowStockCount = ref(0)
const expiringCount = ref(0)
const totalCount = ref(0)

/**
 * 查询列表
 */
const getList = async () => {
  loading.value = true
  try {
    let data
    if (activeTab.value === 'lowStock') {
      data = await InventoryWarningApi.getLowStockWarnings()
    } else if (activeTab.value === 'expiring') {
      data = await InventoryWarningApi.getExpiringWarnings()
    } else {
      data = await InventoryWarningApi.getAllWarnings()
    }
    list.value = data
    
    // 更新统计数据
    if (activeTab.value === 'all') {
      lowStockCount.value = data.filter(item => item.warningType === 'LOW_STOCK').length
      expiringCount.value = data.filter(item => item.warningType === 'EXPIRING').length
      totalCount.value = data.length
    }
  } finally {
    loading.value = false
  }
}

/**
 * 切换标签
 */
const handleTabChange = () => {
  getList()
}

/**
 * 刷新
 */
const handleRefresh = () => {
  getList()
  message.success('刷新成功')
}

/**
 * 初始化
 */
onMounted(async () => {
  await getList()
})
</script>

